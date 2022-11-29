package com.golfzonaca.officesharingplatform.repository.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslInquiryRepository implements InquiryRepository {
    private final SpringJpaInquiryRepository jpaRepository;


    @Override
    public void save(Inquiry inquiry) {
        jpaRepository.save(inquiry);
    }

    @Override
    public Optional<Inquiry> findById(long inquiryId) {
        return jpaRepository.findById(inquiryId);
    }

    @Override
    public void update(Inquiry inquiry, InquiryData data) {
        inquiry.UpdateInquiry(data.getTitle(), data.getQuestion());
    }

    @Override
    public void delete(Inquiry inquiry) {
        jpaRepository.delete(inquiry);
    }
}
