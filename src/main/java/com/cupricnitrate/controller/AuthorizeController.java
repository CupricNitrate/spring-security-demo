package com.cupricnitrate.controller;

import com.cupricnitrate.http.req.RefreshTokenReqDto;
import com.cupricnitrate.http.req.UserDto;
import com.cupricnitrate.http.resp.LoginRespDto;
import com.cupricnitrate.service.TokenService;
import com.cupricnitrate.service.UserDetailsServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 匿名用户
 * @author 硝酸铜
 * @date 2021/9/22
 */
@RestController
@RequestMapping("/authorize")
public class AuthorizeController {
    @Resource
    private TokenService tokenService;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    /**
     * 注册
     * @param userDto 请求体
     * @return UserDto
     */
    @PostMapping("/register")
    public boolean register(@Valid @RequestBody UserDto userDto){
        return userDetailsService.register(userDto);
    }

    /**
     * 刷新访问令牌
     * @param req 请求体
     * @param authorization 访问令牌
     * @return LoginRespDto
     */
    @PostMapping(value = "/refreshToken")
    public LoginRespDto refreshToken(@Validated @RequestBody RefreshTokenReqDto req, @RequestHeader(name = "Authorization") String authorization){
        return tokenService.refreshToken(authorization.replaceFirst("Bearer ", ""),req.getRefreshToken());
    }



}
