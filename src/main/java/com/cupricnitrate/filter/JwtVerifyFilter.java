package com.cupricnitrate.filter;

import com.cupricnitrate.config.property.TokenProperties;
import com.cupricnitrate.model.ClaimInfo;
import com.cupricnitrate.model.Payload;
import com.cupricnitrate.util.JwtUtils;
import com.cupricnitrate.util.RsaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 硝酸铜
 * @date 2021/9/22
 */
public class JwtVerifyFilter extends BasicAuthenticationFilter {

    private TokenProperties tokenProperties;

    public JwtVerifyFilter(AuthenticationManager authenticationManager, TokenProperties tokenProperties) {
        super(authenticationManager);
        this.tokenProperties = tokenProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (checkJwtToken(request)) {
            try {
                //获取权限失败，会抛出异常
                UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
                //获取后，将Authentication写入SecurityContextHolder中供后序使用
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } catch (Exception e) {
                responseJson(response);
                e.printStackTrace();
            }
        } else {
            //token不在请求头中，则说明是匿名用户访问
            List<GrantedAuthority> list = new ArrayList<>();
            GrantedAuthority grantedAuthority = () -> "ROLE_ANONYMOUS";
            list.add(grantedAuthority);
            AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("token-anonymousUser","anonymousUser", list);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }


    /**
     * 检查JWT Token 是否在HTTP 报头中
     *
     * @param request HTTP请求
     * @return boolean
     */
    private boolean checkJwtToken(HttpServletRequest request) {
        String header = request.getHeader(tokenProperties.getHeader());
        return header != null && header.startsWith(tokenProperties.getPrefix());
    }

    /**
     * 未登录提示
     *
     * @param response
     */
    private void responseJson(HttpServletResponse response) {
        try {
            //未登录提示
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", HttpServletResponse.SC_FORBIDDEN);
            map.put("message", "未登录或登录过期，请进行登录!");
            out.write(new ObjectMapper().writeValueAsString(map));
            out.flush();
            out.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 通过token，获取用户信息
     *
     * @param request
     * @return
     */
    @SneakyThrows
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        //读取请求头中的Authorization的值
        String token = request.getHeader("Authorization");
        if (token != null) {
            //Authorization 中JWT传参，默认格式 Authorization:Bearer XXX
            token = token.replaceFirst(tokenProperties.getPrefix(), "");
            //通过token解析出载荷信息，使用公钥进行解析
            Payload<ClaimInfo> payload = JwtUtils.getInfoFromToken(token, RsaUtils.getPublicKey(tokenProperties.getAccess().getPublicKey()), ClaimInfo.class);
            ClaimInfo claimInfo = payload.getUserInfo();
            //不为null，返回一个完全初始化的Authentication
            if (claimInfo != null) {
                return new UsernamePasswordAuthenticationToken(claimInfo.getUsername(), null, claimInfo.getAuthorities());
            }
            return null;

        }
        return null;
    }
}
