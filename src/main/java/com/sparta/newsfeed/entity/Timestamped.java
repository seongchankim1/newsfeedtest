package com.sparta.newsfeed.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private LocalDateTime writeDate; // 생성일자

	@Column(updatable = false)
	private LocalDateTime likeCreated; // 좋아요 생성일자

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updateDate; // 수정일자

	@Column
	private LocalDateTime statusChanged; // 상태변경시간

	@LastModifiedDate
	@Column
	private LocalDateTime likeUpdated; // 좋아요 수정일자

	public void updateUpdateDate() {
		this.updateDate = LocalDateTime.now();
	}

	public void updateStatusChanged() {
		this.statusChanged = LocalDateTime.now();
	}

	public void updateLikeCreated() {
		this.likeCreated = LocalDateTime.now();
	}

	public void updateLikeUpdated() {
		this.likeUpdated = LocalDateTime.now();
	}

}
