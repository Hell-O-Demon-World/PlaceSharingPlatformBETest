package com.golfzonaca.officesharingplatform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MILEAGE_EXPIRED_HISTORY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MileageExpiredHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "TRANSACTION_USAGE_HISTORY_ID")
    private MileageTransactionUsage mileageTransactionUsage;
    @ManyToOne
    @JoinColumn(name = "MILEAGE_UPDATE_ID")
    private MileageUpdate mileageUpdate;
    @Column(name = "CURRENT_POINT")
    private Long currentPoint;
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;
    @Builder
    public MileageExpiredHistory(MileageTransactionUsage mileageTransactionUsage, MileageUpdate mileageUpdate, Long currentPoint, LocalDateTime updateDate) {
        this.mileageTransactionUsage = mileageTransactionUsage;
        this.mileageUpdate = mileageUpdate;
        this.currentPoint = currentPoint;
        this.updateDate = updateDate;
    }

    public void updateCurrendDate(LocalDateTime currentDateTime) {
        this.updateDate = currentDateTime;
    }

    public void updateCurrentPoint(long usedPoint) {
        this.currentPoint += usedPoint;
    }
}
