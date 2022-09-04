package com.sam.shallwego.domain.member.controller;

import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public Mono<SignRO> register(@RequestBody @Valid SignDto signDto) {
        return memberService.registerMember(signDto);
    }

    @PostMapping("/login")
    public Mono<LoginRO> login(@RequestBody @Valid SignDto signDto) {
        return memberService.loginMember(signDto);
    }
}
