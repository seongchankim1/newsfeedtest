package com.sparta.newsfeed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newsfeed.config.WebSecurityConfig;
import com.sparta.newsfeed.controller.CommentController;
import com.sparta.newsfeed.controller.LikeController;
import com.sparta.newsfeed.controller.NewsfeedController;
import com.sparta.newsfeed.controller.UserController;
import com.sparta.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.UserRoleEnum;
import com.sparta.newsfeed.mvc.MockSpringSecurityFilter;
import com.sparta.newsfeed.security.UserDetailsImpl;
import com.sparta.newsfeed.service.CommentService;
import com.sparta.newsfeed.service.NewsfeedService;

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
class NewsfeedControllerTest {

	private MockMvc mvc;
	private Principal mockPrincipal;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private NewsfeedService newsfeedService;

	@MockBean
	private CommentService commentService;

	@MockBean
	private LikeController likeController;

	@MockBean
	private UserController userController;

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
	@DisplayName("NewsfeedController - 뉴스피드 생성 테스트")
	void test1() throws Exception {
		NewsfeedRequestDto requestDto = new NewsfeedRequestDto("testuser", "Test Title", "Test Content");

		MvcResult result = mvc.perform(post("/api/newsfeed")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andReturn();
		String jsonResponse = result.getResponse().getContentAsString();

		if (jsonResponse != null && !jsonResponse.isEmpty()) {
			NewsfeedResponseDto testResponseDto = objectMapper.readValue(jsonResponse, NewsfeedResponseDto.class);

			assertEquals(1L, testResponseDto.getId());
			assertEquals("Test Title", testResponseDto.getTitle());
			assertEquals("Test Content", testResponseDto.getContent());
		}
	}

	@Test
	@DisplayName("NewsfeedController - 뉴스피드 단건 조회 테스트")
	void test2() throws Exception {
		NewsfeedResponseDto responseDto = new NewsfeedResponseDto(1L, "Test Title", "Test Content");

		when(newsfeedService.getNewsfeed(1L)).thenReturn(responseDto);

		MvcResult result = mvc.perform(get("/api/newsfeed/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		String jsonResponse = result.getResponse().getContentAsString();

		if (jsonResponse != null && !jsonResponse.isEmpty()) {
			NewsfeedResponseDto testResponseDto = objectMapper.readValue(jsonResponse, NewsfeedResponseDto.class);

			assertEquals(1L, testResponseDto.getId());
			assertEquals("Test Title", testResponseDto.getTitle());
			assertEquals("Test Content", testResponseDto.getContent());
		}
	}

	@Test
	@DisplayName("NewsfeedController - 뉴스피드 전체 조회 테스트")
	void test3() throws Exception {
		NewsfeedResponseDto responseDto1 = new NewsfeedResponseDto(1L, "Test Title 1", "Test Content 1");
		NewsfeedResponseDto responseDto2 = new NewsfeedResponseDto(2L, "Test Title 2", "Test Content 2");
		List<NewsfeedResponseDto> responseList = Arrays.asList(responseDto1, responseDto2);

		when(newsfeedService.getAllNewsfeeds()).thenReturn(responseList);

		MvcResult result = mvc.perform(get("/api/newsfeed")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		String jsonResponse = result.getResponse().getContentAsString();

		if (jsonResponse != null && !jsonResponse.isEmpty()) {
			NewsfeedResponseDto[] testResponseList = objectMapper.readValue(jsonResponse, NewsfeedResponseDto[].class);

			assertEquals(2, testResponseList.length);

			NewsfeedResponseDto testResponseDto1 = testResponseList[0];
			NewsfeedResponseDto testResponseDto2 = testResponseList[1];

			assertEquals(1L, testResponseDto1.getId());
			assertEquals("Test Title 1", testResponseDto1.getTitle());
			assertEquals("Test Content 1", testResponseDto1.getContent());

			assertEquals(2L, testResponseDto2.getId());
			assertEquals("Test Title 2", testResponseDto2.getTitle());
			assertEquals("Test Content 2", testResponseDto2.getContent());
		}
	}

	@Test
	@DisplayName("NewsfeedController - 뉴스피드 생성 후 수정 테스트")
	void testCreateAndUpdateNewsfeed() throws Exception {
		NewsfeedRequestDto createRequestDto = new NewsfeedRequestDto("testuser", "Title", "Content");
		NewsfeedRequestDto updateRequestDto = new NewsfeedRequestDto("testuser", "Updated Title", "Updated Content");

		NewsfeedResponseDto createResponseDto = new NewsfeedResponseDto(1L, "Title", "Content");
		NewsfeedResponseDto updateResponseDto = new NewsfeedResponseDto(1L, "Updated Title", "Updated Content");

		// any 사용 시 id도 eq로 감싸줘야 하는듯
		when(newsfeedService.createNewsfeed(any(NewsfeedRequestDto.class), any(), any())).thenReturn(createResponseDto);
		when(newsfeedService.updateNewsfeed(eq(1L), any(NewsfeedRequestDto.class), any(), any())).thenReturn(updateResponseDto);

		MvcResult createResult = mvc.perform(post("/api/newsfeed")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		String createJsonResponse = createResult.getResponse().getContentAsString();

		if (createJsonResponse != null && !createJsonResponse.isEmpty()) {
			NewsfeedResponseDto createTestResponseDto = objectMapper.readValue(createJsonResponse, NewsfeedResponseDto.class);

			assertEquals(1L, createTestResponseDto.getId());
			assertEquals("Title", createTestResponseDto.getTitle());
			assertEquals("Content", createTestResponseDto.getContent());
		}

		MvcResult updateResult = mvc.perform(put("/api/newsfeed/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		String updateJsonResponse = updateResult.getResponse().getContentAsString();

		if (updateJsonResponse != null && !updateJsonResponse.isEmpty()) {
			NewsfeedResponseDto updateTestResponseDto = objectMapper.readValue(updateJsonResponse, NewsfeedResponseDto.class);

			assertEquals(1L, updateTestResponseDto.getId());
			assertEquals("Updated Title", updateTestResponseDto.getTitle());
			assertEquals("Updated Content", updateTestResponseDto.getContent());
		}
	}

	@Test
	@DisplayName("NewsfeedController - 뉴스피드 삭제 테스트")
	void test5() throws Exception {
		when(newsfeedService.deleteNewsfeed(eq(1L), any(), any())).thenReturn("삭제 완료!");

		mvc.perform(delete("/api/newsfeed/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("삭제 완료!"));
	}
}
