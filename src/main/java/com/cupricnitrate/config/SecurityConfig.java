package com.cupricnitrate.config;

import com.cupricnitrate.config.property.TokenProperties;
import com.cupricnitrate.filter.JwtAuthenticationFilter;
import com.cupricnitrate.filter.JwtVerifyFilter;
import com.cupricnitrate.service.UserDetailsPasswordSerivceImpl;
import com.cupricnitrate.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * `@EnableWebSecurity` 注解 deug参数为true时，开启调试模式，会有更多的debug输出
 * 启用`@EnableGlobalMethodSecurity(prePostEnabled = true)`注解后即可使用方法级安全注解
 * 方法级安全注解：
 * pre : @PreAuthorize（执行方法之前授权）  @PreFilter（执行方法之前过滤）
 * post : @PostAuthorize （执行方法之后授权） @ PostFilter（执行方法之后过滤）
 *
 * @author 硝酸铜
 * @date 2021/9/22
 */
@EnableWebSecurity(debug = true)
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private TokenProperties tokenProperties;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Resource
    private UserDetailsPasswordSerivceImpl userDetailsPasswordSerivce;

    @Resource
    private PasswordEncoder passwordEncoder;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //禁用生成默认的登陆页面
                .formLogin(AbstractHttpConfigurer::disable)
                //关闭httpBasic，采用自定义过滤器
                .httpBasic(AbstractHttpConfigurer::disable)
                //前后端分离架构不需要csrf保护，这里关闭
                .csrf(AbstractHttpConfigurer::disable)
                //禁用生成默认的注销页面
                .logout(AbstractHttpConfigurer::disable)
                .authorizeRequests(req -> req
                        //允许访问authorize url下的所有接口
                        .antMatchers("/authorize/**").permitAll()
                        .anyRequest().authenticated()
                )
                //添加我们自定义的过滤器，替代UsernamePasswordAuthenticationFilter
                .addFilterAt(new JwtAuthenticationFilter(authenticationManager(),tokenProperties), UsernamePasswordAuthenticationFilter.class)
                //添加token检验过滤器
                .addFilter(new JwtVerifyFilter(authenticationManager(),tokenProperties))
                //前后端分离是无状态的，不用session了，直接禁用。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/error",
                        "/resources/**",
                        "/static/**",
                        "/public/**",
                        "/h2-console/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v2/api-docs/**",
                        "/doc.html",
                        "/swagger-resources/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /**
     * 配置 DaoAuthenticationProvider
     * @return DaoAuthenticationProvider
     */
    private DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // 配置 AuthenticationManager 使用 userService
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        // 配置密码自动升级服务
        daoAuthenticationProvider.setUserDetailsPasswordService(userDetailsPasswordSerivce);
        // 密码编码器
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){

        //默认编码算法的Id,新的密码编码都会使用这个id对应的编码器
        String idForEncode = "bcrypt";
        //要支持的多种编码器
        //举例：历史原因，之前用的SHA-1编码，现在我们希望新的密码使用bcrypt编码
        //老用户使用SHA-1这种老的编码格式，新用户使用bcrypt这种编码格式，登录过程无缝切换
        Map encoders = new HashMap();
        encoders.put(idForEncode,new BCryptPasswordEncoder());

        //（默认编码器id，编码器map）
        return new DelegatingPasswordEncoder(idForEncode,encoders);
    }
}
