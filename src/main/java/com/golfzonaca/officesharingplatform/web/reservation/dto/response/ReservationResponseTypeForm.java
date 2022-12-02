package com.golfzonaca.officesharingplatform.web.reservation.dto.response;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ReservationResponseTypeForm {
    private Boolean desk;
    private List<Integer> meetingRoom;
    private List<Integer> office;

    private Map<String, String> response;

    public void toEntity(Boolean desk, List<Integer> meetingRoom, List<Integer> office, Map<String, String> response) {
        this.desk = desk;
        this.meetingRoom = meetingRoom;
        this.office = office;
        this.response = response;
    }
}
