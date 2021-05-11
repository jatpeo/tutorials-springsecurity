package com.jpp.demo.withjpa.config;

import com.jpp.demo.withjpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.sql.DataSource;

/*
* 09  自动登录的安全风险处理方案
*       1、持久化令牌
*       2、二次校验
* */
@Configuration
public class SecurityConfig_9 extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource; //数据源

    @Autowired
    UserService  userService;

    /*
     * PasswordEncoder  1、密码加密encode()   2、密码比对matches()  3、很多实现类
     * */
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();  //暂时用明文
    }

    /*
    * 基于JDBC永久登录令牌仓库实现。
    * */

    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    /*
    * 表单登录方式
    * 1 、持久化令牌方式
    * */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .and()
//                .rememberMe()
//                .key("jiatp")
//                .tokenRepository(jdbcTokenRepository())
//                .and()
//                .csrf()
//                .disable();
//    }

    /*
    * 自动登录  --》 二次校验
    * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/rememberme").rememberMe()
                .antMatchers("/admin").fullyAuthenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .rememberMe()
                .key("javaboy")
                .tokenRepository(jdbcTokenRepository())
                .and()
                .csrf().disable();
    }
    /*
    * 基于数据库
    * */

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(userService);
    }

    /*
    * web资源的访问
    * */

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**"); //不拦截静态资源

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
}
