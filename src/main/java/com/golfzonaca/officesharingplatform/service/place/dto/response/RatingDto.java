package com.golfzonaca.officesharingplatform.service.place.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RatingDto {
    private String ratingId;
    private String ratingScore;
    private String ratingWriter;
    private String writeDate;
    private String writeTime;
    private String usedRoomType;
    private String ratingContent;
    private String commentQuantity;
}
