package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

@Data
public class Room {

    private Long id;
    private Long roomKindId;
    private Long placeId;
    private Long companyId;
    private int totalNum;
    private boolean roomState;

}
