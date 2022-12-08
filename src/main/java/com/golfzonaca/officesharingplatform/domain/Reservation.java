package com.golfzonaca.officesharingplatform.domain;


import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Column(name = "RES_COMPLETED", nullable = false)
    private LocalDateTime resCompleted;

    @Column(name = "RES_STARTDATE", nullable = false)
    private LocalDate resStartDate;

    @Column(name = "RES_STARTTIME", nullable = false)
    private LocalTime resStartTime;

    @Column(name = "RES_ENDDATE", nullable = false)
    private LocalDate resEndDate;

    @Column(name = "RES_ENDTIME", nullable = false)
    private LocalTime resEndTime;

    @Column(name = "RES_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @OneToOne(mappedBy = "reservation")
    private Rating rating;

    @OneToMany(mappedBy = "reservation")
    private List<Payment> paymentList = new LinkedList<>();

    public Reservation(User user, Room room, LocalDate resStartDate, LocalTime resStartTime, LocalDate resEndDate, LocalTime resEndTime, ReservationStatus status) {
        this.user = user;
        this.room = room;
        this.resStartDate = resStartDate;
        this.resStartTime = resStartTime;
        this.resEndDate = resEndDate;
        this.resEndTime = resEndTime;
        this.status = status;
    }

    public Reservation toEntity() {
        return Reservation.builder()
                .user(new User())
                .build();
    }

    public void upDateTime(LocalTime resStartTime, LocalTime resEndTime) {
        this.resStartTime = resStartTime;
        this.resEndTime = resEndTime;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}