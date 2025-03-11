package com.example.be4.restaurant.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "restaurant_post")
public class RestaurantPostEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String password;

    @Column
    private String category;

    @Column
    private Integer tasteRating;  // 음식의 맛 (1~5)

    @Column
    private Integer speedRating;  // 음식이 나오는 속도 (1~5)

    @Column
    private Integer priceRating;  // 가격 적합도 (1~5)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_info_id")
    public RestaurantInfoEntity infoEntity;

}
