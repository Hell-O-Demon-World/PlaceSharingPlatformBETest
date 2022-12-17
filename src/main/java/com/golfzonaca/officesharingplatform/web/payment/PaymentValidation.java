package com.golfzonaca.officesharingplatform.web.payment;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.type.FixStatus;
import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import com.golfzonaca.officesharingplatform.exception.InvalidResCancelRequest;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PaymentValidation {

    private final ReservationRepository reservationRepository;

    public void cancelRequest(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation.getStatus() != ReservationStatus.COMPLETED || reservation.getFixStatus() != FixStatus.UNFIXED) {
            throw new InvalidResCancelRequest("취소할 수 없는 예약입니다.");
        }
    }
}
