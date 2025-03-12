package com.example.be4.restaurant.dto;

import com.example.be4.restaurant.domain.RestaurantCommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCommentRequestDto {
	private long id;
	private String content;
	private String password;

	public static RestaurantCommentRequestDto of(RestaurantCommentEntity entity) {
		return RestaurantCommentRequestDto.builder()
				.id(entity.getId())
				.content(entity.getContent())
				.password(entity.getPassword())
				.build();
	}

	public static List<RestaurantCommentRequestDto> of(List<RestaurantCommentEntity> entities) {
		return entities.stream().map(RestaurantCommentRequestDto::of).toList();
	}
}