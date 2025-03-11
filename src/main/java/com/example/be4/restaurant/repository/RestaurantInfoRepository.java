package com.example.be4.restaurant.repository;

import com.example.be4.restaurant.domain.RestaurantInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantInfoRepository extends JpaRepository<RestaurantInfoEntity, Long> {

    //식당 주소를 기준으로 식당 정보 있는지 검색
    Optional<RestaurantInfoEntity> findRestaurantInfoEntityByAddress(String address);

}

