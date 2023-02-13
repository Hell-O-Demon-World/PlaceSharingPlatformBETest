package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ROOM_KIND_ID")
    private RoomKind roomKind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID")
    private Place place;

    //양방향 매핑
    @OneToMany(mappedBy = "room")
    private List<Reservation> reservationList = new LinkedList<>();

    @OneToOne(mappedBy = "room")
    private RoomStatus roomStatus;

    @Builder
    public Room(RoomKind roomKind, Place place) {
        this.roomKind = roomKind;
        this.place = place;
    }
}
