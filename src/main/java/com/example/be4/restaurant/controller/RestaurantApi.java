package com.example.be4.restaurant.controller;

import com.example.be4.restaurant.dto.*;
import com.example.be4.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantApi {

    private final RestaurantService restaurantService;

    //식당 게시글 추가
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@RequestBody RestaurantRequestDto requestDto) {
        RestaurantResponseDto saveRestaurant = restaurantService.createRestaurant(requestDto);
        return ResponseEntity.ok().body(saveRestaurant);
    }

    //식당 게시글 상세페이지
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(@PathVariable Long id) {
        RestaurantResponseDto getPost = restaurantService.getPost(id);
        return ResponseEntity.ok().body(getPost);
    }

    //식당 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> deleteRestaurant(@PathVariable Long id, @RequestBody RestaurantUpdateDto updateDto) {
        RestaurantResponseDto responseDto = restaurantService.updatePost(id, updateDto);
       return ResponseEntity.ok().body(responseDto);
    }

    //식당 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> deleteRestaurantById(@PathVariable Long id, @RequestBody RestaurantDeleteDto deleteDto) {
        RestaurantResponseDto responseDto  = restaurantService.deletePost(id, deleteDto);
        return ResponseEntity.ok(responseDto);
    }

    //식당 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<List<RestaurantListResponseDto>> getAllRestaurants() {
        List<RestaurantListResponseDto> listDto = restaurantService.getPostList();
        return ResponseEntity.ok(listDto);
    }

    //식당 게시글 검색
    @GetMapping("/posts/search")
    public ResponseEntity<List<RestaurantListResponseDto>> getAllRestaurantsByKeyword(@RequestParam String keyword) {
        List<RestaurantListResponseDto> listDto = restaurantService.getListByKeyword(keyword);
        return ResponseEntity.ok(listDto);
    }
}
