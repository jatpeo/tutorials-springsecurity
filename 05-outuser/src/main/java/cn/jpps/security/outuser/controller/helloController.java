package cn.jpps.security.outuser.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class helloController {

    @PostMapping("/hello")
    public String hello(){
        return "hello security!";
    }


//    @PostMapping("/login")
//    public  String doLogin(){
//        System.out.println("hello");
//        System.out.println("world");
//        return "hello dologin";
//    }

    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }

    @GetMapping("/user/hello")
    public String user() {
        return "user";
    }
}
