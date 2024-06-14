package com.sparta.newsfeed;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sparta.newsfeed.dto.CommentCreateRequestDto;
import com.sparta.newsfeed.dto.CommentResponseDto;
import com.sparta.newsfeed.dto.CommentUpdateRequestDto;
import com.sparta.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newsfeed.dto.PagingRequestDto;
import com.sparta.newsfeed.dto.SignupRequestDto;
import com.sparta.newsfeed.dto.UserRequestDto;
import com.sparta.newsfeed.dto.UserResponseDto;
import com.sparta.newsfeed.dto.UserUpdateRequestDto;
import com.sparta.newsfeed.dto.UserUpdateResponseDto;
import com.sparta.newsfeed.dto.VerifyRequestDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.Comment;
import com.sparta.newsfeed.entity.Newsfeed;

public class DtoTest {

	private User testUser;
	private Newsfeed testNewsfeed;
	private Comment testComment;

	@BeforeEach
	public void setUp() {
		testUser = new User("testUsername", "testPassword", "testName", "testNickname",
			"testEmail", "testIntroduce", "미인증", "",
			"testAuthKey", LocalDateTime.now());
		testUser.updateUpdateDate();
		testUser.updateStatusChanged();

		NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto(testUser.getUsername(), "testTitle",
			"testContent", 1);
		testNewsfeed = new Newsfeed(newsfeedRequestDto, testUser);
		testNewsfeed.updateUpdateDate();
		testNewsfeed.updateLikeCreated();
		testNewsfeed.updateLikeUpdated();

		CommentCreateRequestDto commentCreateRequestDto = new CommentCreateRequestDto("test comment",
			testUser.getUsername(), testUser.getNickname());
		testComment = new Comment(commentCreateRequestDto, testNewsfeed, testUser.getUsername());
		testComment.setId(1L);
		testComment.updateUpdateDate();
		testComment.updateLikeCreated();
		testComment.updateLikeUpdated();
	}


