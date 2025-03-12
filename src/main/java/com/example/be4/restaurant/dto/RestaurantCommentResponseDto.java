package com.example.be4.restaurant.dto;

import com.example.be4.restaurant.domain.RestaurantCommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCommentResponseDto {
	private long id;
	private long postId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static RestaurantCommentResponseDto of(RestaurantCommentEntity entity) {
		return RestaurantCommentResponseDto.builder()
				.id(entity.getId())
				.postId(entity.getPostEntity().getId())
				.content(entity.getContent())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
	}

	public static List<RestaurantCommentResponseDto> of(List<RestaurantCommentEntity> entities) {
		return entities.stream().map(RestaurantCommentResponseDto::of).toList();
	}
}