package com.golfzonaca.officesharingplatform.service.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;

import java.util.Optional;

public interface InquiryService {

    void save(Long userId, InquiryData data);

    Optional<Inquiry> findById(long inquiryId);

    void update(Long userId, long inquiryId, InquiryData data);

    void delete(Long userId, long inquiryId);
}
