package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.form.DefaultTimeOfDay;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedTypeAndDayForm;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@RequiredArgsConstructor
class JpaReservationServiceTest {

    @Data
    @Builder
    public static class ReservationTest {

        private Long id;
        private Long userId;
        private Long placeId;
        private Long roomId;
        private Long roomKindId;
        private LocalDate resStartDate;
        private LocalTime resStartTime;
        private LocalDate resEndDate;
        private LocalTime resEndTime;
    }

    @Test
    void getReservationTimeListTest() {
        //given
        Place findPlace = Place.builder().id(5L)
                .placeStart(TimeFormatter.toLocalTime("8"))
                .placeEnd(TimeFormatter.toLocalTime("18"))
                .company(Company.builder().id(3L).build())
                .build();
        List<ReservationTest> findReservationList = getReservationTestList();
        List<Room> reservedRoomList = getReservationRoomList();
        int selectedStartTime = 11;
        //when
        Map<Integer, ReservedRoom> reservedRoomMap = getReservedRoomMap(findPlace, findReservationList, reservedRoomList);

        int plusMaxPointer = selectedStartTime;
        int minusMinPointer = selectedStartTime;
        for (int i = 0; i < reservedRoomMap.size(); i++) {
            ReservedRoom reservedRoom = reservedRoomMap.get(i);
            int plusPointer = selectedStartTime;
            int minusPointer = selectedStartTime;
            for (int j = selectedStartTime; j < findPlace.getPlaceEnd().getHour() - 1; j++) {
                if (reservedRoom.timeStates.get(j) && reservedRoom.timeStates.get(j + 1)) {
                    plusPointer = j + 1;
                }
            }
            if (plusPointer > plusMaxPointer) {
                plusMaxPointer = plusPointer;
            }
            for (int j = selectedStartTime; j > findPlace.getPlaceStart().getHour(); j--) {
                if (reservedRoom.timeStates.get(j) && reservedRoom.timeStates.get(j - 1)) {
                    minusPointer = j - 1;
                }
            }
            if (minusPointer < minusMinPointer) {
                minusMinPointer = minusPointer;
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = minusMinPointer; i < plusMaxPointer + 1; i++) {
            result.add(i);
        }
        System.out.println("result = " + result);

    }

    private Map<Integer, ReservedRoom> getReservedRoomMap(Place findPlace, List<ReservationTest> findReservationList, List<Room> reservedRoomList) {
        Map<Integer, ReservedRoom> reservedRoomMap = new HashMap<>();
        for (int i = 0; i < reservedRoomList.size(); i++) {
            ReservedRoom reservedRoom = new ReservedRoom(reservedRoomList.get(i).getId());
            reservedRoomMap.put(i, reservedRoom);
        }

        for (int i = 0; i < reservedRoomMap.size(); i++) {
            ReservedRoom reservedRoom = reservedRoomMap.get(i);
            for (ReservationTest reservationTest : findReservationList) {
                if (reservedRoom.roomId.equals(reservationTest.roomId)) {
                    for (int time = reservationTest.resStartTime.getHour(); time < reservationTest.resEndTime.getHour(); time++) {
                        if (!reservedRoom.timeStates.get(time) && isOpenToday(findPlace, time)) {
                            reservedRoom.timeStates.replace(time, true);
                        }
                    }
                    reservedRoom.setStartAndEndTimeMap(findPlace.getPlaceStart(), findPlace.getPlaceEnd());
                    reservedRoomMap.replace(i, reservedRoom);
                }
            }
        }
        return reservedRoomMap;
    }

    private boolean isOpenToday(Place findPlace, Integer time) {

        LocalTime now = TimeFormatter.toLocalTime(Integer.toString(time));
        return now.isAfter(findPlace.getPlaceStart().minusHours(1)) && now.isBefore(findPlace.getPlaceEnd());
    }

    @Test
    void getStartAndEndTimeTest() {
        Room room = Room.builder()
                .id(1L)
                .roomKind(RoomKind.builder().id(1L).build())
                .place(Place.builder().id(5L)
                        .company(Company.builder().id(3L).build())
                        .build())
                .totalNum(1)
                .build();
        ReservedRoom reservedRoom = new ReservedRoom(1L);
        ReservedRoom reservedRoom1 = new ReservedRoom(2L);
        reservedRoom.setStartAndEndTimeMap(TimeFormatter.toLocalTime("18"), TimeFormatter.toLocalTime("4"));
        reservedRoom1.setStartAndEndTimeMap(TimeFormatter.toLocalTime("4"), TimeFormatter.toLocalTime("18"));
        Map<Integer, ReservedRoom> reservedRoomMap = new HashMap<Integer, ReservedRoom>();
        reservedRoomMap.put(1, reservedRoom);
        reservedRoomMap.put(2, reservedRoom1);

        System.out.println("startAndEndTimeMap = " + reservedRoomMap.get(1).timeStates);
        System.out.println("reservedRoomMap.get = " + reservedRoomMap.get(2).timeStates);
    }

    private static List<ReservationTest> getReservationTestList() {
        List<ReservationTest> findReservationList = new ArrayList<>();
        for (long i = 123; i < 142; i++) {
            long roomId;
            LocalTime resStartTime;
            LocalTime resEndTime;
            if (i == 123) {
                roomId = 49L;
                resStartTime = TimeFormatter.toLocalTime("8");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 124) {
                roomId = 50L;
                resStartTime = TimeFormatter.toLocalTime("12");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 125) {
                roomId = 51L;
                resStartTime = TimeFormatter.toLocalTime("8");
                resEndTime = TimeFormatter.toLocalTime("10");
            } else if (i == 126) {
                roomId = 51L;
                resStartTime = TimeFormatter.toLocalTime("13");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 127) {
                roomId = 52L;
                resStartTime = TimeFormatter.toLocalTime("8");
                resEndTime = TimeFormatter.toLocalTime("11");
            } else if (i == 128) {
                roomId = 53L;
                resStartTime = TimeFormatter.toLocalTime("9");
                resEndTime = TimeFormatter.toLocalTime("11");
            } else if (i == 129) {
                roomId = 53L;
                resStartTime = TimeFormatter.toLocalTime("13");
                resEndTime = TimeFormatter.toLocalTime("15");
            } else if (i == 130) {
                roomId = 53L;
                resStartTime = TimeFormatter.toLocalTime("16");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 131) {
                roomId = 54L;
                resStartTime = TimeFormatter.toLocalTime("10");
                resEndTime = TimeFormatter.toLocalTime("12");
            } else if (i == 132) {
                roomId = 54L;
                resStartTime = TimeFormatter.toLocalTime("13");
                resEndTime = TimeFormatter.toLocalTime("15");
            } else if (i == 133) {
                roomId = 54L;
                resStartTime = TimeFormatter.toLocalTime("15");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 134) {
                roomId = 55L;
                resStartTime = TimeFormatter.toLocalTime("8");
                resEndTime = TimeFormatter.toLocalTime("10");
            } else if (i == 135) {
                roomId = 55L;
                resStartTime = TimeFormatter.toLocalTime("17");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 136) {
                roomId = 56L;
                resStartTime = TimeFormatter.toLocalTime("8");
                resEndTime = TimeFormatter.toLocalTime("10");
            } else if (i == 137) {
                roomId = 56L;
                resStartTime = TimeFormatter.toLocalTime("16");
                resEndTime = TimeFormatter.toLocalTime("18");
            } else if (i == 138) {
                roomId = 57L;
                resStartTime = TimeFormatter.toLocalTime("9");
                resEndTime = TimeFormatter.toLocalTime("11");
            } else if (i == 139) {
                roomId = 57L;
                resStartTime = TimeFormatter.toLocalTime("13");
                resEndTime = TimeFormatter.toLocalTime("15");
            } else if (i == 140) {
                roomId = 58L;
                resStartTime = TimeFormatter.toLocalTime("8");
                resEndTime = TimeFormatter.toLocalTime("10");
            } else {
                roomId = 58L;
                resStartTime = TimeFormatter.toLocalTime("14");
                resEndTime = TimeFormatter.toLocalTime("17");
            }

            ReservationTest reservation = ReservationTest.builder()
                    .id(i)
                    .userId(3L)
                    .placeId(5L)
                    .roomId(roomId)
                    .roomKindId(1L)
                    .resStartTime(resStartTime)
                    .resEndTime(resEndTime)
                    .resStartDate(TimeFormatter.toLocalDate("2022-12-02"))
                    .resEndDate(TimeFormatter.toLocalDate("2022-12-02"))
                    .build();
            findReservationList.add(reservation);
        }
        return findReservationList;
    }

    @Test
    void hasFullReservationTest() {
        SelectedTypeAndDayForm selectedTypeAndDayForm = new SelectedTypeAndDayForm();
        selectedTypeAndDayForm.setSelectedType("desk");
        selectedTypeAndDayForm.setDay("2022-12-02");
        selectedTypeAndDayForm.setStartTime("13");
        //given
        List<Reservation> findReservationList = getFindReservationList();
        List<Room> reservationRoomList = getReservationRoomList();
        int totalReservationCount = reservationRoomList.size();
        int beforeReservationCount = findReservationList.size();

        System.out.println("totalReservationCount = " + totalReservationCount);
        System.out.println("beforeReservationCount = " + beforeReservationCount);
        assertThat(hasFullReservation(totalReservationCount, beforeReservationCount)).isTrue();

    }

    private Map<Integer, Boolean> getDefaultTimeMap(Place place) {
        Map<Integer, Boolean> defaultTimeMap = getStartAndEndTimeMap(place.getPlaceStart(), place.getPlaceEnd());

        return defaultTimeMap;
    }

    private Map<Integer, Boolean> getTimeMap() {
        Map<Integer, Boolean> timeMap = new HashMap<>();
        for (int time : DefaultTimeOfDay.getTimes()) {
            timeMap.put(time, false);
        }
        return timeMap;
    }

    private Map<Integer, Boolean> getStartAndEndTimeMap(LocalTime startLocalTime, LocalTime endLocalTime) {
        Map<Integer, Boolean> resultMap = getTimeMap();
        int startTime = startLocalTime.getHour();
        int endTime = endLocalTime.getHour();
        if (endTime == 0) {
            endTime = 24;
        }
        for (int i = startTime; i < endTime; i++) {
            resultMap.replace(i, false, true);
        }
        return resultMap;
    }

    private static List<Room> getReservationRoomList() {
        List<Room> findRoomList = new ArrayList<>();
        for (long i = 49; i < 59; i++) {
            Room room = Room.builder()
                    .id(i)
                    .roomKind(RoomKind.builder().id(1L).build())
                    .place(Place.builder().id(5L)
                            .company(Company.builder().id(3L).build())
                            .build())
                    .totalNum(1)
                    .build();
            findRoomList.add(room);
        }
        return findRoomList;
    }

    private static List<Reservation> getFindReservationList() {
        List<Reservation> findReservationList = new ArrayList<>();
        for (long i = 49; i < 59; i++) {
            if (i == 57) {
                continue;
            }
            Reservation reservation = Reservation.builder()
                    .id(i + 120L)
                    .place(Place.builder().id(5L).build())
                    .user(User.builder().id(3L).build())
                    .room(Room.builder()
                            .id(i)
                            .roomKind(RoomKind.builder().id(1L).build())
                            .place(Place.builder().id(5L)
                                    .company(Company.builder().id(3L).build())
                                    .build())
                            .totalNum(1)
                            .build())
                    .resStartDate(TimeFormatter.toLocalDate("2022-12-02"))
                    .resEndDate(TimeFormatter.toLocalDate("2022-12-02"))
                    .resStartTime(TimeFormatter.toLocalTime("8"))
                    .resEndTime(TimeFormatter.toLocalTime("18"))
                    .build();
            findReservationList.add(reservation);
        }
        return findReservationList;
    }

    private boolean hasFullReservation(int totalReservationCount, int beforeReservationCount) {
        return totalReservationCount - beforeReservationCount > 0;
    }

    private static class ReservedRoom {
        private Long roomId;
        private Map<Integer, Boolean> timeStates;

//        public ReservedRoom(Long roomId, LocalTime startTime, LocalTime endTime) {
//            this.roomId = roomId;
//            this.timeStates = getStartAndEndTimeMap(startTime, endTime);
//        }

        public ReservedRoom(Long roomId) {
            this.roomId = roomId;
            this.timeStates = getTimeMap();
        }

        private Long getRoomId() {
            return this.roomId;
        }

        private Map<Integer, Boolean> getTimeMap() {
            Map<Integer, Boolean> timeMap = new HashMap<>();
            for (int time : DefaultTimeOfDay.getTimes()) {
                timeMap.put(time, false);
            }
            return timeMap;
        }

        private void setStartAndEndTimeMap(LocalTime startLocalTime, LocalTime endLocalTime) {
            Map<Integer, Boolean> resultMap = this.timeStates;
            int startTime = startLocalTime.getHour();
            int endTime = endLocalTime.getHour();

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
}