package com.sam.shallwego.domain.member.controller;

import com.sam.shallwego.domain.member.dto.ReissueDto;
import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.ReissueRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/reissue")
    public Mono<ReissueRO> reissueToken(@RequestBody @Valid ReissueDto reissueDto) {
        return memberService.reissueToken(reissueDto);
    }
}
