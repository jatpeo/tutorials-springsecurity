package com.jpp.security.formlogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//自定义表单登录过滤器
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    //默认登录为Post请求，格式为Key-Value
    //自定义过滤器 登录为Post请求 格式为json格式
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //判断是否为Post请求
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String verify_code = (String)request.getSession().getAttribute("verify_code"); //从session中取出要验证码
        //
        verify_code = "1234";
        //判断登录是否为json请求
        if(request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) ||
                request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){
            Map<String, String> loginData = new HashMap<>();

            try {
                //是json 请求将request中信息通过IO映射到Map上
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                //得到用户输入的验证码
                String code = loginData.get("code");
                //去校验
                checkCode(response, code, verify_code);
            }
            String username = loginData.get(getUsernameParameter());
            String password = loginData.get(getPasswordParameter());
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);

        }else{
            checkCode(response, request.getParameter("code"), verify_code);
            return super.attemptAuthentication(request, response);
        }



    }
    //校验验证码
    public void checkCode(HttpServletResponse resp, String code, String verify_code) {
        if (code == null || verify_code == null || "".equals(code) || !verify_code.toLowerCase().equals(code.toLowerCase())) {
            //验证码不正确
            throw new AuthenticationServiceException("验证码不正确");
        }
    }
}
