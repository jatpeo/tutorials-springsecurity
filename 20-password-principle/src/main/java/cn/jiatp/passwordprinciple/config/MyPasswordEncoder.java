package cn.jiatp.passwordprinciple.config;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * @ClassName: MyPasswordEncoder
 * @Author: jiatp
 * @Date: 2020/12/15 16:35
 * @Description: 密码加密方式一、codec 加密
 */
@Component
public class MyPasswordEncoder implements PasswordEncoder {

    /*
    * 密码加密
    * rawPassword 明文密码
     * */
    @Override
    public String encode(CharSequence rawPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
    }

    /*
    * 密码比对
    * rawPassword  用户登录时传入的密码
    * encodedPassword 相当于是加密后的密码（从数据库中查询而来）
     * */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()));
    }
}
