package cn.jpps.security.config_12;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: jiatp
 * @Date: 2020/12/2 15:01
 * @Description: 12 定义WebAuthenticationDetailsSource 为了扩展WebAuthenticationDetails
 */
@Component
public class MyWebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest,
        MyWebAuthenticationDetails> {

    @Override
    public MyWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new MyWebAuthenticationDetails(context);   //返回自己定义的MyWebAuthenticationDetails
    }
}
