package com.golfzonaca.officesharingplatform.web.reservation.dto.response.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Desk {
    private boolean exist;
    private Integer price;
}
