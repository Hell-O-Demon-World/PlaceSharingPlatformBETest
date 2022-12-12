package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.type.RatingStatus;
import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import com.golfzonaca.officesharingplatform.domain.type.UsageStatus;
import com.golfzonaca.officesharingplatform.repository.comment.CommentRepository;
import com.golfzonaca.officesharingplatform.repository.inquiry.InquiryRepository;
import com.golfzonaca.officesharingplatform.repository.inquirystatus.InquiryStatusRepository;
import com.golfzonaca.officesharingplatform.repository.rating.RatingRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationSearchCond;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.service.mypage.dto.comment.CommentDataByRating;
import com.golfzonaca.officesharingplatform.service.mypage.dto.comment.MyCommentData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.edituserinfo.EditUserInfo;
import com.golfzonaca.officesharingplatform.service.mypage.dto.qna.AnswerData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.qna.QnAData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.qna.QuestionData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.rating.RatingData;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyPaymentDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyReservationDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.usage.MyReservationList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final InquiryRepository inquiryRepository;
    private final InquiryStatusRepository inquiryStatusRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

    @Override
    public Map<String, JsonObject> getQnAViewData(Long userId, Integer page) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> myQnAMap = processingUserData(user);
        putQnAData(user, myQnAMap, page);
        return myQnAMap;
    }

    @Override
    public Map<String, JsonObject> getEditUserInfo(Long userId) {
        User user = userRepository.findById(userId);
        Map<String, JsonObject> editUserInfoMap = processingUserData(user);
        putUserInfoData(user, editUserInfoMap);
        return editUserInfoMap;
    }

    @Override
    public void updateUserInfo(Long userId, String password, String tel, String job, Map<String, Boolean> preferType) {
        User user = userRepository.findById(userId);
        if (StringUtils.hasText(password)) {
            user.updatePassword(bCryptPasswordEncoder.encode(password));
        }
        if (StringUtils.hasText(tel)) {
            user.updatePhoneNumber(tel);
        }
        if (StringUtils.hasText(job)) {
            user.updateJob(job);
        }
        if (!preferType.isEmpty()) {
            user.updateUserPlace(processingPreferType(preferType));
        }
    }

    @Override
    public void saveInquiry(Long userId, String title, String question) {
        Inquiry inquiry = inquiryRepository.save(new Inquiry(userRepository.findById(userId), title, question, LocalDateTime.now()));
        inquiryStatusRepository.save(new InquiryStatus(inquiry, false));
    }

    private void putUserInfoData(User user, Map<String, JsonObject> editUserInfoMap) {
        Gson gson = new Gson();
        Map<String, Boolean> preferType = processingPreferType(user);
        editUserInfoMap.put("userInfoData", gson.toJsonTree(new EditUserInfo(user.getPhoneNumber(), user.getJob(), preferType)).getAsJsonObject());
    }

    private void putReservationAtNow(Map<String, JsonObject> overViewMap, User user) {
        Gson gson = new Gson();
        Map<String, JsonObject> currentResMap = processingCurrentReservationData(user);
        overViewMap.put("currentResData", gson.toJsonTree(currentResMap).getAsJsonObject());

    }

    private void putRecentReservations(Map<String, JsonObject> overViewMap, User user) {
        Gson gson = new Gson();
        Map<String, JsonObject> recentResMap = processingRecentReservationData(user, LocalDate.now());
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
        Map<String, JsonObject> myUsage = processingAllReservationData(user, page);
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

    private void putQnAData(User user, Map<String, JsonObject> myInquiryMap, Integer page) {
        Gson gson = new Gson();
        Map<String, JsonObject> qnaMap = processingQnAData(user, page);
        myInquiryMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", user.getInquiryList().size() / 8 + 1)).getAsJsonObject());
        myInquiryMap.put("qnaData", gson.toJsonTree(qnaMap).getAsJsonObject());
    }

    private void putReviewData(User user, Map<String, JsonObject> reviewMap, Integer page) {
        Gson gson = new Gson();
        List<Rating> ratingList = ratingRepository.findAllByUserWithPagination(user, page);
        reviewMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", ratingRepository.countByUser(user) / 8 + 1)).getAsJsonObject());
        Map<String, JsonObject> reviewData = processingReviewData(ratingList);
        reviewMap.put("reviewData", gson.toJsonTree(reviewData).getAsJsonObject());
    }

    private Map<String, JsonObject> processingQnAData(User user, Integer page) {
        Gson gson = new Gson();
        Map<String, JsonObject> qnaMap = new LinkedHashMap<>();
        List<Inquiry> inquiryList = inquiryRepository.findAllByUserWithPagination(user, page);
        for (int i = 0; i < inquiryList.size(); i++) {
            Inquiry inquiry = inquiryList.get(i);
            AnswerData answerData = new AnswerData("");
            if (inquiry.getAnswer() != null) {
                answerData.setAnswerContext(inquiry.getAnswer().getAnswer());
            }
            QnAData qnAData = new QnAData(new QuestionData(inquiry.getId(), inquiry.getTitle(), inquiry.getContent(), inquiry.getDateTime().toLocalDate().toString(), inquiry.getDateTime().toLocalTime().toString(), inquiry.getInquiryStatus().getStatus()), answerData);
            qnaMap.put(String.valueOf(i), gson.toJsonTree(qnAData).getAsJsonObject());
        }
        return qnaMap;
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

    private Map<String, JsonObject> processingRecentReservationData(User user, LocalDate date) {
        Gson gson = new Gson();
        Map<String, JsonObject> myUsage = new LinkedHashMap<>();
        for (int i = 0; i < reservationRepository.findRecentDataByUserWithPagination(user, 1, date).size(); i++) {
            Reservation reservation = reservationRepository.findRecentDataByUserWithPagination(user, 1, date).get(i);
            UsageStatus usageStatus = getUsageStatus(reservation);
            RatingStatus ratingStatus = getRatingStatus(reservation, usageStatus);
            JsonObject myReservationViewData = gson.toJsonTree(MyReservationList.builder().reservationId(reservation.getId()).productType(reservation.getRoom().getRoomKind().getRoomType().getDescription()).placeName(reservation.getRoom().getPlace().getPlaceName()).reservationCompletedDate(reservation.getResCompleted().toLocalDate().toString()).reservationCompletedTime(reservation.getResCompleted().toLocalTime().toString()).reservationStartDate(reservation.getResStartDate().toString()).reservationStartTime(reservation.getResStartTime().toString()).reservationEndDate(reservation.getResEndDate().toString()).reservationEndTime(reservation.getResEndTime().toString()).usageStatus(usageStatus.getDescription()).ratingStatus(ratingStatus.equals(RatingStatus.WRITABLE)).ratingStatusDescription(ratingStatus.getDescription()).build()).getAsJsonObject();
            myUsage.put(String.valueOf(i), myReservationViewData);
        }
        return myUsage;
    }

    private Map<String, JsonObject> processingAllReservationData(User user, Integer page) {
        Gson gson = new Gson();
        Map<String, JsonObject> myUsage = new LinkedHashMap<>();
        for (int i = 0; i < reservationRepository.findAllByUserWithPagination(user, page).size(); i++) {
            Reservation reservation = reservationRepository.findAllByUserWithPagination(user, page).get(i);
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

    @NotNull
    private Map<String, Boolean> processingPreferType(User user) {
        Map<String, Boolean> preferType = new LinkedHashMap<>();
        String userPlace = user.getUserPlace();
        while (userPlace.contains(":")) {
            int infoSplitPoint = userPlace.indexOf(":");
            String key = userPlace.substring(0, infoSplitPoint);
            userPlace = userPlace.substring(infoSplitPoint + 1);
            int separatePoint = userPlace.indexOf("&");
            String value;
            if (separatePoint == -1) {
                value = userPlace;
            } else {
                value = userPlace.substring(0, separatePoint);
            }
            userPlace = userPlace.substring(separatePoint + 1);
            preferType.put(key, Boolean.parseBoolean(value));
        }
        return preferType;
    }

    private String processingPreferType(Map<String, Boolean> preferType) {
        String userPreferType = "";
        for (String roomType : preferType.keySet()) {
            userPreferType = userPreferType + roomType + ":" + preferType.get(roomType) + "&";
        }
        return userPreferType.substring(0, userPreferType.lastIndexOf("&"));
    }

    @NotNull
    private Map<String, JsonObject> processingReviewData(List<Rating> ratingList) {
        Gson gson = new Gson();
        Map<String, JsonObject> reviewData = new LinkedHashMap<>();
        for (int i = 0; i < ratingList.size(); i++) {
            Rating rating = ratingList.get(i);
            JsonObject myRatingData = gson.toJsonTree(new RatingData(rating.getReservation().getRoom().getPlace().getPlaceName(), rating.getReservation().getRoom().getRoomKind().getRoomType().getDescription(), rating.getReservation().getResStartDate().toString(), rating.getReservation().getResEndDate().toString(), rating.getReservation().getResStartTime().toString(), rating.getReservation().getResEndTime().toString(), String.valueOf(rating.getId()), rating.getRatingTime().toLocalDate().toString(), rating.getRatingTime().toLocalTime().toString(), String.valueOf(rating.getRatingScore()), rating.getRatingReview(), String.valueOf(rating.getCommentList().size()))).getAsJsonObject();
            reviewData.put(String.valueOf(i), myRatingData);
        }
        return reviewData;
    }

    private void putCommentData(Integer page, User user, Map<String, JsonObject> myCommentMap) {
        Gson gson = new Gson();
        List<Comment> commentList = commentRepository.findAllByUserWithPagination(user, page);
        myCommentMap.put("paginationData", gson.toJsonTree(Map.of("maxPage", commentRepository.countByUser(user) / 8 + 1)).getAsJsonObject());
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
