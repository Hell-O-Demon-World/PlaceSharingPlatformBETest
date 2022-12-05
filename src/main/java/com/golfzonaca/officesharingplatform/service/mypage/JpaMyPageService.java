package com.golfzonaca.officesharingplatform.service.mypage;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.repository.place.PlaceRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationSearchCond;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageReservationForm;
import com.golfzonaca.officesharingplatform.web.mypage.form.MyPageUsageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class JpaMyPageService implements MyPageService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PlaceRepository placeRepository;
    private final RoomKindRepository roomKindRepository;

    @Override
    public MyPage createMyPageForm(Long userId) {
        User findUser = userRepository.findById(userId);
        Mileage mileage = findUser.getMileage();
        List<Reservation> findReservation = reservationRepository.findAllByUserId(userId);
        return MyPage.builder()
                .userName(findUser.getUsername())
                .joinDate(findUser.getJoinDate())
                .mileagePoint(mileage.getPoint())
                .totalReviewNumber(findReservation.size())
                .build();
    }

    @Override
    public void cancelByOrderAndUserId(Integer order, Long userId) {
        List<Reservation> reservationList = reservationRepository.findAllLimit(ReservationSearchCond.builder()
                .userId(userId)
                .build(), order + 1);
        reservationRepository.deleteById(reservationList.get(order).getId());
    }

    @Override
    public Map<Integer, MyPageUsageForm> getMyPageUsageForm(long userId) {
        Map<Integer, MyPageUsageForm> resultMap = new HashMap<>();
        List<Reservation> findReservationList = reservationRepository.findAllByUserId(userId);
        int cnt = 0;
        // TODO : Must be add 2 types of date for pay ( start date , end date )
        for (Reservation reservation: findReservationList) {
            boolean ratingStatus = Optional.ofNullable(reservation.getRating()).isEmpty();
            MyPageUsageForm form = MyPageUsageForm.builder()
                    .productType(reservation.getRoom().getRoomKind().getRoomType())
                    .companyName(reservation.getRoom().getPlace().getPlaceName())
                    .reservationStartDate(LocalDateTime.of(reservation.getResStartDate(), reservation.getResStartTime()))
                    .reservationEndDate(LocalDateTime.of(reservation.getResEndDate(), reservation.getResEndTime()))
                    .paymentStartDate(null)
                    .paymentEndDate(null)
                    .usageState(reservation.getStatus())
                    .isAvailableReview(ratingStatus)
                    .build();
            resultMap.put(cnt, form);
            cnt++;
        }
        return resultMap;
    }

    @Override
    public List<MyPageReservationForm> getMyPageReservationListByUserId(long userId) {
        List<Reservation> findReservationList = reservationRepository.findAll(ReservationSearchCond.builder()
                .userId(userId)
                .build());
        return getMyPageReservationFormList(findReservationList);
    }

    private List<MyPageReservationForm> getMyPageReservationFormList(List<Reservation> findReservationList) {
        List<MyPageReservationForm> myPageReservationFormList = new ArrayList<>();
        for (int i = 0; i < findReservationList.size(); i++) {
            Reservation findReservation = findReservationList.get(i);
            MyPageReservationForm myPageReservationForm = MyPageReservationForm.builder()
                    .resDate(findReservation.getResStartDate().toString() + "~" + findReservationList.get(i).getResEndDate().toString())
                    .placeName(placeRepository.findById(findReservation.getRoom().getPlace().getId()).getPlaceName())
                    .roomKind(roomKindRepository.findById(findReservation.getRoom().getRoomKind().getId()).getRoomType())
                    .resTime(findReservation.getResStartTime().toString() + "~" + findReservation.getResEndTime().toString())
                    .build();
            myPageReservationFormList.add(myPageReservationForm);
        }
        return myPageReservationFormList;
    }
}
