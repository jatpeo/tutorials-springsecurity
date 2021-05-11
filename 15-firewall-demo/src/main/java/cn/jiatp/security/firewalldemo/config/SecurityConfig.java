package cn.jiatp.security.firewalldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * @Author: jiatp
 * @Date: 2020/12/14 11:32
 * @Description: 演示springSecurity中FireWall配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPeriod(true);
        return firewall;
    }

    /*
    *% 不被允许
    * */

    @Bean
    HttpFirewall httpFirewall3() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        return firewall;
    }

    /*
    * 双斜杠不被允许
    * */

    @Bean
    HttpFirewall httpFirewall2() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }
    /*
    * 必须是可打印的 ASCII 字符
    * */



   /**
    * 必须是标准化 URL
    * 请求地址必须是标准化 URl
    * 请求路径不能包含"./", "/../" or "/."
    * 源代码如下
    */
//   private static boolean isNormalized(HttpServletRequest request) {
//       if (!isNormalized(request.getRequestURI())) {
//           return false;
//       }
//       if (!isNormalized(request.getContextPath())) {
//           return false;
//       }
//       if (!isNormalized(request.getServletPath())) {
//           return false;
//       }
//       if (!isNormalized(request.getPathInfo())) {
//           return false;
//       }
//       return true;
//   }

    /**
     * 2.请求地址不能有分号
     * 同时配置 mvc配置  public void configurePathMatch(PathMatchConfigurer configurer)
     * */

//    @Bean
//    HttpFirewall httpFirewall2() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        firewall.setAllowSemicolon(true);
//        return firewall;
//    }
    /**
     * 1.只允许白名单中的方法
     * */
//    @Bean
//    HttpFirewall httpFirewall1() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        firewall.setUnsafeAllowAnyHttpMethod(true);
//        return firewall;
//    }

}
