package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.CommentCreateRequestDto;
import com.sparta.newsfeed.dto.CommentResponseDto;
import com.sparta.newsfeed.dto.CommentUpdateRequestDto;
import com.sparta.newsfeed.entity.Comment;
import com.sparta.newsfeed.entity.Newsfeed;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.CommentRepository;
import com.sparta.newsfeed.repository.NewsfeedRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final NewsfeedRepository newsfeedRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;
    private final DataSourceAutoConfiguration dataSourceAutoConfiguration;
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    public CommentService(NewsfeedRepository newsfeedRepository, CommentRepository commentRepository, JwtUtil jwtUtil, NewsfeedService newsfeedService, DataSourceAutoConfiguration dataSourceAutoConfiguration, DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration) {
        this.newsfeedRepository = newsfeedRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
        this.dataSourceAutoConfiguration = dataSourceAutoConfiguration;
        this.dataSourceTransactionManagerAutoConfiguration = dataSourceTransactionManagerAutoConfiguration;
    }

    @Transactional
    public CommentResponseDto createComment(Long id,
                                         CommentCreateRequestDto requestDto,
                                         HttpServletResponse response,
                                         HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String newAccessToken = jwtUtil.refreshToken(token, response);
        String newBearerAccessToken = jwtUtil.substringToken(newAccessToken);
        String username = jwtUtil.getUserInfoFromToken(newBearerAccessToken).getSubject();
//        token = jwtUtil.substringToken(token);
        Newsfeed newsfeed = newsfeedRepository.findById(id).orElseThrow(()
                -> new RuntimeException("입력하신 뉴스피드가 존재하지 않습니다."));

        Comment comment = new Comment(requestDto, newsfeed, username);
        comment.setNewsfeed(newsfeed);
        Comment saveComment = commentRepository.save(comment);
        System.out.println(id + "번 뉴스피드에 댓글이 등록되었습니다.");
        return new CommentResponseDto(saveComment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id,
                                         Long newsfeedId,
                                         CommentUpdateRequestDto requestDto,
                                         HttpServletResponse response,
                                         HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String newAccessToken = jwtUtil.refreshToken(token, response);
        String username = jwtUtil.getUserInfoFromToken(newAccessToken).getSubject();

        Newsfeed newsfeed = newsfeedRepository.findById(newsfeedId).orElseThrow(()
                -> new IllegalArgumentException("입력하신 뉴스피드가 존재하지 않습니다."));

        Comment comment = commentRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("자신의 댓글만 삭제할 수 있습니다.");
        }
        comment.updateUpdateDate();
        comment.update(requestDto, newsfeed);
        return new CommentResponseDto(comment);

    }

    @Transactional
    public String deleteComment(Long id,
                                Long newsfeedId,
                                HttpServletResponse response,
                                HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String newAccessToken = jwtUtil.refreshToken(token, response);
        String newBearerAccessToken = jwtUtil.substringToken(newAccessToken);
        String username = jwtUtil.getUserInfoFromToken(newBearerAccessToken).getSubject();
        newsfeedRepository.findById(newsfeedId).orElseThrow(() -> new IllegalArgumentException("올바른 뉴스피드 번호를 입력해주세요."));
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제할 댓글이 없습니다."));
        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("자신의 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
        return id + "번 댓글이 삭제되었습니다.";
    }

    public CommentResponseDto findComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회하신 댓글이 존재하지 않습니다."));
        return new CommentResponseDto(comment);
    }
}


//    public Newsfeed findNewsfeedById(long id) {
//        return newsfeedRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 Id에 맞는 일정을 찾을 수 없습니다."));
//    }

//    public CommentResponse createComment(long id , CommentCreateRequest requestDto) {
////         DB에 일정이 있는지 확인
//        Newsfeed newsfeed = newsfeedRepository.findByNewsfeedId(id);
//                new IllegalArgumentException("해당 Newsfeed가 존재하지않습니다"));
//        Comment comment = new Comment(requestDto, newsfeed);
//        comment.setNewsfeed(newsfeed);
//        return CommentResponse.toDto(commentRepository.save(comment));
//    }


//    public List<NewsfeedResponseDto> findAllComment() {
//        List<Newsfeed> list = newsfeedRepository.findAll();
//        return list
//                .stream()
//                .sorted(Comparator.comparing(Newsfeed::getWrite_date).reversed())
//                .map(NewsfeedResponseDto::toDto)
//                .toList();
//    }

//        Newsfeed newsfeed = newsfeedRepository.findByUsername(comment.getNewsfeed().getUsername());
//        if (!Objects.equals(newsfeed.getUsername(), comment.getNewsfeed().getUsername())) {
//            throw new IllegalArgumentException("자신의 글만 삭제할 수 있습니다.");
