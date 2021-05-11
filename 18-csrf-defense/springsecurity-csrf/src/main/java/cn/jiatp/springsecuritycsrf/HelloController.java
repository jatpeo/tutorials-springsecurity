package cn.jiatp.springsecuritycsrf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: HelloController
 * @Author: jiatp
 * @Date: 2020/12/15 10:05
 * @Description: spring security演示csrf
 */
@Controller
public class HelloController {

    @ResponseBody
    @PostMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/hello")
    public String hello2() {
        return "hello";
    }
}
