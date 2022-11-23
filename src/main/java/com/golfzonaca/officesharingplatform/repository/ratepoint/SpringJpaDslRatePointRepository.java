package com.golfzonaca.officesharingplatform.repository.ratepoint;

import com.golfzonaca.officesharingplatform.domain.RatePoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class SpringJpaDslRatePointRepository implements RatePointRepository {
    private final SpringJpaRatePointRepository jpaRepository;
    
    @Override
    public RatePoint update(RatePoint ratePoint) {
        return jpaRepository.save(ratePoint);
    }
}
