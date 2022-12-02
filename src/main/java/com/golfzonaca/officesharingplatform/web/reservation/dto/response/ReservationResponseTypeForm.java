package com.golfzonaca.officesharingplatform.web.reservation.dto.response;

import com.golfzonaca.officesharingplatform.web.reservation.dto.response.type.Desk;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.type.MeetingRoom;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.type.Office;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ReservationResponseTypeForm {
    private Desk desk;
    private List<MeetingRoom> meetingRoom;
    private List<Office> office;

    private Map<String, String> response;

    public void toEntity(Boolean desk, List<Integer> meetingRoom, List<Integer> office, Map<String, String> response) {
        this.desk = desk;
        this.meetingRoom = meetingRoom;
        this.office = office;
        this.response = response;
    }
}
