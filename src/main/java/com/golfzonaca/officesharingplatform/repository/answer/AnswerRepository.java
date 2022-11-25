package com.golfzonaca.officesharingplatform.repository.answer;

import com.golfzonaca.officesharingplatform.domain.Answer;
import com.golfzonaca.officesharingplatform.web.inquiry.dto.AnswerData;

import java.util.Optional;

public interface AnswerRepository {
    void save(Answer answer);

    Optional<Answer> findById(long answerId);

    void update(Answer answer, AnswerData data);

    void delete(Answer answer);
}
