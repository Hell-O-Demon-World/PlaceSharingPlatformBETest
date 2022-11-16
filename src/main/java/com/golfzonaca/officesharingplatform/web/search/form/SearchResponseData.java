package com.golfzonaca.officesharingplatform.web.search.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseData {
    private long key;
    private String name;
    private String address;
    private String option;
    private String postcode;

}
