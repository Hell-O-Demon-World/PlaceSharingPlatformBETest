package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageReservationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBatisMyPageReservationFormService implements MyPageReservationFormService {
    private final ReservationRepository reservationRepository;
    private final PlaceRepository placeRepository;
    private final RoomKindRepository roomKindRepository;

    @Override
    public List<MyPageReservationForm> getMyPageReservationListByUserId(long userId) {
        List<MyPageReservationForm> myPageReservationFormList = new ArrayList<>();
        List<Reservation> findReservationList = reservationRepository.findAllByUserId(userId);
        for (int i = 0; i < findReservationList.size(); i++) {
            Reservation findReservation = findReservationList.get(i);
            MyPageReservationForm myPageReservationForm = MyPageReservationForm.builder()
                    .resDate(findReservation.getResStartDate().toString() + "~" + findReservationList.get(i).getResEndDate().toString())
                    .placeName(placeRepository.findById(findReservation.getPlace().getId()).get().getPlaceName())
                    .roomKind(roomKindRepository.findById(findReservation.getRoom().getRoomKind().getId()).getRoomType())
                    .resTime(findReservation.getResStartTime().toString() + "~" + findReservation.getResEndTime().toString())
                    .build();
            myPageReservationFormList.add(myPageReservationForm);
        }
        return myPageReservationFormList;
    }
}
