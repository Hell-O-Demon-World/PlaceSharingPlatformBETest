package com.golfzonaca.officesharingplatform.repository.address;

import com.golfzonaca.officesharingplatform.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>, AddressRepositoryCustom {
}
