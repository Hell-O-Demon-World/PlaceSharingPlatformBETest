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

    public static RoomKind desk() {
        return new RoomKind(1L, RoomType.DESK, 10000);
    }

    public static RoomKind meetingroom4() {
        return new RoomKind(2L, RoomType.MEETINGROOM4, 20000);
    }

    public static RoomKind meetingroom6() {
        return new RoomKind(3L, RoomType.MEETINGROOM6, 30000);
    }

    public static RoomKind meetingroom10() {
        return new RoomKind(4L, RoomType.MEETINGROOM10, 50000);
    }

    public static RoomKind meetingroom20() {
        return new RoomKind(5L, RoomType.MEETINGROOM20, 100000);
    }

    public static RoomKind office20() {
        return new RoomKind(6L, RoomType.OFFICE20, 100000);
    }

    public static RoomKind office40() {
        return new RoomKind(7L, RoomType.OFFICE40, 200000);
    }

    public static RoomKind office70() {
        return new RoomKind(8L, RoomType.OFFICE70, 300000);
    }

    public static RoomKind office100() {
        return new RoomKind(9L, RoomType.OFFICE100, 500000);
    }
}
