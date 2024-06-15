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

import com.sparta.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newsfeed.entity.Newsfeed;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.NewsfeedRepository;
import com.sparta.newsfeed.repository.UserRepository;
import com.sparta.newsfeed.service.NewsfeedService;

@SpringBootTest
class NewsfeedServiceTest {

	@Autowired
	private NewsfeedService newsfeedService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NewsfeedRepository newsfeedRepository;
	@Autowired
	private JwtUtil jwtUtil;
	private MockHttpServletResponse response;
	private String token;

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
		;
		String authKey = null;
		LocalDateTime verifyTime = null;

		User user = new User(username, password, name, nickname, email, introduce, userStatus, refreshToken, authKey, verifyTime);
		userRepository.save(user);

		// 토큰 생성 및 설정
		response = new MockHttpServletResponse();
		token = jwtUtil.createAccessToken(username);
		response.addHeader("Authorization", token);
	}

	// 테스트용 유저 삭제
	@AfterEach
	void removeData() {
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
	@DisplayName("뉴스피드 생성 테스트")
	void createNewsfeedTest() {
		// 토큰 추가
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);

		NewsfeedRequestDto requestDto = new NewsfeedRequestDto("testusername", "testtitle", "testcontent");

		NewsfeedResponseDto responseDto = newsfeedService.createNewsfeed(requestDto, response, request);

		assertEquals("testtitle", responseDto.getTitle());
		assertEquals("testcontent", responseDto.getContent());
	}

	@Test
	@DisplayName("뉴스피드 조회 테스트")
	void getNewsfeedTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		// 뉴스피드 생성
		NewsfeedRequestDto createDto = new NewsfeedRequestDto("testusername", "testtitle", "testcontent");
		NewsfeedResponseDto responseDto = newsfeedService.createNewsfeed(createDto, response, request);

		// 생성된 뉴스피드 조회
		Newsfeed newsfeed = newsfeedRepository.findById(responseDto.getId()).orElse(null);
		assertEquals("testtitle", newsfeed.getTitle());
		assertEquals("testcontent", newsfeed.getContent());
	}

	@Test
	@DisplayName("뉴스피드 수정 테스트")
	void updateNewsfeedTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		// 뉴스피드 생성
		NewsfeedRequestDto createDto = new NewsfeedRequestDto("testusername", "testtitle", "testcontent");
		NewsfeedResponseDto createdResponse = newsfeedService.createNewsfeed(createDto, response, request);

		// 수정할 내용
		NewsfeedRequestDto updateDto = new NewsfeedRequestDto("testusername", "updatedtitle", "updatedcontent");

		// 뉴스피드 수정
		NewsfeedResponseDto updatedResponse = newsfeedService.updateNewsfeed(createdResponse.getId(), updateDto, response, request);

		// 수정된 뉴스피드 확인
		assertEquals("updatedtitle", updatedResponse.getTitle());
		assertEquals("updatedcontent", updatedResponse.getContent());
	}

	@Test
	@DisplayName("뉴스피드 삭제 테스트")
	void deleteNewsfeedTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		// 뉴스피드 생성
		NewsfeedRequestDto createDto = new NewsfeedRequestDto("testusername", "testtitle", "testcontent");
		NewsfeedResponseDto createdResponse = newsfeedService.createNewsfeed(createDto, response, request);

		// 뉴스피드 삭제
		newsfeedService.deleteNewsfeed(createdResponse.getId(), response, request);

		// 뉴스피드 조회 시 삭제 확인
		Newsfeed deletedNewsfeed = newsfeedRepository.findById(createdResponse.getId()).orElse(null);
		assertEquals(null, deletedNewsfeed);
	}
}
