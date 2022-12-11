package com.golfzonaca.officesharingplatform.web.mypage;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.mypage.MyPageService;
import com.golfzonaca.officesharingplatform.web.mypage.dto.EditUserInfo;
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
        return myPageService.getOverViewData(userId);
    }

    @GetMapping("/usage")
    public Map<String, JsonObject> resHistory(@TokenUserId Long userId, @RequestParam Integer page) {
        return myPageService.getResViewData(userId, page);
    }

    @GetMapping("/{reservationId}")
    public Map<String, JsonObject> resDetail(@TokenUserId Long userId, @PathVariable long reservationId) {
        return myPageService.getResDetailViewData(userId, reservationId);
    }

    @PostMapping("/cancel")
    public void cancelReservation(@TokenUserId Long userId, @RequestParam Integer reservationId) {
        myPageService.cancelByOrderAndUserId(reservationId, userId);
    }

    @GetMapping("/review")
    public Map<String, JsonObject> reviewHistory(@TokenUserId Long userId, @RequestParam Integer reviewpage) {
        return myPageService.getReviewData(userId, reviewpage);
    }

    @GetMapping("/review/{ratingId}")
    public Map<String, JsonObject> commentDataByReview(@PathVariable Long ratingId, @RequestParam Integer commentpage) {
        return myPageService.getCommentDataByReview(ratingId, commentpage);
    }

    @GetMapping("/comment")
    public Map<String, JsonObject> commentHistory(@TokenUserId Long userId, @RequestParam Integer page) {
        return myPageService.getCommentViewData(userId, page);
    }


    @GetMapping("/edit")
    public Map<String, JsonObject> editUserInfo(@TokenUserId Long userId) {
        return myPageService.getEditUserInfo(userId);
    }

    @PostMapping("/edit")
    public void editUser(@TokenUserId Long userId, @RequestBody EditUserInfo editUserInfo) {
        String password = editUserInfo.getPassword();
        String tel = editUserInfo.getTel();
        String job = editUserInfo.getJob();
        Map<String, Boolean> preferType = editUserInfo.getPreferType();
        myPageService.updateUserInfo(userId, password, tel, job, preferType);
    }
}
