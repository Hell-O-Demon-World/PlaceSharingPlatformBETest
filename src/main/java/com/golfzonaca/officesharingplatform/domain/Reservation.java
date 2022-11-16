package com.golfzonaca.officesharingplatform.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;
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

}