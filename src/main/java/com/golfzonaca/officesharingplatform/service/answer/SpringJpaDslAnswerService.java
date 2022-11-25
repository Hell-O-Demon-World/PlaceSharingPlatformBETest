package com.golfzonaca.officesharingplatform.service.answer;

import com.golfzonaca.officesharingplatform.domain.Answer;
import com.golfzonaca.officesharingplatform.domain.Inquiry;
import com.golfzonaca.officesharingplatform.domain.Manager;
import com.golfzonaca.officesharingplatform.repository.answer.AnswerRepository;
import com.golfzonaca.officesharingplatform.repository.inquiry.InquiryRepository;
import com.golfzonaca.officesharingplatform.repository.inquirystatus.InquiryStatusRepository;
import com.golfzonaca.officesharingplatform.repository.manager.ManagerRepository;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.AnswerData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslAnswerService implements AnswerService {
    private final ManagerRepository managerRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryStatusRepository inquiryStatusRepository;
    private final AnswerRepository answerRepository;

    @Override
    public void save(Long mangerId, Long inquiryId, AnswerData data) {
        Manager manager = managerRepository.findById(mangerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 운영자입니다."));
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다."));
        if (manager.getRole().getRoleType().getDescription().equals("운영자")) {
            answerRepository.save(new Answer(inquiry, data.getAnswer()));
            inquiryStatusRepository.update(inquiry.getInquiryStatus(), true);
        }
    }

    @Override
    public Answer findById(long answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 답변입니다."));
    }

    @Override
    public void update(Long managerId, long inquiryId, long answerId, AnswerData data) {
        Manager manager = managerRepository.findById(managerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 운영자입니다."));
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다."));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 답변입니다."));
        if (manager.getRole().getRoleType().getDescription().equals("운영자")) {
            answerRepository.update(answer, data);
        }
    }

    @Override
    public void delete(Long managerId, long inquiryId, long answerId) {
        Manager manager = managerRepository.findById(managerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 운영자입니다."));
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다."));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 답변입니다."));
        if (manager.getRole().getRoleType().getDescription().equals("운영자")) {
            answerRepository.delete(answer);
            inquiryStatusRepository.update(inquiry.getInquiryStatus(), false);

        }
    }
}
