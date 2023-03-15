package com.golfzonaca.officesharingplatform.repository.answer;

import com.golfzonaca.officesharingplatform.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAnswerRepository extends JpaRepository<Answer, Long> {
}
