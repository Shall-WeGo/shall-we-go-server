package com.sam.shallwego.domain.review.controller;

import com.sam.shallwego.domain.review.dto.ReviewDto;
import com.sam.shallwego.domain.review.repository.HighRateReview;
import com.sam.shallwego.domain.review.ro.ReviewRO;
import com.sam.shallwego.domain.review.service.ReviewService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "리뷰 API", description = "리뷰를 작성 및 조회, 추천 목록을 조회하는 것을 목적으로 하는 API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "리뷰 작성", requestBody
            = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "location: 장소, review_message: 리뷰, rate: null 혹은 0부터 5사이 정수"),
            parameters = @Parameter(in = ParameterIn.HEADER, description = "Bearer {ACCESS-TOKEN}",
                    name = "Authorization", schema = @Schema(type = "string"))
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "성공적으로 리뷰를 남겼습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Mono<ReviewRO> addReview(Mono<Authentication> authentication,
                                    @RequestBody @Valid ReviewDto reviewDto) {
        return authentication
                .map(auth -> auth.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(token -> reviewService.writeReview(token, reviewDto));
    }

    @GetMapping(value = "{address}", produces = "application/stream+json")
    @Operation(description = "장소로 리뷰 전체 조회",
            parameters = @Parameter(in = ParameterIn.PATH, name = "address", required = true)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "장소에 작성된 모든 리뷰를 조회하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 위치에 저장된 리뷰가 하나도 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Flux<ReviewRO> findReviewByAddress(@PathVariable("address") String address) {
        return reviewService.findAllReview(address);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "리뷰 삭제",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "address", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, description = "Bearer {ACCESS-TOKEN}",
                            name = "Authorization", schema = @Schema(type = "string"))
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "성공적으로 리뷰를 삭제하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 회원이 존재하거나," +
                            " 장소에 저장된 리뷰가 존재하지 않습니다.",
                            content = @Content(schema = @Schema(oneOf = ExceptionSchema.class)))
            }
    )
    public Mono<Void> deleteReview(Mono<Authentication> authentication,
                                   @RequestParam("address") String address) {
        return authentication
                .map(auth -> auth.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(token -> reviewService.deleteReview(token, address));
    }

    @GetMapping(produces = "application/stream+json")
    @Operation(description = "높은 평점으로 추천 장소 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "높은 평점으로 추천 장소 조회하였습니다.")
            }
    )
    public Flux<HighRateReview> findAllHighReview() {
        return reviewService.findAllByHighRate();
    }
}
