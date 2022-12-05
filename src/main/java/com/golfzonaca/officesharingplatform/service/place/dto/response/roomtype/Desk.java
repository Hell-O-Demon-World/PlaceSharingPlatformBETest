package com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Desk {
    private boolean exist;
    private Integer price;
    private List<String> images;
}
