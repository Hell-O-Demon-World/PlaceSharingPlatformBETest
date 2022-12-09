package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.RatingStatus;
import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import com.golfzonaca.officesharingplatform.domain.type.UsageStatus;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationSearchCond;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyPaymentDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class JpaMyPageService implements MyPageService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public UserData getUserData(Long userId) {
        User findUser = userRepository.findById(userId);
        Mileage mileage = findUser.getMileage();
        List<Reservation> findReservation = reservationRepository.findAllByUserId(userId);
        return UserData.builder().userName(findUser.getUsername()).joinDate(findUser.getJoinDate()).mileagePoint(mileage.getPoint()).totalReviewNumber(findReservation.size()).build();
    }

    @Override
    public void cancelByOrderAndUserId(Integer order, Long userId) {
        List<Reservation> reservationList = reservationRepository.findAllLimit(ReservationSearchCond.builder().userId(userId).build(), order + 1);
        reservationRepository.deleteById(reservationList.get(order).getId());
    }

    @Override
    public Map<String, JsonObject> getMyReservation(long userId) {
        Gson gson = new Gson();
        Map<String, JsonObject> myReservationList = new LinkedHashMap<>();
        User user = userRepository.findById(userId);
        JsonObject userData = gson.toJsonTree(getUserData(userId)).getAsJsonObject();
        myReservationList.put("userData", userData);
        List<Reservation> reservationList = user.getReservationList();

        for (int i = 0; i < reservationList.size(); i++) {
            Reservation reservation = reservationList.get(i);
            String usageStatus = getUsageStatus(reservation);
            String ratingStatus = getRatingStatus(reservation, usageStatus);
            JsonObject myReservationViewData = gson.toJsonTree(MyReservationList.builder().productType(reservation.getRoom().getRoomKind().getRoomType().getDescription()).placeName(reservation.getRoom().getPlace().getPlaceName()).reservationCompletedDateTime(reservation.getResCompleted()).reservationStartDateTime(LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime())).reservationEndDateTime(LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime())).usageStatus(usageStatus).ratingStatus(ratingStatus).build()).getAsJsonObject();
            myReservationList.put(String.valueOf(i), myReservationViewData);
        }
        return myReservationList;
    }

    @Override
    public Map<String, JsonObject> getUsageDetail(Long userId, long reservationId) {
        MyReservationDetail myReservationDetail = getMyReservationDetail(userId, reservationId);
        List<MyPaymentDetail> myPaymentDetail = getMyPaymentDetail(userId, reservationId);

        Gson gson = new Gson();
        Map<String, JsonObject> usageDetail = new LinkedHashMap<>();
        usageDetail.put("resData", gson.toJsonTree(myReservationDetail).getAsJsonObject());
        for (int i = 0; i < myPaymentDetail.size(); i++) {
            MyPaymentDetail paymentDetail = myPaymentDetail.get(i);
            usageDetail.put("payData" + i, gson.toJsonTree(paymentDetail).getAsJsonObject());
        }
        return usageDetail;
    }

    private MyReservationDetail getMyReservationDetail(Long userId, long reservationId) {
        Reservation reservation = reservationInfoValidation(userId, reservationId);
        return new MyReservationDetail(reservation.getRoom().getPlace().getPlaceName(), reservation.getRoom().getRoomKind().getRoomType().getDescription(), reservation.getResCompleted().toString(), LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).toString(), LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()).toString(), reservation.getStatus().toString(), String.valueOf(Optional.ofNullable(reservation.getRating()).isEmpty()));
    }

    private List<MyPaymentDetail> getMyPaymentDetail(Long userId, long reservationId) {
        Reservation reservation = reservationInfoValidation(userId, reservationId);
        List<MyPaymentDetail> paymentDetails = new LinkedList<>();
        for (Payment payment : reservation.getPaymentList()) {
            paymentDetails.add(new MyPaymentDetail(LocalDateTime.of(payment.getPayDate(), payment.getPayTime()).toString(), payment.getPrice(), payment.getPayMileage(), payment.getPayWay().toString(), payment.getSavedMileage(), payment.getType().toString(), payment.getPg().toString()));
        }
        return paymentDetails;
    }

    private Reservation reservationInfoValidation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation.getUser() != userRepository.findById(userId)) {
            throw new NoSuchElementException("회원정보와 예약정보가 불일치합니다.");
        }
        return reservation;
    }

    private String getUsageStatus(Reservation reservation) {
        String usageState = null;
        if (reservation.getStatus().equals(ReservationStatus.CANCELED)) {
            usageState = UsageStatus.CANCELED.getDescription();
        } else if (reservation.getStatus().equals(ReservationStatus.PROGRESSING)) {
            usageState = UsageStatus.UNFIXED.getDescription();
        } else if (reservation.getStatus().equals(ReservationStatus.COMPLETED)) {
            if (LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).isAfter(LocalDateTime.now())) {
                usageState = UsageStatus.BEFORE.getDescription();
            } else if (LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).isBefore(LocalDateTime.now()) || LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).isEqual(LocalDateTime.now())) {
                if (LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()).isAfter(LocalDateTime.now())) {
                    usageState = UsageStatus.NOW.getDescription();
                } else {
                    usageState = UsageStatus.AFTER.getDescription();
                }
            }
        }
        if (usageState == null) {
            throw new IllegalStateException("예약 상태를 확인할 수 없습니다.");
        }
        return usageState;
    }

    private String getRatingStatus(Reservation reservation, String usageStatus) {
        String ratingStatus = RatingStatus.YET.getDescription();
        if (usageStatus.equals(UsageStatus.AFTER.getDescription())) {
            if (reservation.getRating() == null) {
                ratingStatus = RatingStatus.WRITABLE.getDescription();
            } else {
                ratingStatus = RatingStatus.WRITTEN.getDescription();
            }
        }
        return ratingStatus;
    }
}
