package com.golfzonaca.officesharingplatform.service.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.service.place.dto.response.PlaceDto;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JpaPlaceService implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public List<Place> findAllPlaces() {
        return placeRepository.findAllPlaces();
    }

    @Override
    public Place findById(long placeId) {
        return placeRepository.findById(placeId);
    }

    @Override
    public boolean isOpenDay(Long id, String day) {
        LocalDate date = TimeFormatter.toLocalDate(day);
        String dayOfTheWeek = TimeFormatter.toDayOfTheWeek(date);
        String strOpenDays = placeRepository.findOpenDayById(id);
        String[] split = strOpenDays.split(", ");
        for (String openDay : split) {
            if (openDay.equals(dayOfTheWeek)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOpenToday(Long id, String startTime) {
        LocalTime localStartTime = TimeFormatter.toLocalTime(startTime);
        Tuple startAndEndTime = placeRepository.findStartAndEndTimeById(id);
        LocalTime start = startAndEndTime.get(0, LocalTime.class);
        LocalTime end = startAndEndTime.get(1, LocalTime.class);
        if (Objects.requireNonNull(end).getHour() - Objects.requireNonNull(start).getHour() == 0) {
            return true;
        }
        return localStartTime.isAfter(Objects.requireNonNull(start))
                && localStartTime.isBefore(Objects.requireNonNull(end));
    }

    @Override
    public boolean selectedDateValidation(String startDate, String endDate) {
        LocalDate startLocalDate = TimeFormatter.toLocalDate(startDate);
        LocalDate endLocalDate = TimeFormatter.toLocalDate(endDate);

        return startLocalDate.isBefore(endLocalDate.plusDays(1));
    }
    
    public Map<Integer, PlaceDto> processingMainPlaceData(List<Place> places) {
        Map<Integer, PlaceDto> mainPlaceData = new LinkedHashMap<>();
        for (int i = 0; i < places.size(); i++) {
            Place place = places.get(i);
            mainPlaceData.put(i, new PlaceDto(place.getId().toString(), place.getPlaceName(), String.valueOf(place.getRatePoint().getRatingPoint()), place.getAddress().getAddress(), stringToList(place.getPlaceAddInfo()), place.getDescription(), excludeOpenDays(stringToList(place.getOpenDays())), place.getPlaceStart().toString(), place.getPlaceEnd().toString(), processingRoomInfo(place.getRooms())));
        }
        return mainPlaceData;
    }

    private List<String> stringToList(String string) {
        return new ArrayList<>(Arrays.asList(string.split(", ")));
    }

    private List<String> excludeOpenDays(List<String> openDays) {
        List<String> daysOfWeek = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        for (String openDay : openDays) {
            daysOfWeek.remove(openDay);
        }
        return daysOfWeek;
    }

    private Map<String, String> processingRoomInfo(List<Room> rooms) {
        Map<String, String> roomInfo = new LinkedHashMap<>();
        calculateMinPriceForDesk(rooms, roomInfo);
        calculateMinPriceForMeetingRoom(rooms, roomInfo);
        calculateMinPriceForOffice(rooms, roomInfo);
        return roomInfo;
    }

    private void calculateMinPriceForDesk(List<Room> rooms, Map<String, String> roomInfo) {
        for (Room room : rooms) {
            if (room.getRoomKind().getRoomType().contains("DESK")) {
                if (roomInfo.containsKey("DESK")) {
                    if (room.getRoomKind().getPrice() < Integer.parseInt(roomInfo.get("DESK"))) {
                        roomInfo.put("DESK", String.valueOf(room.getRoomKind().getPrice()));
                    }
                } else {
                    roomInfo.put("DESK", String.valueOf(room.getRoomKind().getPrice()));
                }
            }
        }
    }

    private void calculateMinPriceForMeetingRoom(List<Room> rooms, Map<String, String> roomInfo) {
        for (Room room : rooms) {
            if (room.getRoomKind().getRoomType().contains("MEETINGROOM")) {
                if (roomInfo.containsKey("MEETINGROOM")) {
                    if (room.getRoomKind().getPrice() < Integer.parseInt(roomInfo.get("MEETINGROOM"))) {
                        roomInfo.put("MEETINGROOM", String.valueOf(room.getRoomKind().getPrice()));
                    }
                } else {
                    roomInfo.put("MEETINGROOM", String.valueOf(room.getRoomKind().getPrice()));
                }
            }
        }
    }

    private void calculateMinPriceForOffice(List<Room> rooms, Map<String, String> roomInfo) {
        for (Room room : rooms) {
            if (room.getRoomKind().getRoomType().contains("OFFICE")) {
                if (roomInfo.containsKey("OFFICE")) {
                    if (room.getRoomKind().getPrice() < Integer.parseInt(roomInfo.get("OFFICE"))) {
                        roomInfo.put("OFFICE", String.valueOf(room.getRoomKind().getPrice()));
                    }
                } else {
                    roomInfo.put("OFFICE", String.valueOf(room.getRoomKind().getPrice()));
                }
            }
        }
    }
}
