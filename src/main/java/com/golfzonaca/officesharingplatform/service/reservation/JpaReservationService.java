package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.Weekdays;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.service.reservation.dto.ReservedRoom;
import com.golfzonaca.officesharingplatform.service.reservation.validation.ReservationProcessValidation;
import com.golfzonaca.officesharingplatform.service.reservation.validation.ReservationRequestValidation;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.form.DefaultTimeOfDay;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedTypeAndDayForm;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaReservationService implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRequestValidation reservationRequestValidation;
    private final ReservationProcessValidation reservationProcessValidation;

    @Override
    public Map<String, String> findRoom(long placeId) {
        Map<String, String> response = new LinkedHashMap<>();
        Place place;
        Optional<Place> findPlace = placeRepository.findById(placeId);
        if (findPlace.isEmpty()) {
            response.put("NonExistPlaceError", "존재하지 않는 공간입니다.");
            return response;
        }
        place = findPlace.get();

        if (place.getRooms().isEmpty()) {
            response.put("NonExistRoomsError", "해당 공간에 등록된 대여공간이 없습니다.");
            return response;
        }
        List<Room> roomList = place.getRooms();
        Set<String> roomSet = new HashSet<>();
        for (Room room : roomList) {
            roomSet.add(room.getRoomKind().getRoomType());
        }
        response.put("room", roomSet.toString().replace("[", "").replace("]", ""));
        return response;
    }

    @Override
    public List<Integer> getReservationTimeList(Long placeId, SelectedTypeAndDayForm selectedTypeAndDayForm) {
        Optional<Place> resultPlace = placeRepository.findById(placeId);
        if (resultPlace.isEmpty()) {
            log.error("placeId 에 맞는 place 가 없습니다.");
            return new ArrayList<>();
        }
        Place findPlace = resultPlace.get();
        String selectedRoomType = selectedTypeAndDayForm.getSelectedType();
        String selectedDay = selectedTypeAndDayForm.getDay();
        int selectedStartTime = Integer.parseInt(selectedTypeAndDayForm.getStartTime());

        List<Room> reservedRoomList = roomRepository.findRoomByPlaceIdAndRoomType(placeId, selectedRoomType);
        List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate(placeId, selectedRoomType, TimeFormatter.toLocalDate(selectedDay));

        int totalReservationCount = reservedRoomList.size();
        int beforeReservationCount = countBeforeReservationList(findReservationList);

        int startTime = resultPlace.get().getPlaceStart().getHour();
        int endTime = resultPlace.get().getPlaceEnd().getHour();
        Map<Integer, Boolean> inputTimeMap = setStartTimeAndEndTime(getDefaultTimeMap(), startTime, endTime);

        if (!hasFullReservation(totalReservationCount, beforeReservationCount)) {
            Map<Integer, ReservedRoom> reservedRoomMap = getReservedRoomMap(findPlace, findReservationList, reservedRoomList);
            return getResultList(findPlace, selectedStartTime, reservedRoomMap);
        }
        return parsingMapToList(inputTimeMap);
    }

    private List<Integer> getResultList(Place findPlace, int selectedStartTime, Map<Integer, ReservedRoom> reservedRoomMap) {
        int plusMaxPointer = selectedStartTime;
        int minusMinPointer = selectedStartTime;
        for (int i = 0; i < reservedRoomMap.size(); i++) {
            ReservedRoom reservedRoom = reservedRoomMap.get(i);
            int plusPointer = selectedStartTime;
            int minusPointer = selectedStartTime;
            if (!(plusMaxPointer == findPlace.getPlaceEnd().getHour())) {
                for (int j = selectedStartTime; j < findPlace.getPlaceEnd().getHour() - 1; j++) {
                    if (reservedRoom.getTimeState(j) && reservedRoom.getTimeState(j + 1)) {
                        plusPointer = j + 1;
                    } else {
                        break;
                    }
                }
                if (plusPointer > plusMaxPointer) {
                    plusMaxPointer = plusPointer;
                }
            }
            if (!(minusMinPointer == findPlace.getPlaceStart().getHour())) {
                for (int j = selectedStartTime; j > findPlace.getPlaceStart().getHour(); j--) {
                    if (reservedRoom.getTimeState(j) && reservedRoom.getTimeState(j - 1)) {
                        minusPointer = j - 1;
                    } else {
                        break;
                    }
                }
                if (minusPointer < minusMinPointer) {
                    minusMinPointer = minusPointer;
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = minusMinPointer; i < plusMaxPointer + 1; i++) {
            result.add(i);
        }
        return result;
    }


    @Override
    public Map<String, String> validation(Map<String, String> response, User user, Place place, ProcessReservationData data) {
        response = reservationRequestValidation.validation(response, user, place, data);
        return response;
    }

    @Override
    public Map<String, String> saveReservation(Map<String, String> response, User user, Place place, ProcessReservationData data) {
        Room room = reservationProcessValidation.selectAvailableRoomForReservation(place, data);
        if (room == null) {
            response.put("NonexistentRoomTypeInPlaceError", "선택하신 타입은 해당 공간에 존재하지 않습니다.");
            return response;
        }

//        Room resultRoom = getResultRoom();

        Reservation reservation = new Reservation(user, place, room, data.getDate(), data.getStartTime(), data.getDate(), data.getEndTime());
        Reservation save = reservationRepository.save(reservation);
        if (save == null) {
            response.put("ReservationError", "예약 실패");
            log.error("예약에 실패하였습니다.");
            return response;
        }
        response.put("reservationId", save.getId().toString());
        return response;
    }

    @Override
    public List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate) {
        return reservationRepository.findResByPlaceIdAndRoomKindId(placeId, roomTypeId, resStartDate, resEndDate);
    }

    /**
     * Room 단위로 예약된 모든 목록을 반환
     */
    private Map<Integer, ReservedRoom> getReservedRoomMap(Place findPlace, List<Reservation> findReservationList, List<Room> reservedRoomList) {
        Map<Integer, ReservedRoom> reservedRoomMap = new HashMap<>();
        for (int i = 0; i < reservedRoomList.size(); i++) {
            ReservedRoom reservedRoom = new ReservedRoom(reservedRoomList.get(i).getId());
            reservedRoomMap.put(i, reservedRoom);
        }

        for (int i = 0; i < reservedRoomMap.size(); i++) {
            ReservedRoom reservedRoom = reservedRoomMap.get(i);
            for (Reservation reservation : findReservationList) {
                if (reservedRoom.getRoomId().equals(reservation.getRoom().getId())) {
                    for (int time = reservation.getResStartTime().getHour(); time < reservation.getResEndTime().getHour(); time++) {
                        if (reservedRoom.getTimeState(time) && isOpenToday(findPlace, time)) {
                            reservedRoom.getTimeStates().replace(time, false);
                        }
                    }
                    reservedRoom.setStartAndEndTimeMap(findPlace.getPlaceStart(), findPlace.getPlaceEnd());
                    reservedRoomMap.replace(i, reservedRoom);
                }
            }
        }
        return reservedRoomMap;
    }

    /**
     * 입력 받은 시간(time)이 place의 영업 시간 내에 존재하는지 여부 확인
     */
    private boolean isOpenToday(Place findPlace, Integer time) {
        LocalTime now = TimeFormatter.toLocalTime(Integer.toString(time));
        return now.isAfter(findPlace.getPlaceStart().minusHours(1)) && now.isBefore(findPlace.getPlaceEnd());
    }

    private List<Integer> parsingMapToList(Map<Integer, Boolean> inputTimeMap) {
        List<Integer> resultTimeList = new ArrayList<>();

        for (Map.Entry<Integer, Boolean> entry : inputTimeMap.entrySet()) {
            if (entry.getValue()) {
                resultTimeList.add(entry.getKey());
            }
        }
        return resultTimeList;
    }

    private boolean hasFullReservation(int totalReservationCount, int beforeReservationCount) {
        return totalReservationCount - beforeReservationCount > 0;
    }

    private Map<Integer, Boolean> setStartTimeAndEndTime(Map<Integer, Boolean> inputTimeMap, int startTime, int endTime) {
        if (endTime == 0) {
            endTime = 24;
        }
        for (int i = startTime; i < endTime; i++) {
            inputTimeMap.replace(i, false, true);
        }
        return inputTimeMap;
    }

    private int countBeforeReservationList(List<Reservation> findReservationList) {
        Set<Long> countRoomIdSet = new HashSet<>();
        for (Reservation reservation : findReservationList) {
            countRoomIdSet.add(reservation.getRoom().getId());
        }
        return countRoomIdSet.size();
    }

    private Map<Integer, Boolean> getDefaultTimeMap() {
        Map<Integer, Boolean> timeMap = new HashMap<>();
        for (int time : DefaultTimeOfDay.getTimes()) {
            timeMap.put(time, false);
        }
        return timeMap;
    }

}
