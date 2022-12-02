package com.golfzonaca.officesharingplatform.service.reservation;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.service.reservation.dto.ReservedRoom;
import com.golfzonaca.officesharingplatform.service.reservation.validation.ReservationRequestValidation;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.golfzonaca.officesharingplatform.web.reservation.dto.process.ProcessReservationData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseData;
import com.golfzonaca.officesharingplatform.web.reservation.dto.response.ReservationResponseTypeForm;
import com.golfzonaca.officesharingplatform.web.reservation.form.DefaultTimeOfDay;
import com.golfzonaca.officesharingplatform.web.reservation.form.StringDateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class JpaReservationService implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRequestValidation reservationRequestValidation;

    @Override
    public ReservationResponseTypeForm findRoom(long placeId) {
        Map<String, String> response = new LinkedHashMap<>();

        Place findPlace = placeRepository.findById(placeId).orElseThrow(() -> new NullPointerException("NonExistPlaceError::: 존재하지 않는 공간입니다."));
        if (findPlace.getRooms().isEmpty()) {
            response.put("NonExistRoomsError", "해당 공간에 등록된 대여공간이 없습니다.");
        }

        List<Room> roomList = findPlace.getRooms();
        Set<String> nonDuplicatedRoomSet = new HashSet<>();
        for (Room room : roomList) {
            nonDuplicatedRoomSet.add(room.getRoomKind().getRoomType());
        }
        return getReservationResponseForm(response, nonDuplicatedRoomSet);
    }

    private static ReservationResponseTypeForm getReservationResponseForm(Map<String, String> response, Set<String> nonDuplicatedRoomSet) {
        ReservationResponseTypeForm responseForm = new ReservationResponseTypeForm();
        boolean responseDesk = false;
        List<Integer> responseMeetingRoom = new ArrayList<>();
        List<Integer> responseOffice = new ArrayList<>();
        for (String room : nonDuplicatedRoomSet) {
            if (room.equals("DESK")) {
                responseDesk = true;
            } else {
                String roomKindTag = room.replaceAll("[^0-9]", "");
                if (room.contains("MEETINGROOM")) {
                    responseMeetingRoom.add(Integer.parseInt(roomKindTag));
                } else {
                    responseOffice.add(Integer.parseInt(roomKindTag));
                }
            }
        }
        Collections.sort(responseMeetingRoom);
        Collections.sort(responseOffice);
        responseForm.toEntity(responseDesk, responseMeetingRoom, responseOffice, response);
        return responseForm;
    }

    @Override
    public List<ReservationResponseData> getReservationResponseData(Long placeId, String selectedType, String inputDate) {
        List<ReservationResponseData> result = new ArrayList<>();
        Optional<Place> optFindPlace = placeRepository.findById(placeId);
        if (optFindPlace.isEmpty()) {
            log.error("placeId 에 맞는 place 가 없습니다.");
            result.add(new ReservationResponseData());
            return result;
        }
        Place findPlace = optFindPlace.get();
        String selectedRoomType = selectedType.toUpperCase();
        LocalDate selectedStartDate = TimeFormatter.toLocalDate(inputDate);
        LocalDate selectedEndDate = TimeFormatter.toLocalDate(inputDate).plusMonths(1);

        return getTotalDayData(findPlace, selectedRoomType, selectedStartDate, selectedEndDate);
    }

    private List<ReservationResponseData> getTotalDayData(Place findPlace, String roomType, LocalDate selectedStartDate, LocalDate selectedEndDate) {
        List<ReservationResponseData> result = new ArrayList<>();
        String[] openDays = findPlace.getOpenDays().split(", ");

        int startYear = selectedStartDate.getYear();
        int endYear = selectedEndDate.getYear();
        List<Integer> years = getTotalYears(startYear, endYear);
        for (Integer year : years) {
            Month startMonth = selectedStartDate.getMonth();
            Month endMonth = selectedEndDate.getMonth();
            if (startYear != endYear) {
                if (startYear == year) {
                    endMonth = Month.of(12);
                } else if (endYear == year) {
                    startMonth = Month.of(1);
                } else {
                    startMonth = Month.of(1);
                    endMonth = Month.of(12);
                }
            }
            List<Month> months = getTotalMonth(startMonth, endMonth);
            for (Month month : months) {
                int startDay = selectedStartDate.getDayOfMonth();
                int endDay = selectedEndDate.getDayOfMonth();
                Calendar cal = Calendar.getInstance();
                if (month.equals(startMonth) && year.equals(startYear)) {
                    cal.set(year, month.getValue() - 1, startDay);
                    endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                } else if (month.equals(endMonth) && year.equals(endYear)) {
                    startDay = 1;
                    cal.set(year, month.getValue() - 1, startDay);
                } else {
                    startDay = 1;
                    cal.set(year, month.getValue() - 1, startDay);
                    endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                }
                for (int day = startDay; day < endDay + 1; day++) {
                    LocalDate date = StringDateForm.toLocalDate(String.valueOf(year), String.valueOf(month.getValue()), String.valueOf(day));
                    boolean state;
                    if (isOpenDaysByDate(openDays, date)) {
                        state = false;
                    } else if (roomType.contains("OFFICE")) {
                        state = getOfficeStateThatDate(findPlace.getId(), roomType, date);
                    } else {
                        List<Room> roomByPlaceIdAndRoomType = roomRepository.findRoomByPlaceIdAndRoomType(findPlace.getId(), roomType);
                        List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate(findPlace.getId(), roomType, date);
                        Map<Integer, ReservedRoom> reservedRoomMap = getReservedRoomMap(findPlace, findReservationList, roomByPlaceIdAndRoomType);
                        state = !isFullReservation(findPlace, reservedRoomMap);
                    }
                    result.add(ReservationResponseData.builder()
                            .state(state)
                            .productType(roomType)
                            .date(date)
                            .build());
                }
            }
        }
        return result;
    }

    private boolean isOpenDaysByDate(String[] openDays, LocalDate date) {
        boolean state = true;
        for (String d : openDays) {
            if (TimeFormatter.toDayOfTheWeek(date).equals(d)) {
                state = false;
                break;
            }
        }
        return state;
    }

    private boolean getOfficeStateThatDate(Long placeId, String roomType, LocalDate date) {
        return !existReservationThatDay(placeId, roomType, date);
    }

    private List<Integer> getTotalYears(int startYear, int endYear) {
        List<Integer> result = new ArrayList<>();
        for (int i = startYear; i < endYear + 1; i++) {
            result.add(i);
        }
        return result;
    }

    private List<Month> getTotalMonth(Month startMonth, Month endMonth) {
        List<Month> result = new ArrayList<>();
        if (startMonth.equals(endMonth)) {
            result.add(startMonth);
        } else {
            for (int i = startMonth.getValue(); i < endMonth.getValue() + 1; i++) {
                result.add(Month.of(i));
            }
        }
        return result;
    }

    private boolean existReservationThatDay(Long placeId, String roomType, LocalDate date) {
        Optional<Reservation> optFindReservation = reservationRepository.findByPlaceIdAndRoomTypeAndDate(placeId, roomType, date);
        return optFindReservation.isPresent();
    }

    @Override
    public List<Integer> findAvailableTimes(Long placeId, String selectedType, LocalDate date, LocalTime startTime) {
        List<Integer> result = new ArrayList<>();
        Optional<Place> optFindPlace = placeRepository.findById(placeId);
        if (optFindPlace.isEmpty()) {
            log.error("placeId 에 맞는 place 가 없습니다.");
            return result;
        }
        Place findPlace = optFindPlace.get();

        return getTimeList(findPlace, selectedType, date, startTime);
    }

    private List<Integer> getTimeList(Place findPlace, String selectedRoomType, LocalDate selectedDate, LocalTime selectedStartTime) {
        List<Room> reservedRoomList = roomRepository.findRoomByPlaceIdAndRoomType(findPlace.getId(), selectedRoomType);
        List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate(findPlace.getId(), selectedRoomType, selectedDate);

        int totalReservationCount = reservedRoomList.size();
        int beforeReservationCount = countBeforeReservationList(findReservationList);

        int startTime = findPlace.getPlaceStart().getHour();
        int endTime = findPlace.getPlaceEnd().getHour();
        Map<Integer, Boolean> inputTimeMap = setStartTimeAndEndTime(getDefaultTimeMap(), startTime, endTime);

        if (!hasFullReservation(totalReservationCount, beforeReservationCount)) {
            Map<Integer, ReservedRoom> reservedRoomMap = getReservedRoomMap(findPlace, findReservationList, reservedRoomList);
            return getResultList(findPlace, selectedStartTime.getHour(), reservedRoomMap);
        }
        return parsingMapToList(inputTimeMap);
    }

    private boolean isFullReservation(Place findPlace, Map<Integer, ReservedRoom> reservedRoomMap) {
        boolean result = true;

        int startTime = findPlace.getPlaceStart().getHour();
        int endTime = findPlace.getPlaceEnd().getHour();
        for (int i = 0; i < reservedRoomMap.size(); i++) {
            ReservedRoom reservedRoom = reservedRoomMap.get(i);
            for (int j = startTime; j < endTime; j++) {
                if (reservedRoom.getTimeState(j)) {
                    result = false;
                    break;
                }
            }
            if (!result) {
                break;
            }
        }
        return result;
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
    public Map<String, Object> saveReservation(User user, Place place, ProcessReservationData data) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, String> errorMap = new HashMap<>();

        LocalTime startTime = data.getStartTime();
        LocalTime endTime = data.getEndTime();
        LocalDate date = data.getStartDate();
        String selectedType = data.getSelectedType();

        Optional<Room> resultRoom = Optional.ofNullable(getResultRoom(place, startTime, endTime, date, selectedType));
        if (resultRoom.isEmpty()) {
            errorMap.put("ReservationError", "예약가능한 Room이 없습니다.");
            log.error("예약 가능한 Room이 없습니다.");
        } else {
            Reservation reservation = new Reservation(user, resultRoom.get(), date, startTime, date, endTime.plusHours(1), true);
            Reservation save = reservationRepository.save(reservation);
            if (save == null) {
                errorMap.put("ReservationError", "예약 실패");
                result.put("errorMap", errorMap);
                log.error("예약에 실패하였습니다.");
                return result;
            }
            result.put("reservationId", save.getId().toString());
        }
        result.put("errorMap", errorMap);
        return result;
    }

    private Room getResultRoom(Place place, LocalTime startLocalTime, LocalTime endLocalTime, LocalDate date, String selectedType) {
        int startTime = startLocalTime.getHour();
        if (startTime == 0) {
            startTime = 24;
        }
        int endTime = endLocalTime.getHour();
        if (endTime == 0) {
            endTime = 24;
        }
        List<Room> reservedRoomList = roomRepository.findRoomByPlaceIdAndRoomType(place.getId(), selectedType);
        List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomTypeAndDate(place.getId(), selectedType, date);
        int maxTime = 24;
        if (place.getPlaceEnd().getHour() > place.getPlaceStart().getHour()) {
            maxTime = place.getPlaceEnd().getHour();
        }
        int maxWindowSize = maxTime - startTime;
        int window = endTime - startTime + 1;
        Map<Integer, ReservedRoom> reservedRoomMap = getReservedRoomMap(place, findReservationList, reservedRoomList);
        Long resultRoomId = -1L;
        boolean endFlag = true;

        while (window <= maxWindowSize && endFlag) {
            for (int i = 0; i < reservedRoomMap.size(); i++) {
                ReservedRoom findReservedRoom = reservedRoomMap.get(i);
                if (startTime == 24) {
                    findReservedRoom.getTimeStates().put(25, false);
                }
                if (startTime == endTime && window == 1) {
                    if (findReservedRoom.getTimeStates().get(startTime) && !findReservedRoom.getTimeStates().get(startTime + 1)) {
                        resultRoomId = findReservedRoom.getRoomId();
                        endFlag = false;
                    }
                } else {
                    int pCnt = 0;
                    for (int j = startTime; j < startTime + window; j++) {
                        if (!findReservedRoom.getTimeStates().get(j)) {
                            break;
                        } else {
                            pCnt++;
                            if (pCnt == window && findReservedRoom.getTimeStates().get(j) && !findReservedRoom.getTimeStates().get(j + 1)) {
                                resultRoomId = findReservedRoom.getRoomId();
                                endFlag = false;
                            }
                        }
                    }
                }
                if (!endFlag) {
                    break;
                }
            }
            window++;
        }

        if (resultRoomId == -1L) {
            log.info("예약가능한 시간이 없습니다. time range = {} - {}", startTime, endTime);
            return null;
        }
        return roomRepository.findById(resultRoomId);
    }

    /**
     * Room 단위로 모든 예약된 시간을 맵으로 저장 후 반환해주는 메서드 (Key: Order, Value: ReservedRoom)
     */
    private Map<Integer, ReservedRoom> getReservedRoomMap(Place findPlace, List<Reservation> findReservationList, List<Room> reservedRoomList) {
        Map<Integer, ReservedRoom> reservedRoomMap = new HashMap<>();
        for (int i = 0; i < reservedRoomList.size(); i++) {
            ReservedRoom reservedRoom = new ReservedRoom(reservedRoomList.get(i).getId());
            reservedRoom.setStartAndEndTimeMap(findPlace.getPlaceStart(), findPlace.getPlaceEnd());
            reservedRoomMap.put(i, reservedRoom);
        }

        for (int i = 0; i < reservedRoomMap.size(); i++) {
            ReservedRoom reservedRoom = reservedRoomMap.get(i);
            for (Reservation reservation : findReservationList) {
                if (reservedRoom.getRoomId().equals(reservation.getRoom().getId())) {
                    for (int time = reservation.getResStartTime().getHour(); time < reservation.getResEndTime().getHour(); time++) {
                        if (reservedRoom.getTimeState(time) && isOpenToday(findPlace, TimeFormatter.toLocalTime(String.valueOf(time)))) {
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
    private boolean isOpenToday(Place findPlace, LocalTime now) {
        return (findPlace.getPlaceStart().equals(now) || findPlace.getPlaceStart().isBefore(now)) && findPlace.getPlaceEnd().isAfter(now);
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
