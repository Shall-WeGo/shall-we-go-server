package com.sam.shallwego.domain.location.entity;

import com.sam.shallwego.global.exception.BusinessException;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "location", indexes = @Index(name = "location_uq_ix_address", columnList = "address"))
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;     // 주소

    @Column(length = 100, nullable = false)
    private String place;       // 장소명

    @Column(nullable = false)
    private double coordinateX;

    @Column(nullable = false)
    private double coordinateY;

    public static class NotExistsException extends BusinessException {
        public NotExistsException() {
            super(HttpStatus.NOT_FOUND, "존재하지 않은 장소입니다.");
        }
    }
}
