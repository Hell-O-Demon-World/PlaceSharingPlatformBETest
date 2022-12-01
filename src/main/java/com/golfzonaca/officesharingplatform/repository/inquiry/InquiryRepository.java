package com.golfzonaca.officesharingplatform.repository.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;

public interface InquiryRepository {
    void save(Inquiry inquiry);

    Inquiry findById(long inquiryId);

    void update(Inquiry inquiry, InquiryData data);

    void delete(Inquiry inquiry);

}
