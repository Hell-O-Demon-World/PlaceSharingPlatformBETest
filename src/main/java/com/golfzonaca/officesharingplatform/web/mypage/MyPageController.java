package com.golfzonaca.officesharingplatform.web.mypage;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.domain.MyPage;
import com.golfzonaca.officesharingplatform.service.mypage.MyPageService;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyPaymentDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationDetail;
import com.golfzonaca.officesharingplatform.service.mypage.dto.MyReservationList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
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
    public Map<Integer, MyReservationList> usageHistory(@TokenUserId Long userId) {
        return myPageService.getMyReservationMap(userId);
    }

//    @GetMapping("/edit")
//    public EditUserForm editUser(@TokenUserId Long userId) {
//
//        return
//    }

    @GetMapping("/{reservationId}")
    public Map<String, JsonObject> usageDetail(@TokenUserId Long userId, @PathVariable long reservationId) {
        MyReservationDetail myReservationDetail = myPageService.getMyReservationDetail(userId, reservationId);
        List<MyPaymentDetail> myPaymentDetail = myPageService.getMyPaymentDetail(userId, reservationId);

        Gson gson = new Gson();
        Map<String, JsonObject> usageDetail = new LinkedHashMap<>();
        usageDetail.put("resData", gson.toJsonTree(myReservationDetail).getAsJsonObject());
        for (int i = 0; i < myPaymentDetail.size(); i++) {
            MyPaymentDetail paymentDetail = myPaymentDetail.get(i);
            usageDetail.put("payData" + i, gson.toJsonTree(paymentDetail).getAsJsonObject());
        }
        return usageDetail;
    }

    @PostMapping("/cancel")
    public void cancelReservation(@TokenUserId Long userId, @RequestParam Integer reservationId) {
        myPageService.cancelByOrderAndUserId(reservationId, userId);
    }
}
