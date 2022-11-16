package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
    @OneToOne
    @JoinColumn(name = "RATEPOINT_ID")
    private RatePoint ratePoint;
    @Column(name = "PLACE_NAME", nullable = false, length = 30)
    private String placeName;
    @Column(name = "PLACE_DESCRIPTION", nullable = false, length = 50)
    private String description;
    @Column(name = "PLACE_OPENDAYS", nullable = false)
    private String openDays;
    @Column(name = "PLACE_START", nullable = false)
    private LocalTime placeStart;
    @Column(name = "PLACE_END", nullable = false)
    private LocalTime placeEnd;
    @Column(name = "PLACE_ADDINFO", nullable = false)
    private String placeAddinfo;
    @OneToOne
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;
}
