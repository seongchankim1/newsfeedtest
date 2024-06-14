package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.CommentCreateRequestDto;
import com.sparta.newsfeed.dto.CommentResponseDto;
import com.sparta.newsfeed.dto.CommentUpdateRequestDto;
import com.sparta.newsfeed.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{newsfeedId}/comments")
@RequiredArgsConstructor

public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto createComment(@PathVariable(value = "newsfeedId") Long newsfeedId,
                                         @RequestBody CommentCreateRequestDto requestDto,
                                         HttpServletResponse response,
                                         HttpServletRequest request) {
        return commentService.createComment(newsfeedId, requestDto, response, request);
    }

    @GetMapping("/{id}")
    public CommentResponseDto findComments(@PathVariable(value = "id") Long id) {
        return commentService.findComment(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long id,
            @PathVariable(value = "newsfeedId") Long newsfeedId,
            @RequestBody CommentUpdateRequestDto requestDto,
            HttpServletResponse response,
            HttpServletRequest request) {
        return ResponseEntity.ok().body(commentService.updateComment(id,newsfeedId, requestDto, response, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long id,
            @PathVariable(value = "newsfeedId") Long newsfeedId,
            HttpServletResponse response,
            HttpServletRequest request) {
        commentService.deleteComment(id,newsfeedId, response, request);
        return ResponseEntity.ok().body("성공적으로 댓글 삭제");
    }
}

