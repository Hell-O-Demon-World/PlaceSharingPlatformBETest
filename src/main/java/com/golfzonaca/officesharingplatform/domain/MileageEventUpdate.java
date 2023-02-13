package com.golfzonaca.officesharingplatform.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "MILEAGE_EVENT_UPDATE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MileageEventUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MILEAGE_UPDATE_ID")
    private MileageUpdate mileageUpdate;
    @Column(name = "UPDATE_POINT")
    private Long increasePoint;
    @Column(name = "INCREASE_REASON")
    private String increaseReason;
}
