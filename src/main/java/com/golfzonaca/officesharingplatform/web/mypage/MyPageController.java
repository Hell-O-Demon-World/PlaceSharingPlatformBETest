package com.golfzonaca.officesharingplatform.web.mypage;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.domain.MyPage;
import com.golfzonaca.officesharingplatform.service.mypage.MyPageService;
import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageUsageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping
    public MyPage myPageForm(@TokenUserId Long userId) {
        return myPageService.createMyPageForm(userId);
    }

    @GetMapping("/usage")
    public Map<Integer, MyPageUsageForm> usageHistory(@TokenUserId Long userId) {
        return myPageService.getMyPageUsageForm(userId);
    }

//    @GetMapping("/edit")
//    public EditUserForm editUser(@TokenUserId Long userId) {
//
//        return
//    }

    @PostMapping("/cancel")
    public void cancelReservation(@TokenUserId Long userId, @RequestParam Integer order){
        myPageService.cancelByOrderAndUserId(order, userId);
    }
}