	@Test
	@DisplayName("CommentCreateRequestDto 테스트")
	public void test1() {
		// Given
		String commentText = testComment.getComment();

		// When
		CommentCreateRequestDto request = new CommentCreateRequestDto(commentText, testUser.getUsername(), testUser.getNickname());

		// Then
		assertEquals(commentText, request.getComment(), "댓글이 일치하지 않습니다.");
		assertEquals(testUser.getUsername(), request.getUsername(), "이름이 일치하지 않습니다.");
		assertEquals(testUser.getNickname(), request.getNickname(), "닉네임이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("CommentResponseDto 테스트")
	public void test2() {
		// Given
		long id = testComment.getId();
		String title = testNewsfeed.getTitle();
		String commentText = testComment.getComment();
		long goodCounting = testComment.getGoodCounting();

		// When
		CommentResponseDto response = new CommentResponseDto(id, title, commentText, goodCounting);

		// Then
		assertEquals(id, response.getId(), "id가 일치하지 않습니다.");
		assertEquals(title, response.getTitle(), "제목이 일치하지 않습니다.");
		assertEquals(commentText, response.getComment(), "댓글이 일치하지 않습니다.");
		assertEquals(goodCounting, response.getGood_counting(), "좋아요 수가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("CommentUpdateRequestDto 테스트")
	public void test3() {
		// Given
		Long id = testComment.getId();
		String commentText = testComment.getComment();

		// When
		CommentUpdateRequestDto request = new CommentUpdateRequestDto(id, commentText);

		// Then
		assertEquals(commentText, request.getComment(), "댓글이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("NewsfeedRequestDto 테스트")
	public void test4() {
		// Given
		String username = testUser.getUsername();
		String title = testNewsfeed.getTitle();
		String content = testNewsfeed.getContent();
		int like = testNewsfeed.getLikes();

		// When
		NewsfeedRequestDto request = new NewsfeedRequestDto(username, title, content, like);

		// Then
		assertEquals(username, request.getUsername(), "이름이 일치하지 않습니다.");
		assertEquals(title, request.getTitle(), "제목이 일치하지 않습니다.");
		assertEquals(content, request.getContent(), "내용이 일치하지 않습니다.");
		assertEquals(like, request.getLike(), "좋아요 수가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("NewsfeedResponseDto 테스트")
	public void test5() {
		// Given
		Long id = testNewsfeed.getId();
		String title = testNewsfeed.getTitle();
		String content = testNewsfeed.getContent();

		// When
		NewsfeedResponseDto response = new NewsfeedResponseDto(id, title, content);

		// Then
		assertEquals(id, response.getId(), "이름이 일치하지 않습니다.");
		assertEquals(title, response.getTitle(), "제목이 일치하지 않습니다.");
		assertEquals(content, response.getContent(), "내용이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("PagingRequestDto 테스트")
	public void test6() {
		// Given
		LocalDateTime startDate = LocalDateTime.of(2024, 6, 14, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 6, 14, 23, 59);
		String sortBy = "likes";

		// When
		PagingRequestDto request = new PagingRequestDto(startDate, endDate, sortBy);

		// Then
		assertEquals(startDate, request.getStartDate(), "시작 날짜가 일치하지 않습니다.");
		assertEquals(endDate, request.getEndDate(), "마지막 날짜가 일치하지 않습니다.");
		assertEquals(sortBy, request.getSortBy(), "정렬 방식이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("SignupRequestDto 테스트")
	public void test7() {
		// Given
		String username = testUser.getUsername();
		String password = testUser.getPassword();
		String nickname = testUser.getNickname();
		String name = testUser.getName();
		String email = testUser.getEmail();
		String introduce = testUser.getIntroduce();
		String userStatus = testUser.getUserStatus();
		String refreshToken = testUser.getRefreshToken();
		Timestamp created = Timestamp.valueOf(testUser.getWriteDate());
		Timestamp updated = Timestamp.valueOf(testUser.getUpdateDate());

		// When
		SignupRequestDto request = new SignupRequestDto(username, password, nickname, name, email, introduce, userStatus, refreshToken, created, updated);

		// Then
		assertEquals(username, request.getUsername(), "사용자명이 일치하지 않습니다.");
		assertEquals(password, request.getPassword(), "비밀번호가 일치하지 않습니다.");
		assertEquals(nickname, request.getNickname(), "닉네임이 일치하지 않습니다.");
		assertEquals(name, request.getName(), "이름이 일치하지 않습니다.");
		assertEquals(email, request.getEmail(), "이메일이 일치하지 않습니다.");
		assertEquals(introduce, request.getIntroduce(), "소개가 일치하지 않습니다.");
		assertEquals(userStatus, request.getUser_status(), "사용자 상태가 일치하지 않습니다.");
		assertEquals(refreshToken, request.getRefreshToken(), "리프레시 토큰이 일치하지 않습니다.");
		assertEquals(created, request.getCreated(), "생성일이 일치하지 않습니다.");
		assertEquals(updated, request.getUpdated(), "수정일이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("UserRequestDto 테스트")
	public void test8() {
		// Given
		String nickname = testUser.getNickname();
		String name = testUser.getName();
		String password = testUser.getPassword();
		String email = testUser.getEmail();
		String introduce = testUser.getIntroduce();
		Timestamp updated = Timestamp.valueOf(testUser.getUpdateDate());

		// When
		UserRequestDto request = new UserRequestDto(nickname, name, password, email, introduce, updated);

		// Then
		assertEquals(nickname, request.getNickname(), "닉네임이 일치하지 않습니다.");
		assertEquals(name, request.getName(), "이름이 일치하지 않습니다.");
		assertEquals(password, request.getPassword(), "패스워드가 일치하지 않습니다.");
		assertEquals(email, request.getEmail(), "이메일이 일치하지 않습니다.");
		assertEquals(introduce, request.getIntroduce(), "자기소개가 일치하지 않습니다.");
		assertEquals(updated, request.getUpdated(), "수정 날짜가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("UserResponseDto 테스트")
	public void test9() {
		// Given
		String username = testUser.getUsername();
		String name = testUser.getName();
		String email = testUser.getEmail();
		String introduce = testUser.getIntroduce();

		// When
		UserResponseDto response = new UserResponseDto(testUser);

		// Then
		assertEquals(username, response.getUsername(), "사용자명이 일치하지 않습니다.");
		assertEquals(name, response.getName(), "이름이 일치하지 않습니다.");
		assertEquals(email, response.getEmail(), "이메일이 일치하지 않습니다.");
		assertEquals(introduce, response.getIntroduce(), "소개가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("UserUpdateRequestDto 테스트")
	public void test10() {
		// Given
		String name = testUser.getName();
		String nickname = testUser.getNickname();
		String email = testUser.getEmail();
		String introduce = testUser.getIntroduce();
		String password = testUser.getPassword();

		// When
		UserUpdateRequestDto request = new UserUpdateRequestDto(testUser);

		// Then
		assertEquals(name, request.getName(), "이름이 일치하지 않습니다.");
		assertEquals(email, request.getEmail(), "이메일이 일치하지 않습니다.");
		assertEquals(introduce, request.getIntroduce(), "소개가 일치하지 않습니다.");
		assertEquals(nickname, request.getNickname(), "닉네임이 일치하지 않습니다.");
		assertEquals(password, request.getPassword(), "패스워드가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("UserUpdateResponseDto 테스트")
	public void test11() {
		// Given
		String name = testUser.getName();
		String nickname = testUser.getNickname();
		String email = testUser.getEmail();
		String introduce = testUser.getIntroduce();

		// When
		UserUpdateResponseDto response = new UserUpdateResponseDto(testUser);

		// Then
		assertEquals(name, response.getName(), "이름이 일치하지 않습니다.");
		assertEquals(email, response.getEmail(), "이메일이 일치하지 않습니다.");
		assertEquals(introduce, response.getIntroduce(), "소개가 일치하지 않습니다.");
		assertEquals(nickname, response.getNickname(), "닉네임이 일치하지 않습니다.");
	}

	@Test
	@DisplayName("VerifyRequestDto 테스트")
	public void test12() {
		// Given
		 String username = testUser.getUsername();
		 String password = testUser.getPassword();
		 String authKey = testUser.getAuthKey();

		// When
		VerifyRequestDto request = new VerifyRequestDto(testUser);

		// Then
		assertEquals(username, request.getUsername(), "이름이 일치하지 않습니다.");
		assertEquals(password, request.getPassword(), "패스워드가 일치하지 않습니다.");
		assertEquals(authKey, request.getAuthKey(), "인증키가 일치하지 않습니다.");
	}
}
