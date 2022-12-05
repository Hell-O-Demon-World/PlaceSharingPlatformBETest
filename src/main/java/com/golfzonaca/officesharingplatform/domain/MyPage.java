package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPage {
    private String userName;
    private String joinDate;
    private String mileagePoint;
    private Integer totalReviewNumber;
}
