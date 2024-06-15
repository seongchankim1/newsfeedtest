package com.sparta.newsfeed.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sparta.newsfeed.dto.CommentCreateRequestDto;
import com.sparta.newsfeed.dto.CommentResponseDto;
import com.sparta.newsfeed.dto.CommentUpdateRequestDto;
import com.sparta.newsfeed.entity.Comment;
import com.sparta.newsfeed.entity.Newsfeed;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.CommentRepository;
import com.sparta.newsfeed.repository.NewsfeedRepository;
import com.sparta.newsfeed.repository.UserRepository;
import com.sparta.newsfeed.service.CommentService;

@SpringBootTest
class CommentServiceTest {

	@Autowired
	private CommentService commentService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NewsfeedRepository newsfeedRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private JwtUtil jwtUtil;
	private MockHttpServletResponse response;
	private String token;
	private User user;
	private Newsfeed newsfeed;

	@BeforeEach
	void setUp() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		// 사용자 생성
		String username = "testusername";
		String password = passwordEncoder.encode("testpassword");
		String name = "testname";
		String nickname = "testnickname";
		String email = "test@test";
		String introduce = "testintroduce";
		String userStatus = "정상";
		String refreshToken = jwtUtil.createAccessToken(username);
		String authKey = null;
		LocalDateTime verifyTime = null;

		user = new User(username, password, name, nickname, email, introduce, userStatus, refreshToken, authKey, verifyTime);
		userRepository.save(user);

		// 뉴스피드 생성
		newsfeed = new Newsfeed();
		newsfeed.setTitle("testtitle");
		newsfeed.setContent("testcontent");
		newsfeed.setUser(user);
		newsfeedRepository.save(newsfeed);

		// 토큰 생성 및 설정
		response = new MockHttpServletResponse();
		token = jwtUtil.createAccessToken(username);
		response.addHeader("Authorization", token);
	}

	@AfterEach
	void removeData() {
		Comment comment = commentRepository.findByUsername("testusername");
		if (comment != null) {
			commentRepository.deleteById(comment.getId());
		}
		Newsfeed newsfeed = newsfeedRepository.findByUsername("testusername").orElse(null);
		if (newsfeed != null) {
			newsfeedRepository.deleteById(newsfeed.getId());
		}
		User user = userRepository.findByUsername("testusername").orElse(null);
		if (user != null) {
			userRepository.deleteById(user.getId());
		}
	}

	@Test
	@DisplayName("댓글 생성 테스트")
	void createCommentTest() {
		// 토큰 추가
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);

		CommentCreateRequestDto createDto = new CommentCreateRequestDto("testcomment", "testusername", "testnickname");

		CommentResponseDto responseDto = commentService.createComment(newsfeed.getId(), createDto, response, request);

		assertEquals("testcomment", responseDto.getComment());
		assertEquals("testtitle", responseDto.getTitle());
	}

	@Test
	@DisplayName("댓글 조회 테스트")
	void getCommentTest() {
		// 토큰 추가
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);

		CommentCreateRequestDto createDto = new CommentCreateRequestDto("testcomment", "testusername", "testnickname");
		CommentResponseDto createdResponse = commentService.createComment(newsfeed.getId(), createDto, response, request);

		CommentResponseDto responseDto = commentService.findComment(createdResponse.getId());

		assertEquals("testcomment", responseDto.getComment());
		assertEquals("testtitle", responseDto.getTitle());
	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void updateCommentTest() {
		// 토큰 추가
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);

		CommentCreateRequestDto createDto = new CommentCreateRequestDto("testcomment", "testusername", "testnickname");
		CommentResponseDto createdResponse = commentService.createComment(newsfeed.getId(), createDto, response, request);

		CommentUpdateRequestDto updateDto = new CommentUpdateRequestDto(createdResponse.getId(), "updatedcomment");

		CommentResponseDto updatedResponse = commentService.updateComment(createdResponse.getId(), newsfeed.getId(), updateDto, response, request);

		assertEquals("updatedcomment", updatedResponse.getComment());
	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void deleteCommentTest() {
		// 토큰 추가
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);

		CommentCreateRequestDto createDto = new CommentCreateRequestDto("testcomment", "testusername", "testnickname");
		CommentResponseDto createdResponse = commentService.createComment(newsfeed.getId(), createDto, response, request);

		String result = commentService.deleteComment(createdResponse.getId(), newsfeed.getId(), response, request);

		assertEquals(createdResponse.getId() + "번 댓글이 삭제되었습니다.", result);
	}
}
