package com.golfzonaca.officesharingplatform.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(name = "RES_STARTDATE", nullable = false)
    private LocalDate resStartDate;

    @Column(name = "RES_STARTTIME", nullable = false)
    private LocalTime resStartTime;

    @Column(name = "RES_ENDDATE", nullable = false)
    private LocalDate resEndDate;

    @Column(name = "RES_ENDTIME", nullable = false)
    private LocalTime resEndTime;

    @OneToOne(mappedBy = "reservation")
    private Rating rating;

    public Reservation(User user, Room room, LocalDate resStartDate, LocalTime resStartTime, LocalDate resEndDate, LocalTime resEndTime) {
        this.user = user;
        this.room = room;
        this.resStartDate = resStartDate;
        this.resStartTime = resStartTime;
        this.resEndDate = resEndDate;
        this.resEndTime = resEndTime;
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
}