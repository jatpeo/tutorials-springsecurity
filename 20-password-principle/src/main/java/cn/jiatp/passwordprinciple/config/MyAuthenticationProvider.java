package cn.jiatp.passwordprinciple.config;

import cn.jiatp.passwordprinciple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName: MyAuthenticationProvider
 * @Author: jiatp
 * @Date: 2020/12/16 11:44
 * @Description: 实现密码的自定义校验逻辑
 */

public class MyAuthenticationProvider extends DaoAuthenticationProvider {

    @Resource
    MyPasswordEncoder mypasswordEncoder;

    @Autowired
    UserService userService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("用户名密码错误");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "用户名密码错误"));
        } else {
            //得到密码
            String presentedPassword = authentication.getCredentials().toString();
            UserDetails _userDetails = userService.loadUserByUsername(authentication.getName());
            //密码比对
            if (!this.mypasswordEncoder.matches(presentedPassword, _userDetails.getPassword())) {
                this.logger.debug("密码错误");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "密码错误"));
            }
        }
    }
}
