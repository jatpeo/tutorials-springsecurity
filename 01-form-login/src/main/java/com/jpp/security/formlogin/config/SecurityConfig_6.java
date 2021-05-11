package com.jpp.security.formlogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpp.security.formlogin.bean.Hr;
import com.jpp.security.formlogin.bean.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

/*
* 演示Spring Security 如何将用户数据存入数据库
 * */
@EnableWebSecurity
public class SecurityConfig_6 extends WebSecurityConfigurerAdapter {

    @Resource
    DataSource dataSource;

    /*
    * 注册自定义的Filter
    * */
    @Bean
    LoginFilter loginFilter() throws Exception{
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
              //  Hr hr = (Hr) authentication.getPrincipal();
              //   hr.setPassword(null);
                Hr hr = new Hr("lisi","123");
                JSONResult ok = JSONResult.build(200,"登录成功!", hr); //登录成功
                String s = new ObjectMapper().writeValueAsString(ok);
                out.write(s);
                out.flush();
                out.close();

            }
        });
        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                JSONResult respBean = JSONResult.errorMsg(exception.getMessage());
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
        return loginFilter;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /*
    * PasswordEncoder  1、密码加密encode()   2、密码比对matches()  3、很多实现类
    * */
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();  //暂时用明文
    }

    /*
    * 基于内存配置测试用户  第一种方式
    * */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("jiatp")
//                .roles("admin")
//                .password("123456")
//                .and()
//                .withUser("jpp")
//                .roles("user")
//                .password("123123");
//    }
    /*
     * 基于数据库的认证模型
     * */
    @Bean
    protected UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        if (!manager.userExists("jtp")) {
            manager.createUser(User.withUsername("jtp").password("123").roles("admin").build());
        }
        if (!manager.userExists("jpp")) {
            manager.createUser(User.withUsername("jpp").password("123").roles("user").build());
        }
        return manager;
    }


    /*
    * 角色继承
    * */
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //默认的登录请求请求方式
//        http.authorizeRequests() ////开启登录验证
//                .anyRequest()
//                .authenticated()  //任何请求都要验证
//                .and()
//                .formLogin()
//                .loginPage("/login.html")//登录页面 默认情况下 登录的页面和登录接口是一样的 login.html中的表单提交的action也是login.html
//                .loginProcessingUrl("/doLogin")  //这个是配置登录post请求验证
//                .usernameParameter("username")
//                .passwordParameter("password")
//                .defaultSuccessUrl("/test") //登录成功重定向的url
//                .permitAll()
//                .and()
//                .csrf().disable(); //关闭跨站请求伪造
        //使用自定义过滤器   匹配路劲从上往下匹配
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and().csrf().disable();
       // http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**"); //不拦截静态资源

    }
}
