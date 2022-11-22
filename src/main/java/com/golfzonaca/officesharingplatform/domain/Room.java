package com.golfzonaca.officesharingplatform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
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

    //양방향 매핑
    @OneToMany(mappedBy = "room")
    private List<Reservation> reservationList = new ArrayList<>();

    @Builder
    public Room(RoomKind roomKind, Place place, int totalNum) {
        this.roomKind = roomKind;
        this.place = place;
        this.totalNum = totalNum;
    }

    public Room(Long id) {
        this.id = id;
    }
}
