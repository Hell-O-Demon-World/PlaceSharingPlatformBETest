package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SpringDataJpaReservationRepository extends JpaRepository<Reservation, Long> {

}
