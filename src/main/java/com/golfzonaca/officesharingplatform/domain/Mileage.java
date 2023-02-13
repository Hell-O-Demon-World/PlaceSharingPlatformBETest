package com.golfzonaca.officesharingplatform.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mileage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "POINT", nullable = false, length = 11)
    private long point;
    @Column(name = "LATEST_UPDATE_DATE", nullable = false)
    private LocalDateTime latestUpdateDate;
    //양방향 매핑
    @OneToMany(mappedBy = "mileage")
    private List<MileageUpdate> mileageUpdateList = new LinkedList<>();

    public Mileage(long point) {
        this.point = point;
    }

    public long addPoint(long addPoint) {
        this.point = this.point + addPoint;
        return point;
    }

    public long minusPoint(long minusPoint) {
        this.point = this.point - minusPoint;
        return point;
    }
}
