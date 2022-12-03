package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Mileage;
import com.golfzonaca.officesharingplatform.domain.Reservation;

public interface KakaoPayUtility {

    /*
     *   1. totalAmount 계산
     *   2. taxFreeAmount 계산
     * */

    Integer calculateTotalAmount(Reservation reservation, String payWay, String payType);

    Integer calculateTaxFreeAmount(Integer totalAmount);

    Integer calculateVatAmount(Integer totalAmount);

    long calculateMileage(Integer totalAmount);
}
