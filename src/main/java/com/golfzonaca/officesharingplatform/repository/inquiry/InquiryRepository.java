package com.golfzonaca.officesharingplatform.repository.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;

import java.util.List;

public interface InquiryRepository {
    void save(Inquiry inquiry);

    Inquiry findById(long inquiryId);

    void update(Inquiry inquiry, InquiryData data);

    void delete(Inquiry inquiry);

    List<Inquiry> findByUserIdWithPagination(Long userId, long page, long quantity);
}
