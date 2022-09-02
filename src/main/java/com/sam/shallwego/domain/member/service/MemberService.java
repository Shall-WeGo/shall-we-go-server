package com.sam.shallwego.domain.member.service;

import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class MemberService implements ReactiveUserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> memberRepository.findByUsername(username)
                .orElseThrow(Member.NotExistsException::new))
                .cast(UserDetails.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
