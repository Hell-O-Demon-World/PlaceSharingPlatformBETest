package com.golfzonaca.officesharingplatform.service.mypage;

import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    Map<String, JsonObject> getOverViewData(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<String, JsonObject> getResViewData(long userId, Integer page);

    Map<String, JsonObject> getResDetailViewData(Long userId, long reservationId);

    Map<String, JsonObject> getReviewData(Long userId, Integer reviewpage);

    Map<String, JsonObject> getCommentViewData(Long userId, Integer page);
}
