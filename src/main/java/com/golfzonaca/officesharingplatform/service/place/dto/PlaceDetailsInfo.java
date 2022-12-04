package com.golfzonaca.officesharingplatform.service.place.dto;

import com.golfzonaca.officesharingplatform.service.place.dto.response.RatingDto;
import com.golfzonaca.officesharingplatform.service.place.dto.response.RoomTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceDetailsInfo {
    private String placeId;
    private String placeName;
    private String placePostalCode;
    private String placeAddress;
    private List<String> placeMainInfo;
    private List<String> placeImage;
    private String ratePoint;
    private String reviewQuantity;
    private String deskQuantity;
    private String meetingRoomQuantity;
    private String officeQuantity;
    private String placeDescription;
    private List<String> placeCloseDays;
    private String placeOpenTime;
    private String placeCloseTime;
    RoomTypeResponse roomTypeResponse;
    private List<RatingDto> ratingList;
}
