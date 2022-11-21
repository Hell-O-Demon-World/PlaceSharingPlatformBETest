package com.golfzonaca.officesharingplatform.web.payment.form;

import lombok.Data;

@Data
public class Amount {

    private Integer total, tax_free, vat, point, discount;
}

