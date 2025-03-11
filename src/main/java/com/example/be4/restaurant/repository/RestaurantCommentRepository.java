package com.example.be4.restaurant.repository;

import com.example.be4.restaurant.domain.RestaurantCommentEntity;
import com.example.be4.restaurant.domain.RestaurantPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RestaurantCommentRepository extends JpaRepository<RestaurantCommentEntity, Long> {
    List<RestaurantCommentEntity> findAllByPostEntity(RestaurantPostEntity postEntity);
    RestaurantCommentEntity findByIdAndPostEntity(Long commentId, RestaurantPostEntity postEntity);
}
