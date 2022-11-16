package com.golfzonaca.officesharingplatform.repository.address;

import com.golfzonaca.officesharingplatform.domain.Address;
import com.golfzonaca.officesharingplatform.repository.mybatis.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisAddressRepository implements AddressRepository {

    private final AddressMapper addressMapper;


    @Override
    public List<Address> findByAddressIds(List addressIds) {
        return addressMapper.findByAddressIds(addressIds);
    }
}
