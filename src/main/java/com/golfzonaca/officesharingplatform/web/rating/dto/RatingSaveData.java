package com.golfzonaca.officesharingplatform.web.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RatingSaveData {
    @NotBlank
    private String RatingScore;
    @NotBlank
    private String RatingReview;
    @NotBlank
    private String RatingTime;
}
