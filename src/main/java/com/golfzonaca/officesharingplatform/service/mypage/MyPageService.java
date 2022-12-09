package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.MyPage;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationList;
import com.google.gson.JsonObject;

import java.util.Map;

public interface MyPageService {
    MyPage createMyPageForm(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<Integer, MyReservationList> getMyReservationMap(long userId);

    Map<String, JsonObject> getUsageDetail(Long userId, long reservationId);
}
