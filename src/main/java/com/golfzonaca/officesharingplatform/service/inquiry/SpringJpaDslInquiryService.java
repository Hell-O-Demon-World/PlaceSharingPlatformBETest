package com.golfzonaca.officesharingplatform.service.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.domain.InquiryStatus;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.inquiry.InquiryRepository;
import com.golfzonaca.officesharingplatform.repository.inquirystatus.InquiryStatusRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SpringJpaDslInquiryService implements InquiryService {
    private final InquiryRepository inquiryRepository;
    private final InquiryStatusRepository inquiryStatusRepository;
    private final UserRepository userRepository;


    @Override
    public void save(Long userId, InquiryData data) {
        User user = userRepository.findById(userId);
        Inquiry inquiry = new Inquiry(user, data.getTitle(), data.getQuestion(), LocalDateTime.now());
        inquiryRepository.save(inquiry);
        inquiryStatusRepository.save(new InquiryStatus(inquiry, false));
    }

    @Override
    public Inquiry findById(long inquiryId) {
        return inquiryRepository.findById(inquiryId);
    }

    @Override
    public void update(Long userId, long inquiryId, InquiryData data) {
        User user = userRepository.findById(userId);
        Inquiry inquiry = inquiryRepository.findById(inquiryId);
        if (inquiry.getUser() == user && !inquiry.getInquiryStatus().getStatus()) {
            inquiryRepository.update(inquiry, data);
        }
    }

    @Override
    public void delete(Long userId, long inquiryId) {
        User user = userRepository.findById(userId);
        Inquiry inquiry = inquiryRepository.findById(inquiryId);
        if (inquiry.getUser() == user) {
            inquiryStatusRepository.delete(inquiry.getInquiryStatus());
            inquiryRepository.delete(inquiry);
        }
    }
}
