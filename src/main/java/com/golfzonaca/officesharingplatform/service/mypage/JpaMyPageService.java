package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.RatingStatus;
import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import com.golfzonaca.officesharingplatform.domain.type.UsageStatus;
import com.golfzonaca.officesharingplatform.repository.comment.CommentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationSearchCond;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mypage.dto.comment.MyCommentData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyPaymentDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyReservationDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyReservationList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    private final CommentRepository commentRepository;

    @Override
    public UserData getUserData(Long userId) {
        User findUser = userRepository.findById(userId);
        Mileage mileage = findUser.getMileage();
        List<Reservation> findReservation = reservationRepository.findAllByUserId(userId);
        return UserData.builder().userName(findUser.getUsername()).joinDate(findUser.getJoinDate().toLocalDate().toString()).mileagePoint(mileage.getPoint()).totalReviewNumber(findReservation.size()).build();
    }

    @Override
    public void cancelByOrderAndUserId(Integer order, Long userId) {
        List<Reservation> reservationList = reservationRepository.findAllLimit(ReservationSearchCond.builder().userId(userId).build(), order + 1);
        reservationRepository.deleteById(reservationList.get(order).getId());
    }

    @Override
    public Map<String, JsonObject> getUsageList(long userId) {
        Gson gson = new Gson();
        Map<String, JsonObject> myUsageMap = getMyDataMap(userId, gson);
        User user = userRepository.findById(userId);
        List<Reservation> reservationList = user.getReservationList();

        for (Reservation reservation : reservationList) {
            String usageStatus = getUsageStatus(reservation);
            String ratingStatus = getRatingStatus(reservation, usageStatus);
            JsonObject myReservationViewData = gson.toJsonTree(MyReservationList.builder().productType(reservation.getRoom().getRoomKind().getRoomType().getDescription()).placeName(reservation.getRoom().getPlace().getPlaceName()).reservationCompletedDate(reservation.getResCompleted().toLocalDate()).reservationCompletedTime(reservation.getResCompleted().toLocalTime()).reservationStartDate(reservation.getResStartDate()).reservationStartTime(reservation.getResStartTime()).reservationEndDate(reservation.getResEndDate()).reservationEndTime(reservation.getResEndTime()).usageStatus(usageStatus).ratingStatus(ratingStatus).build()).getAsJsonObject();
            myUsageMap.put(String.valueOf(reservation.getId()), myReservationViewData);
        }
        return myUsageMap;
    }

    @Override
    public Map<String, JsonObject> getUsageDetail(Long userId, long reservationId) {
        Gson gson = new Gson();
        Map<String, JsonObject> usageDetail = getMyDataMap(userId, gson);
        MyReservationDetail myReservationDetail = getMyReservationDetail(userId, reservationId);
        List<MyPaymentDetail> myPaymentDetail = getMyPaymentDetail(userId, reservationId);

        usageDetail.put("resData", gson.toJsonTree(myReservationDetail).getAsJsonObject());
        for (int i = 0; i < myPaymentDetail.size(); i++) {
            MyPaymentDetail paymentDetail = myPaymentDetail.get(i);
            usageDetail.put("payData" + i, gson.toJsonTree(paymentDetail).getAsJsonObject());
        }
        return usageDetail;
    }

    @Override
    public Map<String, JsonObject> getMyCommentMap(Long userId, Integer page) {
        Gson gson = new Gson();
        Map<String, JsonObject> myCommentMap = getMyDataMap(userId, gson);
        User user = userRepository.findById(userId);
        List<Comment> commentList = commentRepository.findAllByUser(user, page);

//        List<Comment> commentList = user.getCommentList();
        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
            MyCommentData myCommentData = new MyCommentData(comment.getRating().getReservation().getRoom().getPlace().getPlaceName(), comment.getRating().getReservation().getRoom().getRoomKind().getRoomType().getDescription(), comment.getText(), comment.getDateTime().toLocalDate().toString(), comment.getDateTime().toLocalTime().toString());
            myCommentMap.put(String.valueOf(i), gson.toJsonTree(myCommentData).getAsJsonObject());
        }
        return myCommentMap;
    }

    @NotNull
    private Map<String, JsonObject> getMyDataMap(Long userId, Gson gson) {
        Map<String, JsonObject> myDataMap = new LinkedHashMap<>();
        UserData user = getUserData(userId);
        JsonObject userData = gson.toJsonTree(user).getAsJsonObject();
        myDataMap.put("userData", userData);
        return myDataMap;
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
