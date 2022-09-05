package com.sam.shallwego.service.member;

import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.global.jwt.JwtUtil;
import com.sam.shallwego.global.jwt.config.JwtSecretConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.ValidationException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtSecretConfig jwtSecretConfig = new JwtSecretConfig();

    @Spy
    private JwtUtil jwtUtil = new JwtUtil(
            jwtSecretConfig.setInit("accessSecret", "refreshSecret")
    );

    @Test
    @DisplayName("회원 가입 실패 테스트")
    void registerFail() {
        // given
        SignDto signDto = new SignDto("", "1234");
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new ValidationException(""));

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

    @Test
    @DisplayName("로그인 실패 테스트")
    void loginFailed() {
        // given
        SignDto signDto = new SignDto("username", "password");
        when(memberRepository.findByUsername(any()))
                .thenThrow(new Member.NotExistsException());

        // when
        Mono<LoginRO> signROMono = memberService.loginMember(signDto);

        // then
        StepVerifier.create(signROMono)
                .expectError(Member.NotExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // given
        SignDto signDto = new SignDto("username", "password");
        Member member = new Member(
                1L,
                signDto.getUsername(),
                passwordEncoder.encode(signDto.getPassword())
        );
        LoginRO loginRO = new LoginRO(
                jwtUtil.generateAccessToken(member.getUsername()),
                jwtUtil.generateRefreshToken(member.getPassword())
        );
        when(memberRepository.findByUsername(any()))
                .thenReturn(Optional.of(member));

        // when
        Mono<LoginRO> signROMono = memberService.loginMember(signDto)
                        .thenReturn(loginRO);

        // then
        StepVerifier.create(signROMono)
                .expectNext(loginRO)
                .expectComplete()
                .verify();
    }
}
