package com.sparta.newsfeed.dto;

import com.sparta.newsfeed.entity.User;

import lombok.Getter;

@Getter
public class VerifyRequestDto {
	private String username;
	private String password;
	private String authKey;

	public VerifyRequestDto(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.authKey = user.getAuthKey();
	}
}
