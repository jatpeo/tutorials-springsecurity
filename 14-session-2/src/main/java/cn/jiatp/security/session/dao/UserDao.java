package cn.jiatp.security.session.dao;

import cn.jiatp.security.session.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName: UserDao
 * @Author: jiatp
 * @Date: 2020/12/15 17:23
 * @Description: userDao
 */
public interface UserDao extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
}
