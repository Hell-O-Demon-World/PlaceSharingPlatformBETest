package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.MyPage;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyPaymentDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationList;

import java.util.List;
import java.util.Map;

public interface MyPageService {
    MyPage createMyPageForm(Long userId);

    void cancelByOrderAndUserId(Integer order, Long userId);

    Map<Integer, MyReservationList> getMyReservationMap(long userId);

    MyReservationDetail getMyReservationDetail(Long userId, long reservationId);

    List<MyPaymentDetail> getMyPaymentDetail(Long userId, long reservationId);
}
