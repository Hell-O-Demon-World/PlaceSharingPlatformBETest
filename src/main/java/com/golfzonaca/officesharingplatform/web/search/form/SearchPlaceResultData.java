package com.golfzonaca.officesharingplatform.web.search.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPlaceResultData {
    private long id;
    private String placeName;
    private long addressId;
    private String placeAddinfo;
}
