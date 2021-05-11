package com.jpp.demo.withjpa.bean;

import java.util.Date;

/*
* 自动登录安全策略
* 保存令牌的处理类
* */
public class PersistentRememberMeToken {

    private final String username;
    private final String series;
    private final String tokenValue;
    private final Date date;  //上一次登录的时间

    public PersistentRememberMeToken(String username, String series, String tokenValue, Date date) {
        this.username = username;
        this.series = series;
        this.tokenValue = tokenValue;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getSeries() {
        return series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Date getDate() {
        return date;
    }
}
