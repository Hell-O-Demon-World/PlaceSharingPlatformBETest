package com.golfzonaca.officesharingplatform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ROOM_KIND")
@NoArgsConstructor
public class RoomKind {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ROOM_TYPE", nullable = false)
    private String roomType;
    @Column(name = "PRICE", nullable = false)
    private Integer price;
}
