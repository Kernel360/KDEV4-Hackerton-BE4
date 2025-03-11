package com.example.be4.restaurant.service;

import com.example.be4.restaurant.domain.RestaurantInfoEntity;
import com.example.be4.restaurant.domain.RestaurantPostEntity;
import com.example.be4.restaurant.dto.RestaurantListResponseDto;
import com.example.be4.restaurant.dto.RestaurantRequestDto;
import com.example.be4.restaurant.dto.RestaurantResponseDto;
import com.example.be4.restaurant.dto.RestaurantUpdateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-11T15:33:00+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class CustomMapperImpl implements CustomMapper {

    @Override
    public RestaurantResponseDto postToResponse(RestaurantPostEntity postEntity) {
        if ( postEntity == null ) {
            return null;
        }

        RestaurantResponseDto restaurantResponseDto = new RestaurantResponseDto();

        restaurantResponseDto.setName( postEntityInfoEntityName( postEntity ) );
        restaurantResponseDto.setAddress( postEntityInfoEntityAddress( postEntity ) );
        restaurantResponseDto.setLat( postEntityInfoEntityLat( postEntity ) );
        restaurantResponseDto.setLng( postEntityInfoEntityLng( postEntity ) );
        restaurantResponseDto.setId( postEntity.getId() );
        restaurantResponseDto.setTitle( postEntity.getTitle() );
        restaurantResponseDto.setContent( postEntity.getContent() );
        restaurantResponseDto.setCreatedAt( postEntity.getCreatedAt() );
        restaurantResponseDto.setUpdatedAt( postEntity.getUpdatedAt() );
        restaurantResponseDto.setCategory( postEntity.getCategory() );
        restaurantResponseDto.setTasteRating( postEntity.getTasteRating() );
        restaurantResponseDto.setSpeedRating( postEntity.getSpeedRating() );
        restaurantResponseDto.setPriceRating( postEntity.getPriceRating() );

        return restaurantResponseDto;
    }

    @Override
    public RestaurantPostEntity responseToPost(RestaurantResponseDto responseDto) {
        if ( responseDto == null ) {
            return null;
        }

        RestaurantPostEntity restaurantPostEntity = new RestaurantPostEntity();

        restaurantPostEntity.setId( responseDto.getId() );
        restaurantPostEntity.setTitle( responseDto.getTitle() );
        restaurantPostEntity.setContent( responseDto.getContent() );
        restaurantPostEntity.setCreatedAt( responseDto.getCreatedAt() );
        restaurantPostEntity.setUpdatedAt( responseDto.getUpdatedAt() );
        restaurantPostEntity.setCategory( responseDto.getCategory() );
        restaurantPostEntity.setTasteRating( responseDto.getTasteRating() );
        restaurantPostEntity.setSpeedRating( responseDto.getSpeedRating() );
        restaurantPostEntity.setPriceRating( responseDto.getPriceRating() );

        return restaurantPostEntity;
    }

    @Override
    public RestaurantPostEntity requestToPost(RestaurantRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        RestaurantPostEntity restaurantPostEntity = new RestaurantPostEntity();

        restaurantPostEntity.setTitle( requestDto.getTitle() );
        restaurantPostEntity.setContent( requestDto.getContent() );
        restaurantPostEntity.setPassword( requestDto.getPassword() );
        restaurantPostEntity.setCategory( requestDto.getCategory() );
        restaurantPostEntity.setTasteRating( requestDto.getTasteRating() );
        restaurantPostEntity.setSpeedRating( requestDto.getSpeedRating() );
        restaurantPostEntity.setPriceRating( requestDto.getPriceRating() );

        restaurantPostEntity.setCreatedAt( java.time.LocalDateTime.now() );

        return restaurantPostEntity;
    }

    @Override
    public RestaurantInfoEntity requestToInfo(RestaurantRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        RestaurantInfoEntity restaurantInfoEntity = new RestaurantInfoEntity();

        restaurantInfoEntity.setLat( requestDto.getLat() );
        restaurantInfoEntity.setLng( requestDto.getLng() );
        restaurantInfoEntity.setAddress( requestDto.getAddress() );
        restaurantInfoEntity.setName( requestDto.getName() );

        return restaurantInfoEntity;
    }

    @Override
    public RestaurantResponseDto infoToResponse(RestaurantInfoEntity infoEntity) {
        if ( infoEntity == null ) {
            return null;
        }

        RestaurantResponseDto restaurantResponseDto = new RestaurantResponseDto();

        restaurantResponseDto.setName( infoEntity.getName() );
        restaurantResponseDto.setAddress( infoEntity.getAddress() );
        restaurantResponseDto.setLat( infoEntity.getLat() );
        restaurantResponseDto.setLng( infoEntity.getLng() );

        return restaurantResponseDto;
    }

    @Override
    public RestaurantListResponseDto postToListResponse(RestaurantPostEntity postEntity) {
        if ( postEntity == null ) {
            return null;
        }

        RestaurantListResponseDto restaurantListResponseDto = new RestaurantListResponseDto();

        restaurantListResponseDto.setId( postEntity.getId() );
        restaurantListResponseDto.setName( postEntityInfoEntityName( postEntity ) );
        restaurantListResponseDto.setAddress( postEntityInfoEntityAddress( postEntity ) );
        restaurantListResponseDto.setLat( postEntityInfoEntityLat( postEntity ) );
        restaurantListResponseDto.setLng( postEntityInfoEntityLng( postEntity ) );
        restaurantListResponseDto.setTitle( postEntity.getTitle() );

        return restaurantListResponseDto;
    }

    @Override
    public void updateFromDto(RestaurantUpdateDto updateDto, RestaurantPostEntity entity) {
        if ( updateDto == null ) {
            return;
        }

        if ( updateDto.getTitle() != null ) {
            entity.setTitle( updateDto.getTitle() );
        }
        if ( updateDto.getContent() != null ) {
            entity.setContent( updateDto.getContent() );
        }
        if ( updateDto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( updateDto.getUpdatedAt() );
        }
        if ( updateDto.getPassword() != null ) {
            entity.setPassword( updateDto.getPassword() );
        }
        if ( updateDto.getCategory() != null ) {
            entity.setCategory( updateDto.getCategory() );
        }
        if ( updateDto.getTasteRating() != null ) {
            entity.setTasteRating( updateDto.getTasteRating() );
        }
        if ( updateDto.getSpeedRating() != null ) {
            entity.setSpeedRating( updateDto.getSpeedRating() );
        }
        if ( updateDto.getPriceRating() != null ) {
            entity.setPriceRating( updateDto.getPriceRating() );
        }
    }

    private String postEntityInfoEntityName(RestaurantPostEntity restaurantPostEntity) {
        if ( restaurantPostEntity == null ) {
            return null;
        }
        RestaurantInfoEntity infoEntity = restaurantPostEntity.getInfoEntity();
        if ( infoEntity == null ) {
            return null;
        }
        String name = infoEntity.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String postEntityInfoEntityAddress(RestaurantPostEntity restaurantPostEntity) {
        if ( restaurantPostEntity == null ) {
            return null;
        }
        RestaurantInfoEntity infoEntity = restaurantPostEntity.getInfoEntity();
        if ( infoEntity == null ) {
            return null;
        }
        String address = infoEntity.getAddress();
        if ( address == null ) {
            return null;
        }
        return address;
    }

    private Double postEntityInfoEntityLat(RestaurantPostEntity restaurantPostEntity) {
        if ( restaurantPostEntity == null ) {
            return null;
        }
        RestaurantInfoEntity infoEntity = restaurantPostEntity.getInfoEntity();
        if ( infoEntity == null ) {
            return null;
        }
        Double lat = infoEntity.getLat();
        if ( lat == null ) {
            return null;
        }
        return lat;
    }

    private Double postEntityInfoEntityLng(RestaurantPostEntity restaurantPostEntity) {
        if ( restaurantPostEntity == null ) {
            return null;
        }
        RestaurantInfoEntity infoEntity = restaurantPostEntity.getInfoEntity();
        if ( infoEntity == null ) {
            return null;
        }
        Double lng = infoEntity.getLng();
        if ( lng == null ) {
            return null;
        }
        return lng;
    }
}
