package com.golfzonaca.officesharingplatform.repository.company;

import com.golfzonaca.officesharingplatform.repository.mybatis.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyBatisCompanyRepository implements CompanyRepository {
    private final CompanyMapper companyMapper;

    @Override
    public String findOpenDaysById(long placeId) {
        return companyMapper.findOpenDaysById(placeId);
    }
}
