package com.sam.shallwego.domain.savelocation.controller;

import com.sam.shallwego.domain.review.ro.ReviewRO;
import com.sam.shallwego.domain.savelocation.dto.LocationDto;
import com.sam.shallwego.domain.savelocation.ro.SaveLocationRO;
import com.sam.shallwego.domain.savelocation.service.LocationService;

import com.sam.shallwego.global.content.ExceptionSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
@Tag(name = "장소 API", description = "장소를 저장 및 조회, 삭제하는 것을 목적으로 하는 API")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "장소 저장", requestBody
            = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "location: 장소"),
            parameters = @Parameter(in = ParameterIn.HEADER, description = "Bearer {ACCESS-TOKEN}",
                    name = "Authorization", schema = @Schema(type = "string"))
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "장소를 성공적으로 저장하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Mono<SaveLocationRO> saveLocation(Mono<Authentication> authenticationMono,
                                   @RequestBody @Valid LocationDto locationDto) {
        return authenticationMono
                .map(authentication -> authentication.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(token -> locationService.saveLocation(token, locationDto));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "저장된 장소 취소",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "address-id", required = true,
                            schema = @Schema(type = "$int64")),
                    @Parameter(in = ParameterIn.HEADER, description = "Bearer {ACCESS-TOKEN}",
                            name = "Authorization", schema = @Schema(type = "string"))
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "성공적으로 저장을 취소하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하거나," +
                            " 장소에 저장된 리뷰가 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class))),
                    @ApiResponse(responseCode = "409", description = "저장되지 않은 장소입니다.")
            }
    )
    public Mono<Object> deleteLocation(Mono<Authentication> authenticationMono,
                                         @RequestParam(value = "address-id", required = false) long addressId) {
        return authenticationMono
                .map(authentication -> authentication.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(token -> locationService.deleteLocation(token, addressId));
    }

    @GetMapping(produces = "application/stream+json")
    @Operation(description = "저장된 장소 조회",
            parameters = @Parameter(in = ParameterIn.HEADER, description = "Bearer {ACCESS-TOKEN}",
                    name = "Authorization", schema = @Schema(type = "string"))
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "회원에게 저장된 장소를 모두 조회하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Flux<SaveLocationRO> findAllLocation(Mono<Authentication> authenticationMono) {
        return authenticationMono
                .map(authentication -> authentication.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMapMany(locationService::findLocationsByMember);
    }
}
