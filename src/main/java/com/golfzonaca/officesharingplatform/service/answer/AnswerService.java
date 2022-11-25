package com.golfzonaca.officesharingplatform.service.answer;

import com.golfzonaca.officesharingplatform.domain.Answer;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.AnswerData;

public interface AnswerService {
    void save(Long managerId, Long inquiryId, AnswerData data);

    Answer findById(long answerId);

    void update(Long managerId, long inquiryId, long answerId, AnswerData data);

    void delete(Long managerId, long inquiryId, long answerId);
}
