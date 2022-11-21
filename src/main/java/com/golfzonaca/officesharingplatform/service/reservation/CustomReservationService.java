package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.type.Weekdays;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.web.reservation.form.DefaultTimeOfDay;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedDateTimeForm;
import com.golfzonaca.officesharingplatform.web.reservation.form.StringDateForm;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomReservationService implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomKindRepository roomKindRepository;
    private final PlaceRepository placeRepository;

    @Override
    public JsonObject findRoom(long placeId) {
        List<Integer> meetingRoomList = new ArrayList<>();
        List<Integer> officeList = new ArrayList<>();
        JsonObject responseData = new JsonObject();

        List<Integer> findRoomTypeList = roomRepository.findRoomTypeByPlaceId(placeId);

        int deskQuantity = Collections.frequency(findRoomTypeList, 1);
        responseData.addProperty("desk", deskQuantity != 0);

        for (int i = 0; i < 5; i++) {
            if (Collections.frequency(findRoomTypeList, i + 1) != 0) {
                if (i + 1 == 2) {
                    meetingRoomList.add(4);
                } else if (i + 1 == 3) {
                    meetingRoomList.add(6);
                } else if (i + 1 == 4) {
                    meetingRoomList.add(10);
                } else if (i + 1 == 5) {
                    meetingRoomList.add(20);
                }
            }
        }
        String meetingRoom = new Gson().toJson(meetingRoomList);
        responseData.addProperty("meetingRoom", meetingRoom);

        for (int i = 0; i < 4; i++) {
            if (Collections.frequency(findRoomTypeList, i + 6) != 0) {
                if (i + 6 == 6) {
                    officeList.add(20);
                } else if (i + 6 == 7) {
                    officeList.add(40);
                } else if (i + 6 == 8) {
                    officeList.add(70);
                } else {
                    officeList.add(100);
                }
            }
        }
        String office = new Gson().toJson(officeList);
        responseData.addProperty("office", office);
        return responseData;
    }

    @Override
    public List<Integer> getReservationTimeList(Long placeId, SelectedDateTimeForm selectedDateTimeForm) {
        if (placeRepository.findById(placeId) == null) {
            log.error("placeId 에 맞는 place가 없습니다.");
            return new ArrayList<>();
        }
        String selectedRoomType = selectedDateTimeForm.getSelectedType();
        String selectedYear = selectedDateTimeForm.getYear().toString();
        String selectedMonth = selectedDateTimeForm.getMonth().toString();
        String selectedDay = selectedDateTimeForm.getDay().toString();
        Place findPlace = placeRepository.findById(placeId).get();

        String reservationDayOfWeek = getReservationAvailableDaysOfWeek(selectedYear, selectedMonth, selectedDay);
        List<String> placeOpenList = getPlaceOpenList(findPlace);

        Map<Integer, Boolean> inputTimeMap = getDefaultTimeMap();
        if (isOpenToday(reservationDayOfWeek, placeOpenList)) {
            List<Room> reservationRoomList = roomRepository.findRoomByPlaceIdAndRoomType(placeId, selectedRoomType);
            List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate(placeId, selectedRoomType, StringDateForm.toLocalDate(selectedYear, selectedMonth, selectedDay));

            int totalReservationCount = reservationRoomList.size();
            int beforeReservationCount = countBeforeReservationList(findReservationList);

            int startTime = findPlace.getPlaceStart().getHour();
            int endTime = findPlace.getPlaceEnd().getHour();

            if (hasFullReservation(totalReservationCount, beforeReservationCount)) {
                inputTimeMap = setStartTimeAndEndTime(inputTimeMap, startTime, endTime);
            } else {
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
            }
        }
        return parsingMapToList(inputTimeMap);
    }

    private String getReservationAvailableDaysOfWeek(String selectedYear, String selectedMonth, String selectedDay) {
        LocalDate reservationDate = StringDateForm.toLocalDate(selectedYear, selectedMonth, selectedDay);
        return getDaysOfWeek(reservationDate);
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
            if (entry.getValue() == true) {
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
        for (int time: DefaultTimeOfDay.getTimes()) {
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

    @Override
    public Map<String, String> reservation(Map<String, String> errorMap, User user, Place place, ResRequestData resRequestData) {
        Room room = null;
        for (Room candidate : place.getRooms()) {
            if (candidate.getReservationList().size() != 0) {
                for (Reservation reservation : candidate.getReservationList()) {

                }
            }
        }

        for (Room candidate : place.getRooms()) {
            if (candidate.getRoomKind().getRoomType().equals(resRequestData.getSelectedType())) {
                room = candidate;
                break;
            }
        }
        Reservation reservation = new Reservation(user, place, room, resRequestData.getDate(), resRequestData.getStartTime(), resRequestData.getDate(), resRequestData.getEndTime());
        Reservation save = reservationRepository.save(reservation);
        if (save == null) {
            errorMap.put("ReservationError", "예약 실패");
            log.info("예약에 실패하였습니다.");
        }
        return errorMap;
    }

    private boolean isOpenToday(String reservationDayOfWeek, List<String> placeOpenList) {
        Map<String, Boolean> placeOpenMap = getOpenDayMap(placeOpenList);
        return placeOpenMap.get(reservationDayOfWeek);
    }
}
