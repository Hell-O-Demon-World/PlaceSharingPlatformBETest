package com.golfzonaca.officesharingplatform.web.mypage.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.golfzonaca.officesharingplatform.domain.type.ReservationStatus;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class MyPageUsageForm {
    private String productType;
    private String companyName;
    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;
    private LocalDateTime paymentStartDate;
    private LocalDateTime paymentEndDate;
    private ReservationStatus usageState;
    private boolean isAvailableReview;

}
