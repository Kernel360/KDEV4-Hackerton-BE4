package com.example.be4.restaurant.domain;

import com.example.be4.restaurant.dto.RestaurantCommentRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "restaurant_comment")
public class RestaurantCommentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Setter private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_post_id")
    public RestaurantPostEntity postEntity;

    public static RestaurantCommentEntity of(RestaurantPostEntity postEntity, RestaurantCommentRequestDto dto) {
        return RestaurantCommentEntity.builder()
                .postEntity(postEntity)
                .content(dto.getContent())
                .password(dto.getPassword())
                .build();
    }
}