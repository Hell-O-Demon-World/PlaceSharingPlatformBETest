package com.golfzonaca.officesharingplatform.repository.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomInquiryRepository implements InquiryRepository {
    private final SpringJpaInquiryRepository jpaInquiryRepository;
    private final QueryInquiryRepository queryInquiryRepository;

    @Override
    public void save(Inquiry inquiry) {
        jpaInquiryRepository.save(inquiry);
    }

    @Override
    public Inquiry findById(long inquiryId) {
        return jpaInquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다."));
    }

    @Override
    public void update(Inquiry inquiry, InquiryData data) {
        inquiry.UpdateInquiry(data.getTitle(), data.getQuestion());
    }

    @Override
    public void delete(Inquiry inquiry) {
        jpaInquiryRepository.delete(inquiry);
    }

    @Override
    public List<Inquiry> findByUserIdWithPagination(Long userId, long page, long quantity) {
        return queryInquiryRepository.findByUserIdWithPagination(userId, page, quantity);
    }

}
