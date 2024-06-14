package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.SignupRequestDto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table
public class User extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID

	@Column(nullable = false, unique = true)
	private String username; // 사용자 ID

	@Column(nullable = false)
	private String password; // 비밀번호

	@Column(nullable = false)
	private String name; // 이름

	@Email
	@NotBlank
	private String email; // 이메일

	private String nickname; // 별칭, 별명

	@Column(nullable = false)
	private String introduce; // 한 줄 소개

	@Column(nullable = false)
	private String userStatus; // 회원상태코드

	@Column
	private String refreshToken;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Newsfeed> newsfeedList = new ArrayList<>();

	@Column
	private String authKey;

	@Column
	private LocalDateTime verifyTime;

	@Enumerated(value = EnumType.STRING)
	private UserRoleEnum role = UserRoleEnum.USER;

	public User(String username, String password, String name, String nickname, String email, String introduce, String userStatus, String refreshToken, String authKey, LocalDateTime verifyTime) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.introduce = introduce;
		this.userStatus = userStatus;
		this.refreshToken = refreshToken;
		this.authKey = authKey;
		this.verifyTime = verifyTime;
	}

	public void updateStatus(String userStatus) {
		this.userStatus = userStatus;
		updateStatusChanged();
	}

	public void update(String nickname, String email, String introduce, String password) {
		this.nickname = nickname;
		this.email = email;
		this.introduce = introduce;
		this.password = password;
		updateUpdateDate();
	}
}
