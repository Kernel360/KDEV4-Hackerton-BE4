package com.example.be4.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "restaurant_info")
@NoArgsConstructor
@Getter @Setter
public class RestaurantInfoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15)
    private Double lat;

    @Column(length = 15)
    private Double lng;

    @Column(length = 50)
    private String address;

    @Column(length = 30)
    private String name;

    @OneToMany(mappedBy = "infoEntity")
    private List<RestaurantPostEntity> posts;

}
