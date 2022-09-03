package com.sam.shallwego.domain.member.service;

import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements ReactiveUserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> memberRepository.findByUsername(username)
                .orElseThrow(Member.NotExistsException::new))
                .cast(UserDetails.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<SignRO> registerMember(final SignDto signDto) {
        return Mono.fromCallable(() -> memberRepository.save(signDto.toEntity(passwordEncoder)))
                .map(SignRO::new)
                .subscribeOn(Schedulers.boundedElastic())
                .log();
    }

    public Mono<LoginRO> loginMember(final SignDto signDto) {
        return Mono.fromCallable(() -> memberRepository.findByUsername(signDto.getUsername())
                .orElseThrow(Member.NotExistsException::new)).flatMap(member -> {
            if (!passwordEncoder.matches(signDto.getPassword(), member.getPassword())) {
                return Mono.error(Member.InvalidPasswordException::new);
            }

            return Mono.just(new LoginRO(
                    jwtUtil.generateAccessToken(String.valueOf(member.getId())),
                    jwtUtil.generateRefreshToken(String.valueOf(member.getId()))
            ));
        }).subscribeOn(Schedulers.boundedElastic())
        .log();
    }
}
