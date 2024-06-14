package com.sparta.newsfeed.dto;

import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {

    private String comment;

    public CommentUpdateRequestDto(Long id, String comment) {
        this.comment = comment;
    }
}
