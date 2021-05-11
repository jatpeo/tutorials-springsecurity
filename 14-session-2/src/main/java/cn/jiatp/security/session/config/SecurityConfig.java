package cn.jiatp.security.session.config;

import cn.jiatp.security.session.bean.Hr;
import cn.jiatp.security.session.bean.RespBean;
import cn.jiatp.security.session.bean.User;
import cn.jiatp.security.session.filter.LoginFilter;
import cn.jiatp.security.session.sevice.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName: SecurityConfig
 * @Author: jiatp
 * @Date: 2020/12/10 16:00
 * @Description: Spring Boot + Vue 前后端分离项目，如何踢掉已登录用户
 * www.jiiatp.cn
 */
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    UserService userService;

    @Resource
    DataSource dataSource;





    /*
     * password密码管理期
     * */
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /*
     * web过滤器
     * */

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    /*
     *
     * 内存用户支持  这里只是看一下User对象是怎么保持一致性（重写hashCode()、equals（））
     * */
//    @Bean
//    UserDetailsService userDetailsService(){
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser();
//        return manager;
//    }SessionRegistryImpl
    //这里演示的是基于数据库的 因此也要保证自定义User的一致性
    @Bean
    protected UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        return manager;
    }

    /*
     * 完成用户名的密码比对
     * */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    /*
     * 表单登录的
     *  //使用自定义过滤器   匹配路劲从上往下匹配
     * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //使用自定义过滤器   匹配路劲从上往下匹配
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/index.html")
                .loginProcessingUrl("/doLogin") // 指定处理登录请求的路径
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
                        res.setContentType("application/json;charset=utf-8");
                        PrintWriter out = res.getWriter();
                        String name = authentication.getName();

                        System.out.println("success");
                        out.print("{'msg':'你好啊','name':"+name+"',code:200}");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception) throws IOException, ServletException {
                        res.setContentType("application/json;charset=utf-8");
                        PrintWriter out = res.getWriter();
                        out.print("{'msg':'登录失败了',code:500}");
                    }
                })
                .permitAll() // 使登录页不设限访问
                .and()
                .rememberMe()
                .and()
                .csrf().disable();
        log.info("Loginfilter.........");
        // 配置ConcurrentSessionFilter
        http.addFilterAt(new ConcurrentSessionFilter(sessionRegistry(), event -> {
            HttpServletResponse resp = event.getResponse();
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(401);
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(RespBean.error("您已在另一台设备登录，本次登录已下线!")));
            out.flush();
            out.close();
        }), ConcurrentSessionFilter.class);
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * @Author: jiatp
     * @Date: 2020/12/11 13:03
     * @Description: 注入自定义的LoginFilter  替代UsernamePasswordAuthenticationFilter
     */

    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();

        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {

                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                User user = (User) authentication.getPrincipal();
              //  user.setPassword(null);
                RespBean ok = RespBean.ok("登录成功!", user);
                String s = new ObjectMapper().writeValueAsString(ok);
                out.write(s);
                out.flush();
                out.close();

            }
        });
        //失败情况
        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                RespBean respBean = RespBean.error(exception.getMessage());
                if (exception instanceof LockedException) {
                    respBean.setMsg("账户被锁定，请联系管理员!");
                } else if (exception instanceof CredentialsExpiredException) {
                    respBean.setMsg("密码过期，请联系管理员!");
                } else if (exception instanceof AccountExpiredException) {
                    respBean.setMsg("账户过期，请联系管理员!");
                } else if (exception instanceof DisabledException) {
                    respBean.setMsg("账户被禁用，请联系管理员!");
                } else if (exception instanceof BadCredentialsException) {
                    respBean.setMsg("用户名或者密码输入错误，请重新输入!");
                }
                out.write(new ObjectMapper().writeValueAsString(respBean));
                out.flush();
                out.close();
            }
        });
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setFilterProcessesUrl("/doLogin");
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        loginFilter.setSessionAuthenticationStrategy(sessionStrategy);
        return loginFilter;

    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * @Author: jiatp
     * @Date: 2020/12/11 15:12
     * @Description: todo
     * 这里我们要自己提供 SessionAuthenticationStrategy，而前面处理 session 并发的是 ConcurrentSessionControlAuthenticationStrategy，
     * 也就是说，我们需要自己提供一个 ConcurrentSessionControlAuthenticationStrategy 的实例，然后配置给 LoginFilter，
     * 但是在创建 ConcurrentSessionControlAuthenticationStrategy 实例的过程中，还需要有一个 SessionRegistryImpl
     * SessionRegistryImpl 维护session信息
     */

    @Bean
    SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
