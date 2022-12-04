package com.golfzonaca.officesharingplatform.service.place.dto.response;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RatingDto {
    private String ratingId;
    private String ratingScore;
    private String ratingWriter;
    private String writeDateTime;
    private String usedRoomType;
    private String ratingContent;
    private List<String> commentList;
}
