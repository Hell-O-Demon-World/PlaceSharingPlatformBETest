package com.golfzonaca.officesharingplatform.service.inquiry;

import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.inquiry.InquiryRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquirySaveData;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.InquiryUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpringJpaDslInquiryService implements InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;


    @Override
    public void save(Long userId, InquirySaveData data) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Inquiry inquiry = new Inquiry(user, data.getTitle(), data.getQuestion(), Boolean.parseBoolean(data.getStatus()), TimeFormatter.toLocalDateTime(data.getDateTime()));
        inquiryRepository.save(inquiry);
    }

    @Override
    public Optional<Inquiry> findById(long inquiryId) {
        return inquiryRepository.findById(inquiryId);
    }

    @Override
    public void update(Long userId, long inquiryId, InquiryUpdateData data) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다."));
        if (inquiry.getUser() == user && !inquiry.isAnswerPresent()) {
            inquiryRepository.update(inquiry, data);
        }
    }

    @Override
    public void delete(Long userId, long inquiryId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다."));
        if (inquiry.getUser() == user) {
            inquiryRepository.delete(inquiry);
        }
    }
}
