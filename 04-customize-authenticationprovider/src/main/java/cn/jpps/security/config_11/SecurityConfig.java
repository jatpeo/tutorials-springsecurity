package cn.jpps.security.config_11;



import cn.jpps.security.util.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

/**
 * @ClassName: SecurityConfig
 * @Author: jiatp
 * @Date: 2020/12/2 11:49
 * @Description: Part 11、实现自定义认证逻辑
 * 登录请求是调用 AbstractUserDetailsAuthenticationProvider#authenticate 方法进行认证的，
 * 在该方法中，又会调用到 DaoAuthenticationProvider#additionalAuthenticationChecks 方法做进一步的校验，
 * 去校验用户登录密码。我们可以自定义一个 AuthenticationProvider 代替 DaoAuthenticationProvider，
 * 并重写它里边的 additionalAuthenticationChecks 方法，在重写的过程中，加入验证码的校验逻辑即可。
 */
//@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * @Description: 个实体类用来描述验证码的基本信息
     */
    @Bean
    public Producer verifyCode(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
    /**
     * @Author: jiatp
     * @Date: 2020/12/2 14:06
     * @Description: 密码认证方式
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
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

    /**
     * @Author: jiatp
     * @Date: 2020/12/2 14:20
     * @Description: 提供自定义的 AuthenticationManager
     */
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        ProviderManager providerManager = new ProviderManager(Arrays.asList(myAuthenticationProvider()));
        return providerManager;
    }

    /**
     * @Author: jiatp
     * @Date: 2020/12/2 14:11
     * @Description: 基于内存的认证方式
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("jiatp").password("123").roles("admin").build());
        return manager;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/vc.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler((req, resp, auth) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.ok("success", auth.getPrincipal())));
                    out.flush();
                    out.close();
                })
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.errorMsg(e.getMessage())));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable();
    }
}
