package com.golfzonaca.officesharingplatform.web.mypage;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.mypage.MyPageService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping
    public Map<String, JsonObject> overView(@TokenUserId Long userId) {
        return myPageService.getOverView(userId);
    }

    @GetMapping("/usage")
    public Map<String, JsonObject> usageHistory(@TokenUserId Long userId) {
        return myPageService.getUsageList(userId);
    }

    @GetMapping("/{reservationId}")
    public Map<String, JsonObject> usageDetail(@TokenUserId Long userId, @PathVariable long reservationId) {
        Map<String, JsonObject> usageDetail = myPageService.getUsageDetail(userId, reservationId);

        return usageDetail;
    }

    @PostMapping("/cancel")
    public void cancelReservation(@TokenUserId Long userId, @RequestParam Integer reservationId) {
        myPageService.cancelByOrderAndUserId(reservationId, userId);
    }

    @GetMapping("/comment")
    public Map<String, JsonObject> commentHistory(@TokenUserId Long userId, @RequestParam Integer page) {
        return myPageService.getMyCommentMap(userId, page);
    }


//    @GetMapping("/edit")
//    public EditUserForm editUser(@TokenUserId Long userId) {
//
//        return
//    }

}
