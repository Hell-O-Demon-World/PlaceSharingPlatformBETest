package com.golfzonaca.officesharingplatform.web.search.dto;

import com.golfzonaca.officesharingplatform.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    private long key;
    private String name;
    private Address address;
    private String option;


}
