package com.golfzonaca.officesharingplatform.web.reservation.form;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.RoomImage;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReservationResponseForm {
    private Long reservationId;
    private RoomType roomType;
    private String placeName;
    private List<String> placeImgUrl;
    private Float totalReview;
    private Integer currentRate;
    private String reservationStartDate;
    private String reservationStartTime;
    private String reservationEndDate;
    private String reservationEndTime;
    private Integer itemPrice;
    private Long totalPrice;
    private Long totalMileage;

    public void toEntity(Reservation savedReservation, User user) {
        Long totalPrice = getTotalPrice(savedReservation);
        List<RoomImage> roomImages = savedReservation.getRoom().getPlace().getRoomImages();
        List<String> urls = getUrls(roomImages);
        this.reservationId = savedReservation.getId();
        this.roomType = savedReservation.getRoom().getRoomKind().getRoomType();
        this.placeName = savedReservation.getRoom().getPlace().getPlaceName();
        this.placeImgUrl = urls;
        this.totalReview = savedReservation.getRoom().getPlace().getRatePoint().getRatingPoint();
        this.reservationStartDate = savedReservation.getResStartDate().toString();
        this.reservationStartTime = savedReservation.getResStartTime().toString();
        this.reservationEndDate = savedReservation.getResEndDate().toString();
        this.reservationEndTime = savedReservation.getResEndTime().toString();
        this.itemPrice = savedReservation.getRoom().getRoomKind().getPrice();
        this.totalPrice = totalPrice;
        this.totalMileage = user.getMileage().getPoint();
    }

    private List<String> getUrls(List<RoomImage> roomImages) {
        List<String> urls = new LinkedList<>();
        for (RoomImage roomImage : roomImages) {
            urls.add(roomImage.getSavedPath());
        }
        return urls;
    }

    private Long getTotalPrice(Reservation savedReservation) {
        long count = 0;
        if (savedReservation.getRoom().getRoomKind().getRoomType().toString().contains("OFFICE")) {
            count = ChronoUnit.DAYS.between(savedReservation.getResStartDate(), savedReservation.getResEndDate());
        } else {
            count = savedReservation.getResEndTime().getHour() - savedReservation.getResStartTime().getHour();
        }
        Long totalPrice = count * savedReservation.getRoom().getRoomKind().getPrice();
        return totalPrice;
    }
}
