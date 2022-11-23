package com.golfzonaca.officesharingplatform.domain.payment.kakaopay;

import lombok.Data;

@Data
public class Amount {

    private Integer total, tax_free, vat, point, discount;
}

