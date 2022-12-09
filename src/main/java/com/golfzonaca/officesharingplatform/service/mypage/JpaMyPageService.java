package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.*;
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
    public MyPage createMyPageForm(Long userId) {
        User findUser = userRepository.findById(userId);
        Mileage mileage = findUser.getMileage();
        List<Reservation> findReservation = reservationRepository.findAllByUserId(userId);
        return MyPage.builder().userName(findUser.getUsername()).joinDate(findUser.getJoinDate()).mileagePoint(mileage.getPoint()).totalReviewNumber(findReservation.size()).build();
    }

    @Override
    public void cancelByOrderAndUserId(Integer order, Long userId) {
        List<Reservation> reservationList = reservationRepository.findAllLimit(ReservationSearchCond.builder().userId(userId).build(), order + 1);
        reservationRepository.deleteById(reservationList.get(order).getId());
    }

    @Override
    public Map<Integer, MyReservationList> getMyReservationMap(long userId) {
        Map<Integer, MyReservationList> myReservationMap = new LinkedHashMap<>();
        User user = userRepository.findById(userId);
        List<Reservation> reservationList = user.getReservationList();

        for (int i = 0; i < reservationList.size(); i++) {
            Reservation reservation = reservationList.get(i);
            boolean ratingStatus = Optional.ofNullable(reservation.getRating()).isEmpty();
            MyReservationList myReservationViewData = MyReservationList.builder().productType(reservation.getRoom().getRoomKind().getRoomType()).placeName(reservation.getRoom().getPlace().getPlaceName()).reservationCompletedDateTime(reservation.getResCompleted()).reservationStartDateTime(LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime())).reservationEndDateTime(LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime())).usageState(reservation.getStatus()).isAvailableReview(ratingStatus).build();

            myReservationMap.put(i, myReservationViewData);
        }
        return myReservationMap;
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
        return new MyReservationDetail(reservation.getRoom().getPlace().getPlaceName(), reservation.getRoom().getRoomKind().getRoomType(), reservation.getResCompleted().toString(), LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).toString(), LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()).toString(), reservation.getStatus().toString(), String.valueOf(Optional.ofNullable(reservation.getRating()).isEmpty()));
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
}
