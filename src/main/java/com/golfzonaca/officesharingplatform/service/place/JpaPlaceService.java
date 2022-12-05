package com.golfzonaca.officesharingplatform.service.place;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.service.place.dto.PlaceDetailsInfo;
import com.golfzonaca.officesharingplatform.service.place.dto.PlaceListDto;
import com.golfzonaca.officesharingplatform.service.place.dto.response.RatingDto;
import com.golfzonaca.officesharingplatform.service.place.dto.response.RoomTypeResponse;
import com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype.Desk;
import com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype.MeetingRoom;
import com.golfzonaca.officesharingplatform.service.place.dto.response.roomtype.Office;
import com.golfzonaca.officesharingplatform.web.formatter.TimeFormatter;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JpaPlaceService implements PlaceService {
    private final PlaceRepository placeRepository;
    private final RoomKindRepository roomKindRepository;

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

    @Override
    public PlaceDetailsInfo getPlaceDetailsInfo(long placeId) {
        Place place = placeRepository.findById(placeId);
        List<String> imagesPath = getImagesPath(place);
        List<RatingDto> ratingList = getPlaceRating(place);

        return new PlaceDetailsInfo(
                place.getId().toString(),
                place.getPlaceName(),
                place.getAddress().getPostalCode(),
                place.getAddress().getAddress(),
                stringToList(place.getPlaceAddInfo()),
                imagesPath,
                String.valueOf(place.getRatePoint().getRatingPoint()),
                String.valueOf(ratingList.size()),
                getQuantityByRoomType(place, "DESK"),
                getQuantityByRoomType(place, "MEETINGROOM"),
                getQuantityByRoomType(place, "OFFICE"),
                place.getDescription(),
                excludeOpenDays(stringToList(place.getOpenDays())),
                place.getPlaceStart().toString(),
                place.getPlaceEnd().toString(),
                findRoom(placeId),
                ratingList
        );
    }

    private RoomTypeResponse findRoom(long placeId) {
        Place findPlace = placeRepository.findById(placeId);
        return getRoomTypeResponse(findPlace);
    }

    private RoomTypeResponse getRoomTypeResponse(Place place) {
        RoomTypeResponse roomTypeResponse = new RoomTypeResponse();
        Set<String> nonDuplicatedRoomSet = getNonDuplicatedRoomSet(place.getRooms());

        Desk resultDesk = new Desk();
        boolean deskExist = false;
        SortedSet<MeetingRoom> responseMeetingRoom = new TreeSet<>();
        SortedSet<Office> responseOffice = new TreeSet<>();
        List<String> images = new LinkedList<>();
        int price = 0;
        for (String roomType : nonDuplicatedRoomSet) {
            price = roomKindRepository.findByRoomType(roomType).getPrice();
            for (RoomImage roomImage : place.getRoomImages()) {
                if (roomImage.getRoomKind().getRoomType().equals(roomType)) {
                    images.add(roomImage.getSavedPath());
                }

            }
            if (roomType.equals("DESK")) {
                deskExist = true;
                resultDesk = new Desk(deskExist, price, images);
            } else {
                String roomKindTag = roomType.replaceAll("[^0-9]", "");
                if (roomType.contains("MEETINGROOM")) {
                    responseMeetingRoom.add(new MeetingRoom(roomKindTag, price, images));
                } else {
                    responseOffice.add(new Office(roomKindTag, price, images));
                }
            }
        }

        roomTypeResponse.toEntity(resultDesk, responseMeetingRoom, responseOffice);
        return roomTypeResponse;
    }

    private Set<String> getNonDuplicatedRoomSet(List<Room> roomList) {
        SortedSet<String> nonDuplicatedRoomSet = new TreeSet<>();
        for (Room room : roomList) {
            nonDuplicatedRoomSet.add(room.getRoomKind().getRoomType());
        }
        return nonDuplicatedRoomSet;
    }

    private String getQuantityByRoomType(Place place, String roomType) {
        int quantity = 0;
        for (Room room : place.getRooms()) {
            if (room.getRoomKind().getRoomType().contains(roomType)) {
                quantity++;
            }
        }
        return String.valueOf(quantity);
    }

    public Map<Integer, PlaceListDto> processingMainPlaceData(List<Place> places) {
        Map<Integer, PlaceListDto> mainPlaceData = new LinkedHashMap<>();
        for (int i = 0; i < places.size(); i++) {
            Place place = places.get(i);
            List<String> imagesPath = getImagesPath(place);
            mainPlaceData.put(i, new PlaceListDto(imagesPath, place.getId().toString(), place.getPlaceName(), String.valueOf(place.getRatePoint().getRatingPoint()), place.getAddress().getAddress(), stringToList(place.getPlaceAddInfo()), place.getDescription(), excludeOpenDays(stringToList(place.getOpenDays())), place.getPlaceStart().toString(), place.getPlaceEnd().toString(), processingRoomInfo(place.getRooms())));
        }
        return mainPlaceData;
    }

    private List<RatingDto> getPlaceRating(Place place) {
        List<RatingDto> ratingList = new LinkedList<>();
        for (Room room : place.getRooms()) {
            for (Reservation reservation : room.getReservationList()) {
                if (reservation.getRating() != null) {
                    List<String> comment = getComment(reservation.getRating());
                    ratingList.add(new RatingDto(String.valueOf(reservation.getRating().getId()), String.valueOf(reservation.getRating().getRatingScore()), reservation.getUser().getUsername(), reservation.getRating().getRatingTime().toString(), reservation.getRoom().getRoomKind().getRoomType(), reservation.getRating().getRatingReview(), comment));
                }
            }
        }
        return ratingList;
    }

    private List<String> getComment(Rating rating) {
        List<String> commentList = new LinkedList<>();
        for (Comment comment : rating.getCommentList()) {
            commentList.add(comment.getText());
        }
        return commentList;
    }


    @NotNull
    private List<String> getImagesPath(Place place) {
        List<String> imagesPath = new LinkedList<>();
        for (PlaceImage placeImage : place.getPlaceImages()) {
            imagesPath.add(placeImage.getSavedPath());
        }
        for (RoomImage roomImage : place.getRoomImages()) {
            imagesPath.add(roomImage.getSavedPath());
        }
        return imagesPath;
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
