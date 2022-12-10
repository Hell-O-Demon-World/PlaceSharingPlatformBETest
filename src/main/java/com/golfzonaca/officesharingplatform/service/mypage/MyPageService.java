package com.golfzonaca.officesharingplatform.service.mypage;

import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    Map<String, JsonObject> getOverView(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<String, JsonObject> getResView(long userId);

    Map<String, JsonObject> getUsageDetail(Long userId, long reservationId);

    Map<String, JsonObject> getMyCommentMap(Long userId, Integer page);

}
