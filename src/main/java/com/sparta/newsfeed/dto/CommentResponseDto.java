package com.sparta.newsfeed.dto;

import com.sparta.newsfeed.entity.Comment;
import lombok.Getter;

@Getter

public class CommentResponseDto {
    private long id;
    private String title;
    private String comment;
    private long good_counting;

    public CommentResponseDto(long id, String title, String comment, long good_counting) {
        this.id = id;
        this.title = title;
        this.comment = comment;
        this.good_counting = good_counting;
    }

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.title = comment.getNewsfeed().getTitle();
        this.comment = comment.getComment();
        this.good_counting = comment.getGood_counting();
    }

    public static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getNewsfeed().getTitle(),
                comment.getComment(),
                comment.getGood_counting()
        );
    }
}
