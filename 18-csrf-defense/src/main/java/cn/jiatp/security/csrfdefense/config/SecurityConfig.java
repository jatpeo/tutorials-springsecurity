package cn.jiatp.security.csrfdefense.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Author: jiatp
 * @Date: 2020/12/14 17:46
 * @Description: 演示 SpringSecurity 如何防御csrf攻击
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * csdf 配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests().anyRequest()
               .authenticated()
               .and()
               .formLogin()
               .and()
               .csrf().disable();  //先关闭防御csrf攻击
    }

    /**
     * webmvc 配置
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }
}
