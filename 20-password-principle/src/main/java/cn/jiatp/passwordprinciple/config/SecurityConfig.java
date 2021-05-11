package cn.jiatp.passwordprinciple.config;

import cn.jiatp.passwordprinciple.bean.RespBean;
import cn.jiatp.passwordprinciple.filter.MyLoginFilter;
import cn.jiatp.passwordprinciple.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @Author: jiatp
 * @Date: 2020/12/15 16:58
 * @Description: 密码加密配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    UserService userService;

    @Resource
    DataSource dataSource;

    /*
     * 完成用户名的密码比对
     * */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    /**
     * @Author: jiatp
     * @Date: 2020/12/2 14:09
     * @Description: 加入自己定义的MyAuthenticationProvider
     */
    @Bean
    MyAuthenticationProvider myAuthenticationProvider(){
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        myAuthenticationProvider.setUserDetailsService(userDetailsService());
        return myAuthenticationProvider;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        ProviderManager manager = new ProviderManager(Arrays.asList(myAuthenticationProvider()));
        return manager;
    }

    /*
     * 基于数据库的认证模型
     * */
    @Bean
    protected UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        return manager;
    }
    /**
     * @Author: jiatp
     * @Date: 2020/12/15 17:31
     * @Description: 自定义filter
     * 前后端分离用
     */
   // @Bean
//    MyLoginFilter myLoginFilter() throws Exception {
//        MyLoginFilter loginFilter = new MyLoginFilter();
//        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request,
//                                                HttpServletResponse response,
//                                                Authentication authentication) throws IOException, ServletException {
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter out = response.getWriter();
//                //  Hr hr = (Hr) authentication.getPrincipal();
//                //   hr.setPassword(null);
//
//                RespBean ok = RespBean.build(200,"登录成功!",null); //登录成功
//                String s = new ObjectMapper().writeValueAsString(ok);
//                out.write(s);
//                out.flush();
//                out.close();
//
//            }
//        });
//        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
//            @Override
//            public void onAuthenticationFailure(HttpServletRequest request,
//                                                HttpServletResponse response,
//                                                AuthenticationException exception) throws IOException, ServletException {
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter out = response.getWriter();
//                RespBean respBean = RespBean.errorMsg(exception.getMessage());
//                if (exception instanceof LockedException) {
//                    respBean.setMsg("账户被锁定，请联系管理员!");
//                } else if (exception instanceof CredentialsExpiredException) {
//                    respBean.setMsg("密码过期，请联系管理员!");
//                } else if (exception instanceof AccountExpiredException) {
//                    respBean.setMsg("账户过期，请联系管理员!");
//                } else if (exception instanceof DisabledException) {
//                    respBean.setMsg("账户被禁用，请联系管理员!");
//                } else if (exception instanceof BadCredentialsException) {
//                    respBean.setMsg("用户名或者密码输入错误，请重新输入!");
//                }
//                out.write(new ObjectMapper().writeValueAsString(respBean));
//                out.flush();
//                out.close();
//            }
//        });
//        loginFilter.setAuthenticationManager(authenticationManagerBean());
//        loginFilter.setFilterProcessesUrl("/doLogin");
//        return loginFilter;
//
//    }
//    //配套上面 方法
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManager();
//    }
    /**
     * @Author: jiatp
     * @Date: 2020/12/15 17:03
     * @Description: 使用codec进行密码比对
     */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new MyPasswordEncoder();
       // return new BCryptPasswordEncoder();
    }


    /**
     * @Author: jiatp
     * @Date: 2020/12/15 17:03
     * @Description: 表单登录配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
               http
                .authorizeRequests()    //认证请求
                .antMatchers("/register", "/doRegister", "/login", "/doLogin").permitAll()     //除了***能够无认证访问
                .anyRequest().authenticated()  //任何请求都需要认证
                .and()
                .formLogin()
                .loginPage("/login").loginProcessingUrl("/doLogin")
                       .successHandler(new AuthenticationSuccessHandler() {
                           @Override
                           public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                               response.setContentType("text/html; charset=utf-8");
                               PrintWriter out = response.getWriter();
                               out.write("成功!");
                               out.flush();
                               out.close();
                           }
                       })
                       .failureHandler(new AuthenticationFailureHandler() {
                           @Override
                           public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                               response.setContentType("text/html; charset=utf-8");
                               PrintWriter out = response.getWriter();
                               out.print(exception.getMessage());
                               out.write("失败!");
                               out.flush();
                               out.close();
                           }
                       })
                .and()
                .csrf().disable();     //CSRF跨站请求伪造直接关闭
    }

    /**
     * @ClassName: SecurityConfig
     * @Author: jiatp
     * @Date: 2020/12/15 17:06
     * @Description: 静态资源配置
     */

    @Override
    public void configure(WebSecurity web) throws Exception {
      web.ignoring().antMatchers("/js/**","/css/**","/image/**");
    }
}
