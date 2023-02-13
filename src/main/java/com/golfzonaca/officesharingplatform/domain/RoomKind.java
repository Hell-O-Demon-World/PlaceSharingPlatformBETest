package com.golfzonaca.officesharingplatform.domain;

import com.golfzonaca.officesharingplatform.domain.type.RoomType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "ROOM_KIND", uniqueConstraints = {@UniqueConstraint(name = "ROOM_KIND", columnNames = {"ROOM_TYPE"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RoomKind {

    @Id
    private Long id;

    @Column(name = "ROOM_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(name = "PRICE", nullable = false)
    private int price;
}
