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
    public Map<String, JsonObject> resHistory(@TokenUserId Long userId, @RequestParam Integer page) {
        return myPageService.getResView(userId, page);
    }

    @GetMapping("/{reservationId}")
    public Map<String, JsonObject> resDetail(@TokenUserId Long userId, @PathVariable long reservationId) {
        return myPageService.getResDetailView(userId, reservationId);
    }

    @PostMapping("/cancel")
    public void cancelReservation(@TokenUserId Long userId, @RequestParam Integer reservationId) {
        myPageService.cancelByOrderAndUserId(reservationId, userId);
    }

    @GetMapping("/comment")
    public Map<String, JsonObject> commentHistory(@TokenUserId Long userId, @RequestParam Integer page) {
        return myPageService.getCommentView(userId, page);
    }


//    @GetMapping("/edit")
//    public EditUserForm editUser(@TokenUserId Long userId) {
//
//        return
//    }

}
