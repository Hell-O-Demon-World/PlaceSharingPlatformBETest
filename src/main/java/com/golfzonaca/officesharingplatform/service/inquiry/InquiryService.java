package com.golfzonaca.officesharingplatform.service.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;

public interface InquiryService {

    void save(Long userId, InquiryData data);

    Inquiry findById(long inquiryId);

    void update(Long userId, long inquiryId, InquiryData data);

    void delete(Long userId, long inquiryId);
}
