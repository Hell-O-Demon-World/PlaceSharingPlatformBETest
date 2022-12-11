package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.RatingStatus;
import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import com.golfzonaca.officesharingplatform.domain.type.UsageStatus;
import com.golfzonaca.officesharingplatform.repository.comment.CommentRepository;
import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationSearchCond;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mypage.dto.comment.CommentDataByRating;
import com.golfzonaca.officesharingplatform.service.mypage.dto.comment.MyCommentData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.rating.RatingData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyPaymentDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyReservationDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyReservationList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class JpaMyPageService implements MyPageService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;


    @Override
    public Map<String, JsonObject> getOverViewData(Long userId) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> overViewMap = processingUserData(user);
        putReservationAtNow(overViewMap, user);
        putRecentReservations(overViewMap, user);
        return overViewMap;
    }

    @Override
    public void cancelByOrderAndUserId(Integer order, Long userId) {
        List<Reservation> reservationList = reservationRepository.findAllLimit(ReservationSearchCond.builder().userId(userId).build(), order + 1);
        reservationRepository.deleteById(reservationList.get(order).getId());
    }

    @Override
    public Map<String, JsonObject> getResViewData(long userId, Integer page) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> myResMap = processingUserData(user);
        putReservationData(user, myResMap, page);
        return myResMap;
    }

    @Override
    public Map<String, JsonObject> getResDetailViewData(Long userId, long reservationId) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> usageDetail = processingUserData(user);
        putResDetailData(user, reservationId, usageDetail);
        putPaymentDetailData(user, reservationId, usageDetail);
        return usageDetail;
    }

    @Override
    public Map<String, JsonObject> getReviewData(Long userId, Integer page) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> reviewMap = processingUserData(user);
        putReviewData(user, reviewMap, page);
        return reviewMap;
    }

    @Override
    public Map<String, JsonObject> getCommentDataByReview(Long ratingId, Integer commentpage) {
        Rating rating = ratingRepository.findById(ratingId);
        return putCommentDataByRating(rating, commentpage);
    }

    @Override
    public Map<String, JsonObject> getCommentViewData(Long userId, Integer page) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> myCommentMap = processingUserData(user);
        putCommentData(page, user, myCommentMap);
        return myCommentMap;
    }

    private void putReservationAtNow(Map<String, JsonObject> overViewMap, User user) {
        Gson gson = new Gson();
        Map<String, JsonObject> currentResMap = processingCurrentReservationData(user);
        overViewMap.put("currentResData", gson.toJsonTree(currentResMap).getAsJsonObject());

    }

    private void putRecentReservations(Map<String, JsonObject> overViewMap, User user) {
        Gson gson = new Gson();
        Map<String, JsonObject> recentResMap = processingAllReservationData(user, 1, LocalDate.now());
        overViewMap.put("recentResData", gson.toJsonTree(recentResMap).getAsJsonObject());
    }

    private void putResDetailData(User user, long reservationId, Map<String, JsonObject> usageDetail) {
        Gson gson = new Gson();
        MyReservationDetail myReservationDetail = getMyReservationDetail(user, reservationId);
        usageDetail.put("resData", gson.toJsonTree(myReservationDetail).getAsJsonObject());
    }

    private void putPaymentDetailData(User user, long reservationId, Map<String, JsonObject> usageDetail) {
        Gson gson = new Gson();
        List<MyPaymentDetail> myPaymentDetail = getMyPaymentDetail(user, reservationId);
        for (int i = 0; i < myPaymentDetail.size(); i++) {
            MyPaymentDetail paymentDetail = myPaymentDetail.get(i);
            usageDetail.put("payData" + i, gson.toJsonTree(paymentDetail).getAsJsonObject());
        }
    }

    private void putReservationData(User user, Map<String, JsonObject> myResMap, Integer page) {
        Gson gson = new Gson();
        Map<String, JsonObject> myUsage = processingAllReservationData(user, page, null);
        myResMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", user.getReservationList().size() / 8 + 1)).getAsJsonObject());
        myResMap.put("reservationData", gson.toJsonTree(myUsage).getAsJsonObject());
    }

    private Map<String, JsonObject> putCommentDataByRating(Rating rating, Integer commentpage) {
        Gson gson = new Gson();
        Map<String, JsonObject> commentDataMap = new LinkedHashMap<>();
        commentDataMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", rating.getCommentList().size() / 8 + 1)).getAsJsonObject());
        Map<String, JsonObject> commentData = processingCommentDataByRating(rating, commentpage);
        commentDataMap.put("commentData", gson.toJsonTree(commentData).getAsJsonObject());
        return commentDataMap;
    }

    private Map<String, JsonObject> processingUserData(User user) {
        Gson gson = new Gson();
        Map<String, JsonObject> myDataMap = new LinkedHashMap<>();
        JsonObject userData = gson.toJsonTree(getUserData(user)).getAsJsonObject();
        myDataMap.put("userData", userData);
        return myDataMap;
    }

    private Map<String, JsonObject> processingCurrentReservationData(User user) {
        Gson gson = new Gson();
        Map<String, JsonObject> myUsage = new LinkedHashMap<>();
        List<Reservation> reservationList = reservationRepository.findByUserAndDateTime(user, LocalDate.now(), LocalTime.now());
        for (int i = 0; i < reservationList.size(); i++) {
            Reservation reservation = reservationList.get(i);
            UsageStatus usageStatus = getUsageStatus(reservation);
            RatingStatus ratingStatus = getRatingStatus(reservation, usageStatus);
            JsonObject myReservationViewData = gson.toJsonTree(MyReservationList.builder().reservationId(reservation.getId()).productType(reservation.getRoom().getRoomKind().getRoomType().getDescription()).placeName(reservation.getRoom().getPlace().getPlaceName()).reservationCompletedDate(reservation.getResCompleted().toLocalDate().toString()).reservationCompletedTime(reservation.getResCompleted().toLocalTime().toString()).reservationStartDate(reservation.getResStartDate().toString()).reservationStartTime(reservation.getResStartTime().toString()).reservationEndDate(reservation.getResEndDate().toString()).reservationEndTime(reservation.getResEndTime().toString()).usageStatus(usageStatus.getDescription()).ratingStatus(ratingStatus.equals(RatingStatus.WRITABLE)).ratingStatusDescription(ratingStatus.getDescription()).build()).getAsJsonObject();
            myUsage.put(String.valueOf(i), myReservationViewData);
        }
        return myUsage;
    }

    private Map<String, JsonObject> processingAllReservationData(User user, Integer page, LocalDate date) {
        Gson gson = new Gson();
        Map<String, JsonObject> myUsage = new LinkedHashMap<>();
        for (int i = 0; i < reservationRepository.findAllByUserWithPagination(user, page, date).size(); i++) {
            Reservation reservation = reservationRepository.findAllByUserWithPagination(user, page, date).get(i);
            UsageStatus usageStatus = getUsageStatus(reservation);
            RatingStatus ratingStatus = getRatingStatus(reservation, usageStatus);
            JsonObject myReservationViewData = gson.toJsonTree(MyReservationList.builder().reservationId(reservation.getId()).productType(reservation.getRoom().getRoomKind().getRoomType().getDescription()).placeName(reservation.getRoom().getPlace().getPlaceName()).reservationCompletedDate(reservation.getResCompleted().toLocalDate().toString()).reservationCompletedTime(reservation.getResCompleted().toLocalTime().toString()).reservationStartDate(reservation.getResStartDate().toString()).reservationStartTime(reservation.getResStartTime().toString()).reservationEndDate(reservation.getResEndDate().toString()).reservationEndTime(reservation.getResEndTime().toString()).usageStatus(usageStatus.getDescription()).ratingStatus(ratingStatus.equals(RatingStatus.WRITABLE)).ratingStatusDescription(ratingStatus.getDescription()).build()).getAsJsonObject();
            myUsage.put(String.valueOf(i), myReservationViewData);
        }
        return myUsage;
    }

    @NotNull
    private Map<String, JsonObject> processingCommentDataByRating(Rating rating, Integer commentpage) {
        Gson gson = new Gson();
        Map<String, JsonObject> commentData = new LinkedHashMap<>();
        for (int i = 0; i < commentRepository.findAllByRating(rating, commentpage).size(); i++) {
            Comment comment = commentRepository.findAllByRating(rating, commentpage).get(i);
            commentData.put(String.valueOf(i), gson.toJsonTree(new CommentDataByRating(processingUserIdentification(comment.getWriter()), comment.getText(), comment.getDateTime().toLocalDate().toString(), comment.getDateTime().toLocalTime().toString())).getAsJsonObject());
        }
        return commentData;
    }

    private void putReviewData(User user, Map<String, JsonObject> reviewMap, Integer page) {
        Gson gson = new Gson();
        List<Rating> ratingList = ratingRepository.findAllByUser(user, page);
        reviewMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", ratingList.size() / 8 + 1)).getAsJsonObject());
        for (int i = 0; i < ratingList.size(); i++) {
            Rating rating = ratingList.get(i);
            JsonObject myRatingData = gson.toJsonTree(new RatingData(rating.getReservation().getRoom().getPlace().getPlaceName(), rating.getReservation().getRoom().getRoomKind().getRoomType().getDescription(), rating.getReservation().getResStartDate().toString(), rating.getReservation().getResEndDate().toString(), rating.getReservation().getResStartTime().toString(), rating.getReservation().getResEndTime().toString(), String.valueOf(rating.getId()), rating.getRatingTime().toLocalDate().toString(), rating.getRatingTime().toLocalTime().toString(), String.valueOf(rating.getRatingScore()), rating.getRatingReview(), String.valueOf(rating.getCommentList().size()))).getAsJsonObject();
            reviewMap.put(String.valueOf(i), myRatingData);
        }
    }

    private void putCommentData(Integer page, User user, Map<String, JsonObject> myCommentMap) {
        Gson gson = new Gson();
        List<Comment> commentList = commentRepository.findAllByUser(user, page);
        myCommentMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", commentList.size() / 8 + 1)).getAsJsonObject());
        Map<String, JsonObject> commentDataMap = new LinkedHashMap<>();
        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
            MyCommentData myCommentData = new MyCommentData(comment.getRating().getReservation().getRoom().getPlace().getPlaceName(), comment.getRating().getReservation().getRoom().getRoomKind().getRoomType().getDescription(), comment.getText(), comment.getDateTime().toLocalDate().toString(), comment.getDateTime().toLocalTime().toString());
            commentDataMap.put(String.valueOf(i), gson.toJsonTree(myCommentData).getAsJsonObject());
        }
        myCommentMap.put("commentData", gson.toJsonTree(commentDataMap).getAsJsonObject());
    }

    private UserData getUserData(User user) {
        Mileage mileage = user.getMileage();
        int totalReviewQuantity = 0;
        for (Reservation reservation : user.getReservationList()) {
            if (Optional.ofNullable(reservation.getRating()).isPresent()) {
                totalReviewQuantity++;
            }
        }
        return UserData.builder().userName(user.getUsername()).joinDate(user.getJoinDate().toLocalDate().toString()).mileagePoint(mileage.getPoint()).totalReviewNumber(totalReviewQuantity).build();
    }

    private MyReservationDetail getMyReservationDetail(User user, long reservationId) {
        Reservation reservation = reservationInfoValidation(user, reservationId);
        return new MyReservationDetail(reservation.getRoom().getPlace().getPlaceName(), reservation.getRoom().getRoomKind().getRoomType().getDescription(), reservation.getResCompleted().toString(), LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).toString(), LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()).toString(), reservation.getStatus().toString(), String.valueOf(Optional.ofNullable(reservation.getRating()).isEmpty()));
    }

    private List<MyPaymentDetail> getMyPaymentDetail(User user, long reservationId) {
        Reservation reservation = reservationInfoValidation(user, reservationId);
        List<MyPaymentDetail> paymentDetails = new LinkedList<>();
        for (Payment payment : reservation.getPaymentList()) {
            paymentDetails.add(new MyPaymentDetail(LocalDateTime.of(payment.getPayDate(), payment.getPayTime()).toString(), payment.getPrice(), payment.getPayMileage(), payment.getPayWay().toString(), payment.getSavedMileage(), payment.getType().toString(), payment.getPg().toString()));
        }
        return paymentDetails;
    }

    private UsageStatus getUsageStatus(Reservation reservation) {
        UsageStatus usageState = null;
        if (reservation.getStatus().equals(ReservationStatus.CANCELED)) {
            usageState = UsageStatus.CANCELED;
        } else if (reservation.getStatus().equals(ReservationStatus.PROGRESSING)) {
            usageState = UsageStatus.UNFIXED;
        } else if (reservation.getStatus().equals(ReservationStatus.COMPLETED)) {
            if (LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).isAfter(LocalDateTime.now())) {
                usageState = UsageStatus.BEFORE;
            } else if (LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).isBefore(LocalDateTime.now()) || LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()).isEqual(LocalDateTime.now())) {
                if (LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()).isAfter(LocalDateTime.now())) {
                    usageState = UsageStatus.NOW;
                } else {
                    usageState = UsageStatus.AFTER;
                }
            }
        }
        if (usageState == null) {
            throw new IllegalStateException("예약 상태를 확인할 수 없습니다.");
        }
        return usageState;
    }

    private RatingStatus getRatingStatus(Reservation reservation, UsageStatus usageStatus) {
        RatingStatus ratingStatus = RatingStatus.YET;
        if (usageStatus.equals(UsageStatus.AFTER)) {
            if (reservation.getRating() == null) {
                ratingStatus = RatingStatus.WRITABLE;
            } else {
                ratingStatus = RatingStatus.WRITTEN;
            }
        }
        return ratingStatus;
    }

    private Reservation reservationInfoValidation(User user, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation.getUser() != user) {
            throw new NoSuchElementException("회원정보와 예약정보가 불일치합니다.");
        }
        return reservation;
    }

    private String processingUserIdentification(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        int startMailDomain = email.lastIndexOf("@");
        String mailId = email.substring(0, startMailDomain);
        String mailDomain = email.substring(startMailDomain + 1);
        if (mailId.length() <= 4) {
            mailId = mailId + "***";
        } else {
            mailId = mailId.substring(0, 3) + "***";
        }
        mailDomain = mailDomain.charAt(0) + "*****";
        return username + "(" + mailId + mailDomain + ")";
    }
}
