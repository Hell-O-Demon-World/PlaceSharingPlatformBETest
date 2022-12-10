package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.UserData;
import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    UserData getUserData(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<String, JsonObject> getUsageList(long userId);

    Map<String, JsonObject> getUsageDetail(Long userId, long reservationId);

    Map<String, JsonObject> getMyCommentMap(Long userId, Integer page);
}
