package com.golfzonaca.officesharingplatform.web.inquiry;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.answer.AnswerService;
import com.golfzonaca.officesharingplatform.service.inquiry.InquiryService;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.AnswerData;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final AnswerService answerService;

    @PostMapping("/inquiry/add")
    public String saveInquiry(@TokenUserId Long userId, @Validated @RequestBody InquiryData data, BindingResult bindingResult) {
        inquiryService.save(userId, data);
        return "ok";
    }

    @GetMapping("/inquiry/{inquiryId}")
    public String inquiryDetail(@PathVariable long inquiryId) {
        inquiryService.findById(inquiryId);
        return "ok";
    }

    @PostMapping("/inquiry/{inquiryId}/edit")
    public String editInquiry(@TokenUserId Long userId, @PathVariable long inquiryId, @Validated @RequestBody InquiryData data, BindingResult bindingResult) {
        inquiryService.update(userId, inquiryId, data);
        return "ok";
    }

    @GetMapping("/inquiry/{inquiryId}/delete")
    public String deleteInquiry(@TokenUserId Long userId, @PathVariable long inquiryId) {
        inquiryService.delete(userId, inquiryId);
        return "ok";
    }

    @PostMapping("/inquiry/{inquiryId}/answer/add")
    public String saveAnswer(@TokenUserId Long userId, @PathVariable Long inquiryId, @Validated @RequestBody AnswerData data, BindingResult bindingResult) {
        answerService.save(userId, inquiryId, data);
        return "ok";
    }

    @GetMapping("/inquiry/{inquiryId}/answer/{answerId}")
    public String answerDetail(@PathVariable long inquiryId, @PathVariable long answerId) {
        answerService.findById(answerId);
        return "ok";
    }

    @PostMapping("/inquiry/{inquiryId}/answer/{answerId}/edit")
    public String editAnswer(@TokenUserId Long managerId, @PathVariable long inquiryId, @PathVariable long answerId, @Validated @RequestBody AnswerData data, BindingResult bindingResult) {
        answerService.update(managerId, inquiryId, answerId, data);
        return "ok";
    }

    @GetMapping("/inquiry/{inquiryId}/answer/{answerId}/delete")
    public String deleteAnswer(@TokenUserId Long managerId, @PathVariable long inquiryId, @PathVariable long answerId) {
        answerService.delete(managerId, inquiryId, answerId);
        return "ok";
    }
}
