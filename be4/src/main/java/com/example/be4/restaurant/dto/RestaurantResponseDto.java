package com.example.be4.restaurant.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class RestaurantResponseDto {

    private Long id;

    private String title;

    private String content;

    private String name;

    private String address;

    private Double lat;

    private Double lng;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String category;

    private Integer tasteRating;  // 음식의 맛

    private Integer speedRating;  // 음식이 나오는 속도

    private Integer priceRating;   // 가격 적합도

}
