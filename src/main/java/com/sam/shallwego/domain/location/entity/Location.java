package com.sam.shallwego.domain.location.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "location")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String address;

}
