package com.golfzonaca.officesharingplatform.repository.refund;

import com.golfzonaca.officesharingplatform.domain.Refund;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional
public class CustomRefundRepository implements RefundRepository {

    private final SpringJpaRefundRepository jpaRepository;

    @Override
    public void save(Refund refund) {
        jpaRepository.save(refund);
    }
}
