package com.golfzonaca.officesharingplatform.service.mypage;

import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    Map<String, JsonObject> getOverViewData(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<String, JsonObject> getResViewData(long userId, Integer page);

    Map<String, JsonObject> getResDetailViewData(Long userId, long reservationId);

    Map<String, JsonObject> getReviewData(Long userId, Integer page);

    Map<String, JsonObject> getCommentDataByReview(Long ratingId, Integer page);

    Map<String, JsonObject> getCommentViewData(Long userId, Integer page);

    Map<String, JsonObject> getQnAViewData(Long userId, Integer page);

    Map<String, JsonObject> getEditUserInfo(Long userId);

    void updateUserInfo(Long userId, String password, String tel, String job, Map<String, Boolean> preferType);

    void saveInquiry(Long userId, String title, String question);
}
