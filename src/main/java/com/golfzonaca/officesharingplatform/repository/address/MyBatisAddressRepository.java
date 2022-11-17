package com.golfzonaca.officesharingplatform.repository.address;

import com.golfzonaca.officesharingplatform.domain.Address;
import com.golfzonaca.officesharingplatform.repository.mybatis.AddressMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MyBatisAddressRepository {

    private final AddressMapper addressMapper;

    public List<Address> findByAddressIds(List addressIds) {
        return addressMapper.findByAddressIds(addressIds);
    }
}
