package cn.jiatp.security.session.filter;

import cn.jiatp.security.session.bean.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: LoginFilter
 * @Author: jiatp
 * @Date: 2020/12/10 16:56
 * @Description: 自定义过滤器代替 UsernamePasswordAuthenticationFilter
 * 实现登录Json的Post登录
 *
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    @Autowired
    SessionRegistry sessionRegistry;     //手动向SessionRegistryImpl 添加一条记录


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {

        //验证是否为post 请求
        if (!req.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + req.getMethod());
        }
        //从sessio中取出验证码
        String verify_code = (String)req.getSession().getAttribute("verify_code");
        //暂时写死
        if(verify_code == null){
            verify_code = "1234";
        }


        //判断请求参数是否为Json
        //前后端分离项目中 登录请求为post 参数为json,
        if (req.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) || req.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){

            Map<String, String> loginData = new HashMap<>(); //构建登录Map
            try {
                loginData = new ObjectMapper().readValue(req.getInputStream(), Map.class); //json信息存入map
            } catch (IOException e) {
            }finally {
                String code = loginData.get("code");
                if(code==null){
                    code = "1234";
                }
                checkCode(res, code, verify_code);  //校验验证码
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
            //构建UsernamePasswordAuthenticationToken对象
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);
            //验证
            setDetails(req, authRequest);

            //手工添加一条记录  sessionRegistry
            User u = new User();
            u.setUsername(username);
            sessionRegistry.registerNewSession(req.getSession(true).getId(),u);
            return this.getAuthenticationManager().authenticate(authRequest);


        } else {
            checkCode(res, req.getParameter("code"), verify_code);
            return super.attemptAuthentication(req, res);
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
