package com.sam.shallwego.domain.member.service;

import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

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
                    jwtUtil.generateAccessToken(member.getUsername()),
                    jwtUtil.generateRefreshToken(member.getUsername())
            ));
        }).subscribeOn(Schedulers.boundedElastic())
        .log();
    }

    public Mono<SignRO> getMemberInfo(long memberId) {
        return Mono.fromCallable(() -> memberRepository.findById(memberId)
                .orElseThrow(Member.NotExistsException::new))
                .map(SignRO::new)
                .subscribeOn(Schedulers.boundedElastic())
                .log();
    }
}
