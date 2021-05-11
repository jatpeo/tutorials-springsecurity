package com.jpp.demo.withjpa.dao;

import com.jpp.demo.withjpa.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDao extends JpaRepository<User,Long> {
    User findUserByUsername(String username);

}
