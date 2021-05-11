package cn.jpps.security.config_12;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @ClassName: MyAuthenticationProvider
 * @Author: jiatp
 * @Date: 2020/12/2 13:55
 * @Description: 自定义AuthenticationProvider
 */
public class MyAuthenticationProvider_Details extends DaoAuthenticationProvider {


    //12 通过自定义MyWebAuthenticationDetails 实现验证码的比对
    //这里只是判断 isPassed()
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        if (!((MyWebAuthenticationDetails) authentication.getDetails()).isPassed()) {
            throw new AuthenticationServiceException("验证码错误");
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
