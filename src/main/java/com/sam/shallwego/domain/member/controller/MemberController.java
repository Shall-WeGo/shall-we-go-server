package com.sam.shallwego.domain.member.controller;

import com.sam.shallwego.domain.member.dto.ReissueDto;
import com.sam.shallwego.domain.member.dto.SignDto;
import com.sam.shallwego.domain.member.ro.LoginRO;
import com.sam.shallwego.domain.member.ro.ReissueRO;
import com.sam.shallwego.domain.member.ro.SignRO;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.global.content.ExceptionSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "회원 API", description = "회원가입, 로그인, 토큰 재발급을 목적으로 하는 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "회원 회원가입", requestBody
            = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true, description = "닉네임(최소4자, 최대 80자), 비밀번호(최소 8자, 최대 80자)")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "성공적으로 회원에 성공하였습니다."),
                    @ApiResponse(responseCode = "409", description = "이미 해당 닉네임으로 회원가입된 회원이 존재합니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Mono<SignRO> register(@RequestBody @Valid SignDto signDto) {
        return memberService.registerMember(signDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "회원 로그인", requestBody
            = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "닉네임(최소4자, 최대 80자), 비밀번호(최소 8자, 최대 80자)")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 회원에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class))),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Mono<LoginRO> login(@RequestBody @Valid SignDto signDto) {
        return memberService.loginMember(signDto);
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "토큰 재발급", requestBody
            = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "Refresh Token")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "성공적으로 토큰을 재발급받았습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Mono<ReissueRO> reissueToken(@RequestBody @Valid ReissueDto reissueDto) {
        return memberService.reissueToken(reissueDto);
    }
}
