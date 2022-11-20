package com.golfzonaca.officesharingplatform.repository.company;

import com.golfzonaca.officesharingplatform.repository.mybatis.CompanyMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyBatisCompanyRepository {
    private final CompanyMapper companyMapper;

    public String findOpenDaysById(long placeId) {
        return companyMapper.findOpenDaysById(placeId);
    }
}
