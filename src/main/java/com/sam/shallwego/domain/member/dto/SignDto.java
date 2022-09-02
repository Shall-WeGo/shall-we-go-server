package com.sam.shallwego.domain.member.dto;

import com.sam.shallwego.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class SignDto {

    @NotBlank
    @Size(min = 4, max = 80)
    private String username;

    @NotBlank
    @Size(min = 8, max = 80)
    private String password;

    public Member toEntity(PasswordEncoder encoder) {
        return Member.builder()
                .username(username)
                .password(encoder.encode(password))
                .build();
    }
}
