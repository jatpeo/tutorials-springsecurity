package com.jpp.security.formlogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpp.security.formlogin.bean.Hr;
import com.jpp.security.formlogin.bean.JSONResult;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
* 演示了基本配置、表单登录配置、json格式登录
* */
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {



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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //基于内存的
        auth.inMemoryAuthentication()
                .withUser("jiatp")
                .roles("admin")
                .password("123456")
                .and()
                .withUser("jpp")
                .roles("user")
                .password("123123");
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
        //使用自定义过滤器
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and().csrf().disable();
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**"); //不拦截静态资源

    }
}
