package com.jpp.demo.withjpa.controller;

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
    }     //只要认证就能成功

    @GetMapping("/login")
    public String hello(){
        return "hello hhh！";
    }

    /*
    * 第二个 /admin 接口，必须要用户名密码认证之后才能访问，如果用户是通过自动登录认证的，则必须重新输入用户名密码才能访问该接口。
    * */
    @GetMapping("/admin")
    public String admin2() {
        return "admin";
    }
    /*
    * 第三个 /rememberme 接口，必须是通过自动登录认证后才能访问，如果用户是通过用户名/密码认证的，则无法访问该接口。
     * */
    @GetMapping("/rememberme")
    public String rememberme() {
        return "rememberme";
    }
}
