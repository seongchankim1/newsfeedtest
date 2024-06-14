package com.sparta.newsfeed.dto;

import lombok.Getter;

@Getter

public class CommentCreateRequestDto {
    private String comment;
    private String username;
    private String nickname;

    public CommentCreateRequestDto(String comment, String username, String nickname) {
        this.comment = comment;
        this.username = username;
        this.nickname = nickname;
    }
}
