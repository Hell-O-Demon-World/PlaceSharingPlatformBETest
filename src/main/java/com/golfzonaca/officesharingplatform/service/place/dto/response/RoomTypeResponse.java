package com.golfzonaca.officesharingplatform.service.place.dto.response;

import com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype.Desk;
import com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype.MeetingRoom;
import com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype.Office;
import lombok.Getter;

import java.util.List;

@Getter
public class RoomTypeResponse {
    private Desk desk;
    private List<MeetingRoom> meetingRoom;
    private List<Office> office;

    public void toEntity(Desk desk, List<MeetingRoom> meetingRoom, List<Office> office) {
        this.desk = desk;
        this.meetingRoom = meetingRoom;
        this.office = office;
    }
}
