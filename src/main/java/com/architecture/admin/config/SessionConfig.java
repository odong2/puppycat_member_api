package com.architecture.admin.config;

public interface SessionConfig {
    String LOGIN_ID = "id";
    String LOGIN_NICK = "nick";
    String LOGIN_UUID = "uuid";
    String MEMBER_INFO = "memberInfo";
    Integer EXPIRED_TIME = 60 * 60 * 24 * 30;

    String APP_KEY = "key";
}
