package com.jpp.security.formlogin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }

    @GetMapping("/user/hello")
    public String user() {
        return "user";
    }

    @GetMapping("/doLogin")
    public String doLogin(){
        return "你好 spring security";
    }

    @GetMapping("/hello")
    public String test(){
        return "hello！";
    }

    @GetMapping("/login")
    public String hello(){
        return "hello hhh！";
    }


}
