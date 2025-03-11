package com.example.be4.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantListResponseDto {

    private Long id;

    private String name;

    private String address;

    private Double lat;

    private Double lng;

    private String title;

}
