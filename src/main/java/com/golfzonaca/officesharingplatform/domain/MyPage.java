package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyPage {
    private String userName;
    private LocalDateTime joinDate;
    private Long mileagePoint;
    private Integer totalReviewNumber;
}
