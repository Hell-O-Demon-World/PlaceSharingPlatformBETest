package com.golfzonaca.officesharingplatform.web.inquiry;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.inquiry.InquiryService;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquirySaveData;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/inquiry/add")
    public String saveInquiry(@TokenUserId Long userId, @Validated @RequestBody InquirySaveData saveData, BindingResult bindingResult) {
        inquiryService.save(userId, saveData);
        return "ok";
    }

    @GetMapping("/inquiry/{inquiryId}")
    public String inquiryDetail(@PathVariable long inquiryId) {
        inquiryService.findById(inquiryId);
        return "ok";
    }

    @PostMapping("/inquiry/{inquiryId}/edit")
    public String editInquiry(@TokenUserId Long userId, @PathVariable long inquiryId, @Validated @RequestBody InquiryUpdateData updateData, BindingResult bindingResult) {
        inquiryService.update(userId, inquiryId, updateData);
        return "ok";
    }

    @GetMapping("/inquiry/{inquiryId}/delete")
    public String deleteInquiry(@TokenUserId Long userId, @PathVariable long inquiryId, BindingResult bindingResult) {
        inquiryService.delete(userId, inquiryId);
        return "ok";
    }
}
