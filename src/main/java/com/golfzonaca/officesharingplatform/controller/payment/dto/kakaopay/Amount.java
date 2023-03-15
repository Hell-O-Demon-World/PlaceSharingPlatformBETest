package com.golfzonaca.officesharingplatform.controller.payment.dto.kakaopay;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Amount {

    private Integer total;
    private Integer taxFree;
    private Integer vat;
    private Integer point;
    private Integer discount;
    private Integer greenDeposit;
}
