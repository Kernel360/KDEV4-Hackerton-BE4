package com.example.be4.restaurant.service;

import com.example.be4.restaurant.dto.RestaurantListResponseDto;
import com.example.be4.restaurant.dto.RestaurantUpdateDto;
import com.example.be4.restaurant.domain.RestaurantInfoEntity;
import com.example.be4.restaurant.domain.RestaurantPostEntity;
import com.example.be4.restaurant.dto.RestaurantRequestDto;
import com.example.be4.restaurant.dto.RestaurantResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomMapper {

    // RestaurantPostEntity -> RestaurantResponseDto
    @Mapping(source = "infoEntity.name", target = "name")
    @Mapping(source = "infoEntity.address", target = "address")
    @Mapping(source = "infoEntity.lat", target = "lat")
    @Mapping(source = "infoEntity.lng", target = "lng")
    RestaurantResponseDto postToResponse(RestaurantPostEntity postEntity);

    //RestaurantResponseDto -> RestaurantPostEntity (역변환)
    @InheritInverseConfiguration(name = "postToResponse")
    @Mapping(target = "infoEntity", ignore = true)
    RestaurantPostEntity responseToPost(RestaurantResponseDto responseDto);

    // RestaurantRequestDto -> RestaurantPostEntity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())") // 현재 시간 설정
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "infoEntity", ignore = true)
    RestaurantPostEntity requestToPost(RestaurantRequestDto requestDto);

    // RestaurantRequestDto -> RestaurantInfoEntity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posts", ignore = true) // 컬렉션 필드 무시
    RestaurantInfoEntity requestToInfo(RestaurantRequestDto requestDto);

    //RestaurantInfoEntity -> RestaurantResponseDto (식당 정보만 변환)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    RestaurantResponseDto infoToResponse(RestaurantInfoEntity infoEntity);

    // RestaurantPostEntity -> RestaurantListResponseDto
    @Mapping(source = "id", target = "id")
    @Mapping(source = "infoEntity.name", target = "name")
    @Mapping(source = "infoEntity.address", target = "address")
    @Mapping(source = "infoEntity.lat", target = "lat")
    @Mapping(source = "infoEntity.lng", target = "lng")
    @Mapping(source = "title", target = "title")
    RestaurantListResponseDto postToListResponse(RestaurantPostEntity postEntity);


    //updatedto -> postEntity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(RestaurantUpdateDto updateDto, @MappingTarget RestaurantPostEntity entity);
}
