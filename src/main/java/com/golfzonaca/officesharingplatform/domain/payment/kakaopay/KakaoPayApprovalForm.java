package com.golfzonaca.officesharingplatform.domain.payment.kakaopay;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KakaoPayApprovalForm {

    private String aid, tid, cid, sid;
    private String partner_order_id, partner_user_id, payment_method_type;
    private Amount amount;
    private Card card_info;
    private String item_name, item_code, payload;
    private Integer quantity;
    private LocalDateTime created_at, approved_at;
}
