package com.sparta.newsfeed.dto;

import java.sql.Timestamp;

import lombok.Getter;

@Getter

public class UserRequestDto {
    private String nickname;
    private String name;
    private String password;
    private String email;
    private String introduce;
    private Timestamp updated;

    public UserRequestDto(String nickname, String name, String password, String email, String introduce, Timestamp updated) {
        this.nickname = nickname;
        this.name = name;
        this.password = password;
        this.email = email;
        this.introduce = introduce;
        this.updated = updated;
    }


}
