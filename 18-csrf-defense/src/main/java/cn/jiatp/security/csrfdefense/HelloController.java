package cn.jiatp.security.csrfdefense;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName: HelloController
 * @Author: jiatp
 * @Date: 2020/12/15 10:06
 * @Description: 演示什么是csrf
 * 分别启动两个服务8080 和 8081
 */
@RestController
public class HelloController {

    /**
     * 转账接口
     */
    @PostMapping("/transfer")
    public void transferMoney(String name, Integer money) {
        System.out.println("name = " + name);
        System.out.println("money = " + money);
    }
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
