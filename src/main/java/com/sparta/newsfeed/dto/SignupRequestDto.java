package com.sparta.newsfeed.dto;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class SignupRequestDto {
    @Pattern(regexp = "^[A-Za-z0-9]{10,20}$", message = "아이디는 대소문자 포함 영어 + 숫자 최소 10글자 최대 20글자여야 합니다.")
    private String username;

    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{10,}$",
            message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함하고 최소 10글자 이상이여야 합니다.")
    private String password;
    private String nickname;
    private String name;
    private String email;
    private String introduce;
    private String user_status = "미인증";
    private String refreshToken;
    private Timestamp created;
    private Timestamp updated;

    public SignupRequestDto(String username, String password, String nickname, String name, String email, String introduce, String userStatus, String refreshToken, Timestamp created, Timestamp updated) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.introduce = introduce;
        this.refreshToken = refreshToken;
        this.created = created;
        this.updated = updated;

    }
}
