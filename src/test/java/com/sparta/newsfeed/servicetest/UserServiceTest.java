package com.sparta.newsfeed.servicetest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sparta.newsfeed.dto.SignupRequestDto;
import com.sparta.newsfeed.dto.UserRequestDto;
import com.sparta.newsfeed.dto.UserResponseDto;
import com.sparta.newsfeed.dto.UserUpdateRequestDto;
import com.sparta.newsfeed.dto.VerifyRequestDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.repository.NewsfeedRepository;
import com.sparta.newsfeed.repository.UserRepository;
import com.sparta.newsfeed.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest

public class UserServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	private User testUser;
	@Autowired
	private NewsfeedRepository newsfeedRepository;

	@BeforeEach
	void setUp() throws MessagingException {

		// 회원가입 테스트
		SignupRequestDto requestDto = new SignupRequestDto(
			"testuser",
			"testpassword",
			"testnickname",
			"testname",
			"testemail@mail",
			"testintroduce",
			"testuserstatus",
			"testrefreshtoken",
			Timestamp.valueOf(LocalDateTime.now()),
			Timestamp.valueOf(LocalDateTime.now()));

		UserResponseDto signupResponseDto = userService.signup(requestDto);
		testUser = userRepository.findByUsername("testuser").orElse(null);

		assertThat(testUser).isNotNull();
		assertThat(passwordEncoder.matches(requestDto.getPassword(), testUser.getPassword())).isTrue();

		testUser.setUserStatus("정상");
		userRepository.save(testUser);
	}

	// 테스트용 유저 삭제
	@AfterEach
	void removeData() {

		User user = userRepository.findByUsername("testuser").orElse(null);
		if (user != null) {
			userRepository.deleteById(user.getId());
		}
	}

	@Test
	void loginTest() {
		// 로그인 테스트
		HttpServletResponse response = new MockHttpServletResponse();
		String loginMessage = userService.login("testuser", "testpassword", response);
		assertThat(loginMessage).contains("로그인 성공!");

		// 로그인 실패 테스트
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userService.login("testuser", "wrongpassword", response);
		});
		System.out.println(response.getHeader(JwtUtil.AUTHORIZATION_HEADER));
		assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
	}

	@Test
	void testLogout() {
		// 로그인 먼저 수행
		MockHttpServletResponse response = new MockHttpServletResponse();
		String loginMessage = userService.login("testuser", "testpassword", response);

		assertThat(loginMessage).contains("로그인 성공!");
		String token = response.getHeader(JwtUtil.AUTHORIZATION_HEADER);

		// 로그아웃 테스트
		MockHttpServletRequest logoutRequest = new MockHttpServletRequest();
		logoutRequest.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

		String logoutMessage = userService.logout(logoutRequest);

		assertEquals(logoutMessage, "로그아웃 성공! 토큰이 초기화되었습니다.");

		// 로그아웃 후 리프레시 토큰이 null인지 확인
		User user = userRepository.findByUsername("testuser").orElse(null);
		assertNotEquals(user, null);
		assertEquals(user.getRefreshToken(), null);
	}

	@Test
	void testWithdraw() {
		// 로그인 먼저 수행
		MockHttpServletResponse loginResponse = new MockHttpServletResponse();
		String loginMessage = userService.login("testuser", "testpassword", loginResponse);

		assertThat(loginMessage).contains("로그인 성공!");
		String token = loginResponse.getHeader(JwtUtil.AUTHORIZATION_HEADER);

		// 탈퇴 요청 준비
		MockHttpServletRequest withdrawRequest = new MockHttpServletRequest();
		withdrawRequest.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

		UserRequestDto withdrawRequestDto = new UserRequestDto("nickname", "name", "testpassword", "email", "introduce", Timestamp.valueOf(LocalDateTime.now()));

		MockHttpServletResponse withdrawResponse = new MockHttpServletResponse();

		// 탈퇴 테스트
		userService.withdraw(withdrawRequestDto, withdrawResponse, withdrawRequest);

		User user = userRepository.findByUsername("testuser").orElse(null);
		assertNotEquals(user, null);
		assertEquals(user.getUserStatus(), "탈퇴");
	}

	@Test
	void findUserTest() {
		UserResponseDto userResponseDto = userService.findUser("testuser");

		assertEquals(userResponseDto.getUsername(), "testuser");
		assertEquals(userResponseDto.getEmail(), "testemail@mail");
	}

	@Test
	void profileUpdateTest() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		String loginMessage = userService.login("testuser", "testpassword", response);

		assertThat(loginMessage).contains("로그인 성공!");
		String token = response.getHeader(JwtUtil.AUTHORIZATION_HEADER);

		MockHttpServletRequest updateRequest = new MockHttpServletRequest();
		updateRequest.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

		User user = userRepository.findByUsername("testuser").orElse(null);
		user.setIntroduce("helloworld");
		UserUpdateRequestDto requestDto = new UserUpdateRequestDto(user);
		requestDto.setPassword("testpassword");
		requestDto.setIntroduce("updatedintroduce");
		requestDto.setNickname("updatednickname");
		userService.profileUpdate(requestDto, response, updateRequest);

		User updatedUser = userRepository.findByUsername("testuser").orElse(null);
		assertThat(updatedUser).isNotNull();
		assertEquals(updatedUser.getNickname(), "updatednickname");
		assertEquals(updatedUser.getIntroduce(), "updatedintroduce");
	}

	@Test
	void emailVerificationTest() {
		User user = userRepository.findByUsername("testuser").orElse(null);
		user.setUserStatus("미인증");
		VerifyRequestDto requestDto = new VerifyRequestDto(user);
		requestDto.setPassword("testpassword");
		String verifyMessage = userService.verifyMail(requestDto);

		assertEquals("인증 완료!", verifyMessage);
		User verifiedUser = userRepository.findByUsername("testuser").orElse(null);
		assertThat(verifiedUser).isNotNull();
		assertEquals(verifiedUser.getUserStatus(), "정상");
	}
}
