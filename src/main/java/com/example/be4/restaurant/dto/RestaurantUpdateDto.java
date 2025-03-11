package com.example.be4.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RestaurantUpdateDto {

    private String title;

    private String content;

    private LocalDateTime updatedAt;

    private String password;

    private String category;

    private Integer tasteRating;

    private Integer speedRating;

    private Integer priceRating;
}
