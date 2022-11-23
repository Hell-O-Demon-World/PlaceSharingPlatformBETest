package com.golfzonaca.officesharingplatform.domain.payment;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Builder
@Data
public class KakaoPayApprovalForm {

    private String aid, tid, cid, sid;
    private String partner_order_id, partner_user_id, payment_method_type;
    private Amount amount;
    private Card card_info;
    private String item_name, item_code, payload;
    private Integer quantity;
    private LocalDateTime created_at, approved_at;

    public KakaoPayApprovalForm toEntity(String host, HttpEntity<MultiValueMap<String, String>> body) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(new URI(host + "/v1/payment/approve"), body, KakaoPayApprovalForm.class);
    }
}
