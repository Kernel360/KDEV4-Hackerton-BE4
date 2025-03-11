package com.example.be4.restaurant.repository;

import com.example.be4.restaurant.domain.RestaurantPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface RestaurantPostRepository extends JpaRepository<RestaurantPostEntity, Long> {

    //식당 게시글 추가

    //식당 게시글 목록 조회

    //식당 게시글 상세 조회

    //식당 게시글 삭제

    // 제목에 키워드가 포함된 게시글 검색 (대소문자 구분 X)
    @Query("SELECT p FROM RestaurantPostEntity p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RestaurantPostEntity> searchByTitle(@Param("keyword") String keyword);
}
