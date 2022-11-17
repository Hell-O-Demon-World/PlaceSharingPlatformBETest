package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "ROOM_KIND_ID")
    private RoomKind roomKind;
    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;
    @Column(name = "TOTAL_NUM", nullable = false)
    private Integer totalNum;

}
