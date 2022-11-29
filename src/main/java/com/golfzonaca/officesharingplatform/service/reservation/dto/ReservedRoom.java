package com.golfzonaca.officesharingplatform.service.reservation.dto;

import com.golfzonaca.officesharingplatform.web.reservation.form.DefaultTimeOfDay;
import lombok.Getter;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
@Getter
public class ReservedRoom {
    private Long roomId;
    private Map<Integer, Boolean> timeStates;

    public ReservedRoom(Long roomId) {
        this.roomId = roomId;
        this.timeStates = getTimeMap();
    }

    private Map<Integer, Boolean> getTimeMap() {
        Map<Integer, Boolean> timeMap = new HashMap<>();
        for (int time : DefaultTimeOfDay.getTimes()) {
            timeMap.put(time, true);
        }
        return timeMap;
    }

    public Boolean getTimeState(int time) {
        return this.timeStates.get(time);
    }
    public void setStartAndEndTimeMap(LocalTime startLocalTime, LocalTime endLocalTime) {
        Map<Integer, Boolean> resultMap = this.timeStates;
        int startTime = startLocalTime.getHour();
        int endTime = endLocalTime.getHour();
        if (endTime - startTime == 0) {
            return;
        }
        if (endTime - startTime < 0) {
            int tempTime = endTime;
            endTime = startTime;
            startTime = tempTime;
            for (int i = startTime; i < endTime; i++) {
                if (i == 0) {
                    i = 24;
                }
                resultMap.replace(i, false);
            }
        } else {

            for (int i = DefaultTimeOfDay.getStartTime(); i < startTime; i++) {
                if (i == 0) {
                    i = 24;
                }
                resultMap.replace(i, false);
            }
            for (int j = endTime; j <= DefaultTimeOfDay.getEndTime(); j++) {
                if (j == 0) {
                    j = 24;
                }
                resultMap.replace(j, false);
            }
        }
        this.timeStates = resultMap;
    }
}
