package com.golfzonaca.officesharingplatform.web.inquiry;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.service.answer.AnswerService;
import com.golfzonaca.officesharingplatform.service.inquiry.InquiryService;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.AnswerData;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryDetailDto;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryPageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

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

    @PostMapping("/inquiry")
    public List<InquiryDetailDto> allInquiry(@TokenUserId Long userId, @RequestBody InquiryPageInfo pageInfo) {
        List<Inquiry> inquiryList = inquiryService.findByUserIdWithPagination(userId, pageInfo.getPage(), pageInfo.getQuantity());
        List<InquiryDetailDto> inquiryDtoList = new LinkedList<>();
        for (Inquiry inquiry : inquiryList) {
            inquiryDtoList.add(new InquiryDetailDto(inquiry.getId().toString(), inquiry.getUser().getUsername(), inquiry.getTitle(), inquiry.getContent(), inquiry.getDateTime().toString(), inquiry.getInquiryStatus().getStatus().toString(), inquiry.getAnwer().getId().toString(), inquiry.getAnwer().getAnswer()));
        }
        return inquiryDtoList;
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
