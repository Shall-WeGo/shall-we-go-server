package com.sam.shallwego.domain.savelocation.entity;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.global.exception.BusinessException;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "save_location")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveLocation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Location location;

    public static class NotSavedException extends BusinessException {
        public NotSavedException() {
            super(HttpStatus.CONFLICT, "저장되지 않은 장소입니다.");
        }
    }

    public static class AlreadySavedException extends BusinessException {
        public AlreadySavedException() {
            super(HttpStatus.CONFLICT, "이미 저장된 장소입니다.");
        }
    }
}
