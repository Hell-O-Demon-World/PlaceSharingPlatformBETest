package com.golfzonaca.officesharingplatform.repository.company;

import com.golfzonaca.officesharingplatform.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {
    String findOpenDaysById(long placeId);
}
