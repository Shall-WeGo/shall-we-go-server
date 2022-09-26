package com.sam.shallwego.domain.member.service;

import com.sam.shallwego.domain.member.dto.ReissueDto;
import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.ReissueRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private final Scheduler memberSelectScheduler
            = Schedulers.fromExecutor(Executors.newFixedThreadPool(10));
    private final Scheduler memberSaveScheduler
            = Schedulers.fromExecutor(Executors.newFixedThreadPool(10));

    @Transactional
    public Mono<SignRO> registerMember(final SignDto signDto) {
        //noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> memberRepository.existsByUsername(signDto.getUsername()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(Member.AlreadyExistsException::new);
                    }
                    return Mono.empty();
                }).then(Mono.fromSupplier(() -> memberRepository.save(signDto.toEntity(passwordEncoder)))
                        .publishOn(memberSaveScheduler)
                        .flatMap(member -> Mono.defer(() -> Mono.just(new SignRO(member)))));
    }

    public Mono<LoginRO> loginMember(final SignDto signDto) {
        final String username = signDto.getUsername();
        final String password = signDto.getPassword();
        return memberMonoByUsername(signDto.getUsername())
                .subscribeOn(Schedulers.single())
                .flatMap(member -> {
                    if (!passwordEncoder.matches(password, member.getPassword())) {
                        return Mono.error(Member.InvalidPasswordException::new);
                    }

                    return Mono.just(new LoginRO(
                            jwtUtil.generateAccessToken(username),
                            jwtUtil.generateRefreshToken(username)
                    ));
        });
    }

    public Mono<ReissueRO> reissueToken(final ReissueDto reissueDto) {
        String username = jwtUtil.extractUsernameFromToken(reissueDto.getRefreshToken(), "refresh");
        return memberMonoByUsername(username)
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(new ReissueRO(jwtUtil.generateAccessToken(username)));
    }

    public Mono<Member> memberMonoByUsername(String username) {
        //noinspection BlockingMethodInNonBlockingContext
        return Mono.fromCallable(() -> memberRepository.findByUsername(username)
                .orElseThrow(Member.NotExistsException::new))
                .publishOn(memberSelectScheduler);
    }
}
