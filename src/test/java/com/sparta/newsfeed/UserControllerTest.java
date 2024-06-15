package com.sparta.newsfeed;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newsfeed.config.WebSecurityConfig;
import com.sparta.newsfeed.controller.CommentController;
import com.sparta.newsfeed.controller.LikeController;
import com.sparta.newsfeed.controller.NewsfeedController;
import com.sparta.newsfeed.controller.UserController;
import com.sparta.newsfeed.dto.SignupRequestDto;
import com.sparta.newsfeed.dto.UserRequestDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.UserRoleEnum;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.mvc.MockSpringSecurityFilter;
import com.sparta.newsfeed.repository.UserRepository;
import com.sparta.newsfeed.security.UserDetailsImpl;
import com.sparta.newsfeed.service.CommentService;
import com.sparta.newsfeed.service.LikeService;
import com.sparta.newsfeed.service.NewsfeedService;
import com.sparta.newsfeed.service.UserService;

@WebMvcTest(
	controllers = {
		UserController.class,
		NewsfeedController.class,
		LikeController.class,
		CommentController.class
	},
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	}
)
class UserControllerTest {

	private MockMvc mvc;
	private Principal mockPrincipal;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private UserService userService;

	@MockBean
	private CommentService commentService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private LikeService likeService;

	@MockBean
	private NewsfeedService newsfeedService;

	@MockBean
	private JwtUtil jwtUtil;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(springSecurity(new MockSpringSecurityFilter()))
			.build();
		User mockUser = new User();
		mockUser.setUsername("testusername");
		mockUser.setPassword("Testpassword1!");
		mockUser.setRole(UserRoleEnum.USER);

		UserDetailsImpl mockUserDetails = new UserDetailsImpl(mockUser);
		mockPrincipal = new UsernamePasswordAuthenticationToken(mockUserDetails, "password", mockUserDetails.getAuthorities());
	}

	@Test
	@DisplayName("UserController - 회원가입 테스트")
	public void test1() throws Exception {
		SignupRequestDto signupRequestDto = new SignupRequestDto(
			"testusername", "Testpassword1!", "testnickname", "testname",
			"testemail", "testintroduce", "testuserstatus", "testrefreshtoken",
			new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis())
		);

		mvc.perform(post("/api/user/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequestDto)))
			.andExpect(status().isOk())
			.andExpect(content().string("회원가입 완료! 이메일 인증을 해주세요."));
	}

	@Test
	@DisplayName("UserController - 회원탈퇴 테스트")
	void test2() throws Exception {
		UserRequestDto userRequestDto = new UserRequestDto(
			"testnickname", "testname", "testpassword", "testemail", "testintroduce", Timestamp.valueOf(LocalDateTime.now())
		);

		mvc.perform(delete("/api/user/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequestDto)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("UserController - 로그아웃")
	void test3() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setRequestURI("/api/user/logout");

		mvc.perform(post("/api/user/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal))
			.andExpect(status().isOk());
	}
}
