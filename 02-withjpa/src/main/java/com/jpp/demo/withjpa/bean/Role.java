package com.jpp.demo.withjpa.bean;


import javax.persistence.*;

/*
* 用户角色实体类
* */
@Entity(name="t_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   //角色id
    private String name;   //角色英文
    private String nameZh;  //角色中文名称



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }
}
