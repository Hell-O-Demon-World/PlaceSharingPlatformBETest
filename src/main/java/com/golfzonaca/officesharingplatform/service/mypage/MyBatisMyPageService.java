package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.MyPage;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationSearchCond;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageReservationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBatisMyPageService implements MyPageService{
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PlaceRepository placeRepository;
    private final RoomKindRepository roomKindRepository;
    @Override
    public MyPage createMyPageForm(Long userId) {
        return MyPage.builder()
                .userName(userRepository.findById(userId).get().getUsername())
                .myPageReservationList(getMyPageReservationListByUserId(userId))
                .build();
    }

    @Override
    public void cancelByOrderAndUserId(Integer order, Long userId) {
        List<Reservation> reservationList = reservationRepository.findAllLimit(ReservationSearchCond.builder()
                .userId(userId)
                .build(), order);
        reservationRepository.deleteById( reservationList.get(order).getId());
    }

    public List<MyPageReservationForm> getMyPageReservationListByUserId(long userId) {
        List<Reservation> findReservationList = reservationRepository.findAll(ReservationSearchCond.builder()
                .userId(userId)
                .build());
        return getMypageReservationFormList(findReservationList);
    }

    private List<MyPageReservationForm> getMypageReservationFormList(List<Reservation> findReservationList) {
        List<MyPageReservationForm> myPageReservationFormList = new ArrayList<>();
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
