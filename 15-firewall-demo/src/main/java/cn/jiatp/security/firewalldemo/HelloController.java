package cn.jiatp.security.firewalldemo;

import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.scope.MethodScope;

@RestController
public class HelloController {


    @RequestMapping(value = "/hello/{id}")
    public String hello(@PathVariable Integer id, @MatrixVariable String name) {
        System.out.println("id = " + id);
        System.out.println("name = " + name);
        String msg = id+","+name;
        return msg;
    }
}
