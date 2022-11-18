package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ROOM_KIND_ID")
    private RoomKind roomKind;

    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;

    @Column(name = "TOTAL_NUM", nullable = false)
    private int totalNum;

    @Builder
    public Room(RoomKind roomKind, Place place, int totalNum) {
        this.roomKind = roomKind;
        this.place = place;
        this.totalNum = totalNum;
    }
}
