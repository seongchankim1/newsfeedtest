package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.CommentCreateRequestDto;
import com.sparta.newsfeed.dto.CommentUpdateRequestDto;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@Transactional
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // 사용자 ID

    @Column
    private String nickname; // 사용자별명

    @Column(nullable = false)
    @NotBlank(message = "공백을 허용하지 않습니다.")
    private String comment;

    @Column(nullable = false)
    private long goodCounting;

    @ManyToOne
    @JoinColumn(name = "newsfeed_id", nullable = true)
    private Newsfeed newsfeed;

    public Comment(CommentCreateRequestDto commentCreateRequest, Newsfeed newsfeed, String username) {
        this.username = username;
        this.comment = commentCreateRequest.getComment();
        this.newsfeed = newsfeed;
        this.nickname = commentCreateRequest.getNickname();
        this.goodCounting = 0;
    }

    public void update(CommentUpdateRequestDto requestDto, Newsfeed newsfeed) {
        this.comment = requestDto.getComment();
        updateUpdateDate();
    }
}
