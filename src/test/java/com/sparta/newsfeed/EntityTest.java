package com.sparta.newsfeed;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sparta.newsfeed.dto.CommentCreateRequestDto;
import com.sparta.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newsfeed.entity.Comment;
import com.sparta.newsfeed.entity.Like;
import com.sparta.newsfeed.entity.Newsfeed;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.CommentRepository;
import com.sparta.newsfeed.repository.LikeRepository;
import com.sparta.newsfeed.repository.NewsfeedRepository;
import com.sparta.newsfeed.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class EntityTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NewsfeedRepository newsfeedRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	private User testUser;
	private Newsfeed testNewsfeed;
	private Comment testComment;
	private Like testLike;

	@BeforeEach
	void setup() {
		testUser = new User(
			"testUsername",
			"testPassword",
			"testName",
			"testNickname",
			"test@example.com",
			"testIntroduce",
			"ACTIVE",
			"testRefreshToken",
			"testAuthKey",
			LocalDateTime.now());
		userRepository.save(testUser);

		testNewsfeed = new Newsfeed(
			new NewsfeedRequestDto(
				testUser.getUsername(),
				"test Title",
				"test Content"),
			testUser
		);
		newsfeedRepository.save(testNewsfeed);

		testComment = new Comment(
			new CommentCreateRequestDto(
				"test Comment",
				"testNickname", testUser.getUsername()),
			testNewsfeed,
			testUser.getUsername()
		);
		commentRepository.save(testComment);

		testLike = new Like(testUser, testNewsfeed, testComment);
		likeRepository.save(testLike);
	}

	@AfterEach
	void teardown() {
		if (testLike != null && likeRepository.existsById(testLike.getId())) {
			likeRepository.deleteById(testLike.getId());
		}

		if (testComment != null && commentRepository.existsById(testComment.getId())) {
			commentRepository.deleteById(testComment.getId());
		}

		if (testNewsfeed != null && newsfeedRepository.existsById(testNewsfeed.getId())) {
			newsfeedRepository.deleteById(testNewsfeed.getId());
		}

		if (testUser != null && userRepository.existsById(testUser.getId())) {
			userRepository.deleteById(testUser.getId());
		}
	}

	@Test
	@Transactional
	@DisplayName("User 테스트")
	public void test1() {
		// Given
		userRepository.save(testUser);

		// When
		User foundUser = userRepository.findById(testUser.getId()).orElse(null);

		// Then
		assertEquals(testUser.getUsername(), foundUser.getUsername());
		assertEquals(testUser.getPassword(), foundUser.getPassword());
		assertEquals(testUser.getName(), foundUser.getName());
		assertEquals(testUser.getNickname(), foundUser.getNickname());
		assertEquals(testUser.getEmail(), foundUser.getEmail());
		assertEquals(testUser.getIntroduce(), foundUser.getIntroduce());
		assertEquals(testUser.getUserStatus(), foundUser.getUserStatus());
		assertEquals(testUser.getRefreshToken(), foundUser.getRefreshToken());
		assertEquals(testUser.getAuthKey(), foundUser.getAuthKey());
		assertEquals(testUser.getVerifyTime(), foundUser.getVerifyTime());
	}

	@Test
	@Transactional
	@DisplayName("Newsfeed 테스트")
	public void test2() {
		// Given
		newsfeedRepository.save(testNewsfeed);

		// When
		Newsfeed foundNewsfeed = newsfeedRepository.findById(testNewsfeed.getId()).orElse(null);

		// Then
		assertEquals(testNewsfeed.getTitle(), foundNewsfeed.getTitle());
		assertEquals(testNewsfeed.getContent(), foundNewsfeed.getContent());
		assertEquals(testNewsfeed.getUsername(), foundNewsfeed.getUsername());
		assertEquals(testNewsfeed.getLikes(), foundNewsfeed.getLikes());
		assertEquals(testNewsfeed.getUser(), foundNewsfeed.getUser());
	}

	@Test
	@Transactional
	@DisplayName("Comment 테스트")
	public void test3() {
		// Given
		commentRepository.save(testComment);

		// When
		Comment foundComment = commentRepository.findById(testComment.getId()).orElse(null);

		// Then
		assertEquals(testComment.getUsername(), foundComment.getUsername());
		assertEquals(testComment.getNickname(), foundComment.getNickname());
		assertEquals(testComment.getComment(), foundComment.getComment());
		assertEquals(testComment.getGoodCounting(), foundComment.getGoodCounting());
		assertEquals(testComment.getNewsfeed().getId(), foundComment.getNewsfeed().getId());
	}

	@Test
	@Transactional
	@DisplayName("Like 테스트")
	public void test4() {
		// Given
		likeRepository.save(testLike);

		// When
		Like foundLike = likeRepository.findById(testLike.getId()).orElse(null);

		// Then
		assertEquals(testLike.getUser().getUsername(), foundLike.getUser().getUsername());
		assertEquals(testLike.getNewsfeed().getId(), foundLike.getNewsfeed().getId());
		assertEquals(testLike.getComment().getId(), foundLike.getComment().getId());
	}
}
