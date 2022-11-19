package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.web.payment.form.KakaoPayApprovalForm;
import com.golfzonaca.officesharingplatform.web.payment.form.KakaoPayReady;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyBatisKakaoPayService implements KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";
    private KakaoPayReady kakaoPayReady;
    private KakaoPayApprovalForm kakaoPayApprovalForm;
    private final ReservationRepository reservationRepository;
    private final RoomKindRepository roomKindRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public String kakaoPayReady() {
        log.info("나 카카오 페이 준비 중 (서비스)~~~~~~~~~~~");

        RestTemplate restTemplate = new RestTemplate();

        Reservation reservation = reservationRepository.findById(3L);
        RoomKind roomKind = roomKindRepository.findById(reservation.getRoomId());
        String calculatePayPrice = calculatePayPrice(reservation, roomKind);

        //서버요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK a8e95d70e35d823f1171ddaa015b53c4");
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        //서버요청 바디
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", String.valueOf(reservation.getId())); // place id
        params.add("partner_user_id", String.valueOf(reservation.getUserId())); // user id
        params.add("item_name", roomKind.getRoomType()); // room kind  room type
        params.add("quantity", "1"); // payment payprice에서 역으로 계산
        params.add("total_amount", calculatePayPrice); // payment payprice
        params.add("tax_free_amount", taxFreeAmount(calculatePayPrice)); // payment payprice*90%
        params.add("approval_url", "http://localhost:8080/kakaoPaySuccess");
        params.add("cancel_url", "http://localhost:8080/kakaoPayCancel");
        params.add("fail_url", "http://localhost:8080/kakaoPaySuccessFail");

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);
        try {
            kakaoPayReady = restTemplate.postForObject(new URI(HOST + "/v1/payment/ready"), body, KakaoPayReady.class);
            return kakaoPayReady.getNext_redirect_pc_url();
        } catch (RestClientException e) {
            log.error(e.toString());
        } catch (URISyntaxException e) {
            log.error(e.toString());
        }

        return "/pay";
    }

    @Override
    public KakaoPayApprovalForm kakaoPayInfo(String pg_token) {

        log.info("KakaoPayInfoVO............................................");
        log.info("-----------------------------");

        RestTemplate restTemplate = new RestTemplate();

        Reservation reservation = reservationRepository.findById(3L);
        RoomKind roomKind = roomKindRepository.findById(reservation.getRoomId());
        String calculatePayPrice = calculatePayPrice(reservation, roomKind);

        // 서버로 요청할 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + "a8e95d70e35d823f1171ddaa015b53c4");
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        // 서버로 요청할 Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", kakaoPayReady.getTid());
        params.add("partner_order_id", String.valueOf(reservation.getId()));
        params.add("partner_user_id", String.valueOf(reservation.getUserId()));
        params.add("pg_token", pg_token);
        params.add("total_amount", calculatePayPrice);

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        try {
            kakaoPayApprovalForm = restTemplate.postForObject(new URI(HOST + "/v1/payment/approve"), body, KakaoPayApprovalForm.class);
            log.info("" + kakaoPayApprovalForm);

            savePaymentInfo(kakaoPayApprovalForm);
            return kakaoPayApprovalForm;

        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public String calculatePayPrice(Reservation reservation, RoomKind roomKind) {
        return String.valueOf((reservation.getResEndTime().getHour() - reservation.getResStartTime().getHour()) * roomKind.getPrice());
//        Duration.between(reservation.getResStartTime(), reservation.getResEndTime());
    }

    public String taxFreeAmount(String calculatePayPrice) {
        return String.valueOf((int) (Integer.parseInt(calculatePayPrice) * 0.9));
    }

    public void savePaymentInfo(KakaoPayApprovalForm kakaoPayApprovalForm) {
        long userId = Long.parseLong(kakaoPayApprovalForm.getPartner_user_id());
//        long roomId = Integer.parseInt(kakaoPayApprovalForm.getItem_code()); // roomId는 notnull이어야하는데 null로 들어옴
        long roomId = Integer.parseInt(kakaoPayApprovalForm.getPartner_user_id());
//        LocalDateTime payDate = kakaoPayApprovalForm.getApproved_at();
//        LocalDate payDateFormat = toLocalDate(trxDate);
        LocalDate now = LocalDate.now();
        long payPrice = kakaoPayApprovalForm.getAmount().getTotal();
//        String payStatus = kakaoPayApprovalForm.getPayment_method_type(); -> 막은이유 : DB에서 enum으로 선언되어있어서 enum타입에 맞는 애들이 들어가야함
        String payStatus = "선결제";
        long payMileage = kakaoPayApprovalForm.getAmount().getPoint();
//        String payType = kakaoPayApprovalForm.getPayment_method_type(); -> 마찬가지
        String payType = "보증금";
        String payApiCode = kakaoPayApprovalForm.getTid();

        Payment payment = new Payment(userId, roomId, now, payPrice, payStatus, payMileage, payType, payApiCode);

        paymentRepository.save(payment);
    }

    public LocalDate toLocalDate(LocalDateTime localDateTime) {
        String StringType = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu, M, d");
        LocalDate resDate = LocalDate.parse(StringType);
        return resDate;
    }

}

