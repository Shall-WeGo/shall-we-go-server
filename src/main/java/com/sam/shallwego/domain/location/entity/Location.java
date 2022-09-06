package com.sam.shallwego.domain.location.entity;

import com.sam.shallwego.global.exception.BusinessException;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "location")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String address;

    public static class NotExistsException extends BusinessException {
        public NotExistsException() {
            super(HttpStatus.NOT_FOUND, "존재하지 않은 장소입니다.");
        }
    }

}
