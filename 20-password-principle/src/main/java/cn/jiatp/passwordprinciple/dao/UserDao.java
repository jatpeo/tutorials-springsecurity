package cn.jiatp.passwordprinciple.dao;

import cn.jiatp.passwordprinciple.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,Long> {

    User findUserByUsername(String username);
}
