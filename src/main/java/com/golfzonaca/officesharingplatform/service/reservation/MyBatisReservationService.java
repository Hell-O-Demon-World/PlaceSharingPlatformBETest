package com.golfzonaca.officesharingplatform.service.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.golfzonaca.officesharingplatform.config.auth.token.JwtManager;
import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.repository.company.CompanyRepository;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.room.RoomRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.reservation.form.ResRequestData;
import com.golfzonaca.officesharingplatform.web.reservation.form.SelectedDateTimeForm;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyBatisReservationService implements ReservationService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomKindRepository roomKindRepository;
    private final PlaceRepository placeRepository;

    private enum Weekdays {
        Mon(1), Tue(2), Wed(3), Thu(4), Fri(5), Sat(6), Sun(7);
        private final int description;

        Weekdays(int description) {
            this.description = description;
        }

        public int getDescription() {
            return description;
        }
    }

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

        Place findPlace = placeRepository.findById(placeId).get();

        LocalDate reservationDate = toLocalDate(selectedDateTimeForm.getYear().toString()
                , selectedDateTimeForm.getMonth().toString(), selectedDateTimeForm.getDay().toString());
        String reservationDayOfWeek = reservationDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        List<String> placeOpenList = Arrays.asList(findPlace.getOpenDays().split(", "));

        Map<Integer, Boolean> inputTimeMap = getDefaultTimeMap();
        if (isOpenToday(reservationDayOfWeek, placeOpenList)) {
            Long roomKindId = roomKindRepository.findIdByRoomType(selectedDateTimeForm.getSelectedType());
            List<Room> reservationRoomList = roomRepository.findRoomByPlaceIdAndRoomKindId(placeId, roomKindId);
            List<Reservation> findReservationList = reservationRepository.findAllByPlaceIdAndRoomKindIdAndDate(placeId, roomKindId, reservationDate);

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
        for (int i = 0; i < 24; i++) {
            timeMap.put(i, false);
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
    public Map<String, String> ResRequestValidation(long placeId, ResRequestData resRequestData) throws JsonProcessingException {
        Place place = placeRepository.findById(placeId).get();
        User user = userRepository.findById(JwtManager.getIdByToken(resRequestData.getAccessToken()));
        Room room = null;
        for (Room candidate : place.getRooms()) {
            if (candidate.getRoomKind().getRoomType().equals(resRequestData.getSelectedType())) {
                room = candidate;
                break;
            }
        }

        LocalDate resStartDate = toLocalDate(resRequestData.getYear(), resRequestData.getMonth(), resRequestData.getDay());
        LocalDate resEndDate = toLocalDate(resRequestData.getYear(), resRequestData.getMonth(), resRequestData.getDay());
        LocalTime resStartTime = toLocalTime(resRequestData.getStartTime());
        LocalTime resEndTime = toLocalTime(resRequestData.getEndTime());

        Reservation reservation = new Reservation(place, user, room, resStartDate, resStartTime, resEndDate, resEndTime);
        reservationRepository.save(reservation);
        return null;

        /*
        Map<String, String> errorMap = new LinkedHashMap<>();
        Long userId = JwtManager.getIdByToken(resRequestData.getAccessToken());
        System.out.println("userId = " + userId);

        Boolean registeredStatus = userRepository.validateUserByUserId(userId);
        System.out.println("registeredStatus = " + registeredStatus);

        if (registeredStatus) {
            errorMap.put("InvalidUserError", "등록되지 않은 회원입니다.");
            return errorMap;
        }

        Long roomTypeId = roomKindRepository.findIdByRoomType(resRequestData.getSelectedType());

        if (roomTypeId == -1) {
            errorMap.put("NonexistentRoomTypeError", "선택하신 타입은 존재하지 않습니다.");
            return errorMap;
        }

        int roomQuantity = roomRepository.countRoomQuantityByPlaceId(placeId, roomTypeId);

        int resCount = 0;

        LocalDate resStartDate = toLocalDate(resRequestData.getYear(), resRequestData.getMonth(), resRequestData.getDay());
        LocalDate resEndDate = toLocalDate(resRequestData.getYear(), resRequestData.getMonth(), resRequestData.getDay());
        LocalTime resStartTime = toLocalTime(resRequestData.getStartTime());
        LocalTime resEndTime = toLocalTime(resRequestData.getEndTime());
        String resStartDayOfWeek = resStartDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        String resEndDayOfWeek = resEndDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);


        if (!(placeRepository.findOpenDaysById(placeId).contains(resStartDayOfWeek)) || !(placeRepository.findOpenDaysById(placeId).contains(resEndDayOfWeek))) {
            errorMap.put("InvalidOpenDaysError", "선택하신 요일은 휴무일입니다.");
            return errorMap;
        }


        if (resStartTime.equals(resEndTime)) {
            errorMap.put("InvalidResTimeError", "최소 1시간 이상 예약 가능합니다.");
            return errorMap;
        }

        if (resStartDate.atTime(resStartTime).isBefore(LocalDateTime.now()) || resEndDate.atTime(resEndTime).isBefore(LocalDateTime.now())) {
            errorMap.put("PastDateTimeError", "예약일시가 현재보다 과거입니다.");
            return errorMap;
        }

        if (roomQuantity == 0) {
            errorMap.put("NotExistedRoomError", "선택하신 Place 에 준비되지 않은 공간입니다.");
            return errorMap;
        }

        List<Long> findRoomIdList = roomRepository.findRoomIdByPlaceIdAndRoomTypeId(placeId, roomTypeId);

        List<Reservation> findResList = findResByPlaceIdAndRoomKindId(placeId, roomTypeId, resStartDate, resEndDate);
        

        if (findResList.size() == 0) {
            reservationRepository.save(new Reservation(placeRepository.findById(placeId), userRepository.findById(userId), roo))
            Place place = new Place();
            place.setId(placeId);
            User user = new User();
            user.setId(userId);
            RoomKind roomKind = new RoomKind();
            roomKind.setId(roomTypeId);
            Room room = new Room();
            room.setId(findRoomIdList.get(0));
            room.setRoomKind(roomKind);
            Reservation reservation = Reservation.builder()
                    .place(place)
                    .user(user)
                    .room(room)
                    .resStartDate(resStartDate)
                    .resStartTime(resStartTime)
                    .resEndDate(resEndDate)
                    .resEndTime(resEndTime)
                    .build();
            reservationRepository.save(reservation);
            return errorMap;
        }

        for (Reservation reservation : findResList) {
            if ((reservation.getResStartTime().isBefore(resStartTime) || reservation.getResStartTime().equals(resStartTime)) && reservation.getResEndTime().isAfter(resStartTime)) {
                if (reservation.getUser().getId() != userId) {
                    resCount++;
                    findRoomIdList.remove(reservation.getRoom().getId());
                } else {
                    errorMap.put("DuplicatedResForUserError", "선택하신 시간과 공간에 대한 예약 내역이 존재합니다.");
                    return errorMap;
                }
            } else if (resStartTime.isBefore(reservation.getResStartTime()) && resEndTime.isAfter(reservation.getResStartTime())) {
                if (reservation.getUser().getId() != userId) {
                    resCount++;
                    findRoomIdList.remove(reservation.getRoom().getId());
                } else {
                    errorMap.put("DuplicatedResForUserError", "선택하신 시간과 공간에 대한 예약 내역이 존재합니다.");
                    return errorMap;
                }
            }
        }
        if (roomQuantity == resCount) {
            errorMap.put("DuplicatedResForRoomError", "해당 Place 에 선택하신 타입의 이용가능한 사무공간이 없습니다.");
            return errorMap;
        }
        System.out.println("roomTypeId = " + roomTypeId);
        Place place = Place.builder()
                .id(placeId)
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        RoomKind roomKind = RoomKind.builder()
                .id(roomTypeId)
                .build();
        Room room = Room.builder()
                .id(findRoomIdList.get(0))
                .roomKind(roomKind)
                .build();
        Reservation reservation = Reservation.builder()
                .place(place)
                .user(user)
                .room(room)
                .resStartDate(resStartDate)
                .resStartTime(resStartTime)
                .resEndDate(resEndDate)
                .resEndTime(resEndTime)
                .build();
        reservationRepository.save(reservation);
        return errorMap;
*/
    }

    private boolean isOpenToday(String reservationDayOfWeek, List<String> placeOpenList) {
        Map<String, Boolean> plcaeOpenMap = getOpenDayMap(placeOpenList);
        return plcaeOpenMap.get(reservationDayOfWeek);
    }

    public LocalDate toLocalDate(String year, String month, String day) {
        String StringType = String.format("%s, %s, %s", year, month, day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu, M, d");
        LocalDate resDate = LocalDate.parse(StringType, formatter);
        return resDate;
    }

    public LocalTime toLocalTime(String hour) {
        String StringType = String.format("%s, %s, %s", hour, "0", "0");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H, m, s");
        return LocalTime.parse(StringType, formatter);
    }
}
