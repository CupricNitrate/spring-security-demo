package com.cupricnitrate.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 硝酸铜
 * @date 2021/6/2
 */
@RestController
@RequestMapping(value = "/api")
public class UserResource {

    @PreAuthorize("hasAuthority('api:hello')")
    @GetMapping(value = "/hello")
    public String getHello(){
        return "Hello ";
    }

    @PreAuthorize("hasAuthority('user:name')")
    @GetMapping(value = "/users")
    public String getCurrentUsername(){
        return "Hello " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
