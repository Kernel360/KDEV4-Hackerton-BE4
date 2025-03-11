package com.example.be4.domain;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class RestaurantInfoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15, nullable = false)
    private Double lat;

    @Column(length = 15, nullable = false)
    private Double lng;

    @Column(length = 50, nullable = false)
    private String address;

    @Column(length = 30, nullable = false)
    private String name;

    @OneToMany
    private List<RestaurantEntity> res;
}
