package com.golfzonaca.officesharingplatform.repository.reservation;

import com.golfzonaca.officesharingplatform.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface SpringDataJpaReservationRepository extends JpaRepository<Reservation, Long> {

}
