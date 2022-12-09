package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.UserData;
import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    UserData getUserData(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<String, JsonObject> getMyReservation(long userId);

    Map<String, JsonObject> getUsageDetail(Long userId, long reservationId);
}
