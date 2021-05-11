package cn.jiatp.security.sessioncommon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @ClassName: HelloController
 * @Author: jiatp
 * @Date: 2020/12/14 15:52
 * @Description:  Spring Session 共享研究方案
 */
@RestController
public class HelloController {

    @Value("${server.port}")
    private Integer port;

    @GetMapping("/set")
    public String set(HttpSession session) {
        session.setAttribute("user", "jtp");
        return String.valueOf(port);
    }
    @GetMapping("/get")
    public String get(HttpSession session) {
        return session.getAttribute("user") + ":" + port;
    }

}
