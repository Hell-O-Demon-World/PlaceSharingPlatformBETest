package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.MyPage;
import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageUsageForm;

import java.util.Map;

public interface MyPageService {
    MyPage createMyPageForm(Long userId);
    void cancelByOrderAndUserId(Integer order, Long userId);
    Map<Integer, MyPageUsageForm> getMyPageUsageForm(long userId);
}
