package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.Weekdays;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.service.reservation.validation.ReservationProcessValidation;
import com.golfzonaca.officesharingplatform.service.reservation.validation.ReservationRequestValidation;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.form.DefaultTimeOfDay;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedTypeAndDayForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Optional<Place> result = placeRepository.findById(placeId);
        if (result.isEmpty()) {
            log.error("placeId 에 맞는 place가 없습니다.");
            return new ArrayList<>();
        }
        Place findPlace = result.get();
        String selectedRoomType = selectedTypeAndDayForm.getSelectedType();
        String selectedDay = selectedTypeAndDayForm.getDay();

        String reservationDayOfWeek = getReservationAvailableDaysOfWeek(selectedDay);
        List<String> placeOpenList = getPlaceOpenList(findPlace);

        Map<Integer, Boolean> inputTimeMap = getDefaultTimeMap();
        if (isOpenToday(reservationDayOfWeek, placeOpenList)) {
            List<Room> reservationRoomList = roomRepository.findRoomByPlaceIdAndRoomType(placeId, selectedRoomType);
            List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate(placeId, selectedRoomType, TimeFormatter.toLocalDate(selectedDay));

            int totalReservationCount = reservationRoomList.size();
            int beforeReservationCount = countBeforeReservationList(findReservationList);

            int startTime = findPlace.getPlaceStart().getHour();
            int endTime = findPlace.getPlaceEnd().getHour();

            if (hasFullReservation(totalReservationCount, beforeReservationCount)) {
                inputTimeMap = setStartTimeAndEndTime(inputTimeMap, startTime, endTime);
            } else {
                inputTimeMap = getAvailableRoomMap(inputTimeMap, findReservationList, startTime, endTime);
            }
        }
        return parsingMapToList(inputTimeMap);
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

    private Map<Integer, Boolean> getAvailableRoomMap(Map<Integer, Boolean> inputTimeMap, List<Reservation> findReservationList, int startTime, int endTime) {
        for (Reservation reservation : findReservationList) {
            for (int i = reservation.getResStartTime().getHour(); i < reservation.getResEndTime().getHour(); i++) {
                if (inputTimeMap.get(i) == true) {
                    continue;
                }
                inputTimeMap.replace(i, false, true);
            }
        }

        for (int i = startTime; i < endTime; i++) {
            if (inputTimeMap.get(i) == true) {
                break;
            }
            inputTimeMap.replace(i, false, true);
        }
        for (int i = endTime - 1; i > startTime; i--) {
            if (inputTimeMap.get(i) == true) {
                break;
            }
            inputTimeMap.replace(i, false, true);
        }
        return inputTimeMap;
    }

    private String getReservationAvailableDaysOfWeek(String day) {
        return getDaysOfWeek(TimeFormatter.toLocalDate(day));
    }

    private static String getDaysOfWeek(LocalDate localDate) {
        return localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
    }

    private static List<String> getPlaceOpenList(Place place) {
        return Arrays.asList(place.getOpenDays().split(", "));
    }

    private List<Integer> parsingMapToList(Map<Integer, Boolean> inputTimeMap) {
        List<Integer> resultTimeList = new ArrayList<>();
        Iterator<Map.Entry<Integer, Boolean>> itr = inputTimeMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<Integer, Boolean> entry = itr.next();
            if (entry.getValue()) {
                resultTimeList.add(entry.getKey());
            }
        }
        return resultTimeList;
    }

    private boolean hasFullReservation(int totalReservationCount, int beforeReservationCount) {
        boolean available = false;
        if (totalReservationCount - beforeReservationCount > 0) {
            available = true;
        }
        return available;
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
        for (int i = 0; i < findReservationList.size(); i++) {
            countRoomIdSet.add(findReservationList.get(i).getRoom().getId());
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

    private Map<String, Boolean> getOpenDayMap(List<String> placeOpenList) {
        Map<String, Boolean> openDayMap = new LinkedHashMap<>();
        for (Weekdays item : Weekdays.values()) {
            if (placeOpenList.contains(item.toString())) {
                openDayMap.put(item.toString(), true);
            } else {
                openDayMap.put(item.toString(), false);
            }
        }
        return openDayMap;
    }

    @Override
    public List<Reservation> findResByPlaceIdAndRoomKindId(long placeId, long roomTypeId, LocalDate resStartDate, LocalDate resEndDate) {
        return reservationRepository.findResByPlaceIdAndRoomKindId(placeId, roomTypeId, resStartDate, resEndDate);
    }

    private boolean isOpenToday(String reservationDayOfWeek, List<String> placeOpenList) {
        Map<String, Boolean> placeOpenMap = getOpenDayMap(placeOpenList);
        return placeOpenMap.get(reservationDayOfWeek);
    }
}
