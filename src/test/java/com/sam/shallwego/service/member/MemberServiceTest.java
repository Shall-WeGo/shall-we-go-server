package com.sam.shallwego.service.member;

import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ValidationException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 가입 실패 테스트")
    void registerFail() {
        // given
        SignDto signDto = new SignDto();
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new ValidationException("must not be blank"));

        // when
        Mono<SignRO> signROMono = memberService.registerMember(signDto);

        // then
        StepVerifier.create(signROMono)
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void registerSuccess() {
        // given
        SignDto signDto = new SignDto("username", "password");
        Member member = new Member(1L, signDto.getUsername(), signDto.getPassword());
        SignRO signRO = new SignRO(member);
        when(memberRepository.save(any(Member.class)))
                .thenReturn(member);

        // when
        Mono<SignRO> signROMono = memberService.registerMember(signDto)
                        .thenReturn(signRO);

        // then
        StepVerifier.create(signROMono)
                .expectNext(signRO)
                .expectComplete()
                .verify();
    }
}
