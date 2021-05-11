package cn.jiatp.passwordprinciple.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MyLoginFilter
 * @Author: jiatp
 * @Date: 2020/12/15 17:24
 * @Description: 定义自定义过滤器 实现登录为json登录 post
 */
public class MyLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //验证是否为post 请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //从sessio中取出验证码
        String verify_code = (String)request.getSession().getAttribute("verify_code");
        //暂时写死
        if(verify_code == null){
            verify_code = "1234";
        }

        //判断请求参数是否为Json
        //前后端分离项目中 登录请求为post 参数为json,
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) ||
                request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){

            Map<String, String> loginData = new HashMap<>(); //构建登录Map
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class); //json信息存入map
            } catch (IOException e) {
            }finally {
                String code = loginData.get("code");
                if(code==null){
                    code = "1234";
                }
                checkCode(response, code, verify_code);  //校验验证码
            }
            //取出 username password
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

        } else {
            checkCode(response, request.getParameter("code"), verify_code);
            return super.attemptAuthentication(request, response);
        }
    }

    /**
     * @Author: jiatp
     * @Date: 2020/12/11 12:55
     * @Description: 校验验证码
     */
    public void checkCode(HttpServletResponse resp, String code, String verify_code) {
        if (code == null || verify_code == null || "".equals(code) || !verify_code.toLowerCase().equals(code.toLowerCase())) {
            //验证码不正确
            throw new AuthenticationServiceException("验证码不正确");
        }
    }
}
