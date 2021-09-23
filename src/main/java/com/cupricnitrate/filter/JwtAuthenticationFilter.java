package com.cupricnitrate.filter;

import com.cupricnitrate.config.property.TokenProperties;
import com.cupricnitrate.http.req.LoginReqDto;
import com.cupricnitrate.http.resp.LoginRespDto;
import com.cupricnitrate.model.Authority;
import com.cupricnitrate.model.ClaimInfo;
import com.cupricnitrate.util.JwtUtils;
import com.cupricnitrate.util.RsaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Jwt认证过滤器
 * @author 硝酸铜
 * @date 2021/9/22
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final TokenProperties tokenProperties;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,TokenProperties tokenProperties) {
        this.authenticationManager = authenticationManager;
        this.tokenProperties = tokenProperties;
        // 浏览器访问 /authorize/login 会通过 JWTAuthenticationFilter
        setFilterProcessesUrl("/authorize/login");
    }

    /**
     * json格式：
     *
     * {
     *     "username": "user",
     *     "password": "12345678"
     * }
     *
     * @param request 请求体
     * @param response 返回体
     * @return Authentication
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        InputStream is = null;
        LoginReqDto req = null;
        try {
            //从Body中读取参数
            is = request.getInputStream();
            //使用jackson解析json
            ObjectMapper objectMapper = new ObjectMapper();
            req = objectMapper.readValue(is,LoginReqDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadCredentialsException("json格式错误，没有找到用户名或密码");
        }

        //认证，同父类，生成一个没有被完全初始化的Authentication
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
        this.setDetails(request, authRequest);
        return authenticationManager.authenticate(authRequest);
    }


    /**
     * 认证成功逻辑
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //令牌私钥
        PrivateKey accessPrivateKey = null;
        PrivateKey refreshPrivateKey = null;
        try {
            accessPrivateKey = RsaUtils.getPrivateKey(tokenProperties.getAccess().getPrivateKey());
            refreshPrivateKey = RsaUtils.getPrivateKey(tokenProperties.getRefresh().getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //创建荷载信息
        List<ClaimInfo.ClaimAuthority> authorities = authResult.getAuthorities().stream().map(a -> {
            ClaimInfo.ClaimAuthority claimAuthority = new ClaimInfo.ClaimAuthority();
            claimAuthority.setAuthority(a.getAuthority());
            return claimAuthority;
        }).collect(Collectors.toList());

        ClaimInfo claim = ClaimInfo.builder().username(authResult.getName()).authorities(authorities).build();
        //签发token,使用私钥进行签发
        LoginRespDto respDto = new LoginRespDto(
                JwtUtils.generateTokenExpire(claim, accessPrivateKey,tokenProperties.getAccess().getExpireTime(), JwtUtils.createJTI()),
                JwtUtils.generateTokenExpire(claim, refreshPrivateKey,tokenProperties.getRefresh().getExpireTime(),JwtUtils.createJTI()));

        try {
            //登录成功時，返回json格式进行提示
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            Map<String, Object> map = new HashMap<>();
            map.put("code", HttpServletResponse.SC_OK);
            map.put("message", "登陆成功！");
            map.put("token",respDto);
            out.write(new ObjectMapper().writeValueAsString(map));
            out.flush();
            out.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 登录失败逻辑
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        try {
            //登录成功時，返回json格式进行提示
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            Map<String, Object> map = new HashMap<>();
            map.put("code", HttpServletResponse.SC_FORBIDDEN);
            map.put("message", "登陆失败！");
            map.put("reason",failed.getMessage());
            out.write(new ObjectMapper().writeValueAsString(map));
            out.flush();
            out.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


}
