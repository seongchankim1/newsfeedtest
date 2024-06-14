package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.CommentResponseDto;
import com.sparta.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newsfeed.entity.Comment;
import com.sparta.newsfeed.entity.Like;
import com.sparta.newsfeed.entity.Newsfeed;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.CommentRepository;
import com.sparta.newsfeed.repository.LikeRepository;
import com.sparta.newsfeed.repository.NewsfeedRepository;
import com.sparta.newsfeed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeService {

    private final NewsfeedRepository newsfeedRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public LikeService(NewsfeedRepository newsfeedRepository, LikeRepository likeRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.newsfeedRepository = newsfeedRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public NewsfeedResponseDto toggleLike(String username, Long newsfeedId) {
        Newsfeed newsfeed = newsfeedRepository.findById(newsfeedId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 입니다."));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (newsfeed.getUsername().equals(username)) {
            throw new IllegalArgumentException("본인이 작성한 게시물 입니다.");
        }

        Optional<Like> existLike = likeRepository.findByUserAndNewsfeed(user, newsfeed);

        if(existLike.isPresent()) {
            likeRepository.delete(existLike.get());
            newsfeed.updateLikeUpdated();
            newsfeed.setLikes(newsfeed.getLikes() - 1);
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setNewsfeed(newsfeed);
            like.setComment(null);
            newsfeed.updateLikeCreated();
            newsfeed.updateLikeUpdated();
            likeRepository.save(like);
            newsfeed.setLikes(newsfeed.getLikes() + 1);
        }

        Newsfeed savedNewsfeed = newsfeedRepository.save(newsfeed);
        return new NewsfeedResponseDto(savedNewsfeed);
    }

    public CommentResponseDto commentLiked(String username, Long newsfeedId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Newsfeed newsfeed = newsfeedRepository.findById(newsfeedId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 입니다."));

        if(comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("본인이 작성한 게시물 입니다.");
        }

        Optional<Like> existLike = likeRepository.findByUserAndComment(user, comment);

        if(existLike.isPresent()) {
            likeRepository.delete(existLike.get());
            comment.updateLikeUpdated();
            comment.setGoodCounting(comment.getGoodCounting() - 1);
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setComment(comment);
            like.setNewsfeed(newsfeed);
            comment.updateLikeCreated();
            comment.updateLikeUpdated();
            likeRepository.save(like);
            comment.setGoodCounting(comment.getGoodCounting() + 1);
        }

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment);
    }
}
