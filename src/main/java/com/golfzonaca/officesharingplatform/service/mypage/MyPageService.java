package com.golfzonaca.officesharingplatform.service.mypage;

import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    Map<String, JsonObject> getOverView(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<String, JsonObject> getResView(long userId, Integer page);

    Map<String, JsonObject> getResDetailView(Long userId, long reservationId);

    Map<String, JsonObject> getCommentView(Long userId, Integer page);

}
