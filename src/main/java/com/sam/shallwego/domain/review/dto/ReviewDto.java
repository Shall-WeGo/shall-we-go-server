package com.sam.shallwego.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class ReviewDto {

    @NotBlank
    private String location;

    @NotBlank
    @JsonProperty("review_message")
    private String reviewMessage;

    @Nullable
    private Short rate;

}
