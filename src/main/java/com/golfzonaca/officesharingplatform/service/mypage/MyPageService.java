package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.MyPage;

public interface MyPageService {
    MyPage createMyPageForm(Long userId);
    void cancelByOrderAndUserId(Integer order, Long userId);
}
