package cn.jiatp.passwordprinciple;

import cn.jiatp.passwordprinciple.bean.User;
import cn.jiatp.passwordprinciple.config.MyPasswordEncoder;
import cn.jiatp.passwordprinciple.dao.UserDao;
import cn.jiatp.passwordprinciple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: jiatp
 * @Date: 2020/12/15 16:44
 * @Description: controller
 */
@Controller
public class HelloController {

    @Autowired
    MyPasswordEncoder passwordEncoder;

    @Resource
    UserService userService;

    @ResponseBody
    @PostMapping("/doRegister")
    public String reg(@RequestParam("username") String username,
                      @RequestParam("password") String password){

        //加密
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userService.saveUser(user);
        return "success";
    }

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "loginPage";
    }

    @GetMapping("/register")
    public String register() {
        return "registerPage";
    }
}
