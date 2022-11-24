package com.golfzonaca.officesharingplatform.service.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquirySaveData;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryUpdateData;

import java.util.Optional;

public interface InquiryService {

    void save(Long userId, InquirySaveData data);

    Optional<Inquiry> findById(long inquiryId);

    void update(Long userId, long inquiryId, InquiryUpdateData data);

    void delete(Long userId, long inquiryId);
}
