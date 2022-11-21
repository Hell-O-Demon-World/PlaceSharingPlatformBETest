package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalForm;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReady;
import com.golfzonaca.officesharingplatform.domain.type.PayStatus;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MyBatisKakaoPayService implements KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";
    private KakaoPayReady kakaoPayReady;
    private KakaoPayApprovalForm kakaoPayApprovalForm;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public String kakaoPayReady(Long reservationId) {

        log.info("Started kakaoPayReady method");

        RestTemplate restTemplate = new RestTemplate();

        reservationId = 12L; // 아직 값을 받지 않아서 임시로 넣음
        Reservation reservation = null; // Reservation 에서 Param 으로 넘겨받을 예정
        if (reservationRepository.findById(reservationId).isPresent()) {
            reservation = reservationRepository.findById(reservationId).get();
        }
        User user = reservation.getUser(); // Reservation 에서 Param 으로 넘겨받을 예정
        RoomKind roomKind = reservation.getRoom().getRoomKind();
        String calculatePayPrice = calculatePayPrice(reservation, roomKind);

        //서버요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK a8e95d70e35d823f1171ddaa015b53c4");
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        //서버요청 바디
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", String.valueOf(reservation.getId()));
        params.add("partner_user_id", String.valueOf(user.getId()));
        params.add("item_name", roomKind.getRoomType());
        params.add("quantity", "1");
        params.add("total_amount", calculatePayPrice);
        params.add("tax_free_amount", taxFreeAmount(calculatePayPrice));
        params.add("approval_url", "http://localhost:8080/" + reservationId + "/kakaoPaySuccess");
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
    public KakaoPayApprovalForm kakaoPayInfo(Long reservationId, String pg_token) {
        log.info("Started kakaoPayInfo method");

        RestTemplate restTemplate = new RestTemplate();
        Reservation reservation = null; // Reservation 에서 Param 으로 넘겨받을 예정
        if (reservationRepository.findById(reservationId).isPresent()) {
            reservation = reservationRepository.findById(reservationId).get();
        }
        User user = reservation.getUser(); // Reservation 에서 Param 으로 넘겨받을 예정
        Room room = reservation.getRoom();
        RoomKind roomKind = reservation.getRoom().getRoomKind();
        String calculatePayPrice = calculatePayPrice(reservation, roomKind);


        // 서버로 요청할 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + "a8e95d70e35d823f1171ddaa015b53c4");
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        // 서버로 요청할 Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", kakaoPayReady.getTid());
        params.add("partner_order_id", String.valueOf(reservation.getId()));
        params.add("partner_user_id", String.valueOf(user.getId()));
        params.add("pg_token", pg_token);
        params.add("total_amount", calculatePayPrice);

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        try {
            kakaoPayApprovalForm = restTemplate.postForObject(new URI(HOST + "/v1/payment/approve"), body, KakaoPayApprovalForm.class);
            log.info("" + kakaoPayApprovalForm);

            savePaymentInfo(reservation, user, room, kakaoPayApprovalForm);
            return kakaoPayApprovalForm;

        } catch (RestClientException e) {
            log.error(e.toString());
        } catch (URISyntaxException e) {
            log.error(e.toString());
        }
        return null;
    }

    public String calculatePayPrice(Reservation reservation, RoomKind roomKind) {
        return String.valueOf((reservation.getResEndTime().getHour() - reservation.getResStartTime().getHour()) * roomKind.getPrice());
    }

    public String taxFreeAmount(String calculatePayPrice) {
        return String.valueOf((int) (Integer.parseInt(calculatePayPrice) * 0.9));
    }

    public void savePaymentInfo(Reservation reservation, User user, Room room, KakaoPayApprovalForm kakaoPayApprovalForm) {
        //TODO 1 : Payment 테이블에 roomId에 적절한 것 찾아야함
//        long roomId = Integer.parseInt(kakaoPayApprovalForm.getItem_code()); // roomId는 not null인데  null로 들어옴

        LocalDateTime localDateTime = kakaoPayApprovalForm.getApproved_at();
        LocalDate payDate = toLocalDate(localDateTime);
        LocalTime payTime = toLocalTime(localDateTime);
        long payPrice = kakaoPayApprovalForm.getAmount().getTotal();
        //TODO 2 : Payment 테이블에 payStatus 적절한 것 찾아야함
        //TODO 3 : Reservation 테이블에서 예외상황 생각해야함
//        String payStatus = kakaoPayApprovalForm.getPayment_method_type(); -> 막은이유 : DB에서 enum으로 선언되어있어서 enum타입에 맞는 애들이 들어가야함
        PayStatus payStatus = checkPayStatus(reservation); // 한 사람이 같은 방을 2번(9~10am , 1pm~2pm 이런식으로) 예약한 경우도 생각해야하나?
//        String payStatus = "선결제";
        long payMileage = kakaoPayApprovalForm.getAmount().getPoint();

        PayType payType = PayType.FULLPAYMENT;

        if (payStatus.equals(PayStatus.PREPAYMENT)) { // 선결제일 때
            accumulationMileage(user, payPrice);
        } else { // 현장결제일 때
            if (!kakaoPayApprovalForm.getItem_name().contains("OFFICE")) {
                payType = PayType.DEPOSIT;
                payPrice = calculateDeposit(payPrice);
            }
        }
        String payApiCode = kakaoPayApprovalForm.getTid();

        Payment payment = new Payment(user, room, payDate, payTime, payPrice, payStatus, payMileage, payType, payApiCode);

        paymentRepository.save(payment);
    }

    public PayStatus checkPayStatus(Reservation reservation) {
        PayStatus payStatus;

        if (reservation == null) {
            payStatus = PayStatus.PREPAYMENT;
        } else {
            payStatus = PayStatus.POSTPAYMENT;
        }
        return payStatus;
    }

    public void accumulationMileage(User user, long payPrice) {
        long addMileage = (long) (payPrice * 0.05);
        user.getMileage().addPoint(user.getMileage().getPoint(), addMileage);
    }

    public long calculateDeposit(long payPrice) {
        return (long) (payPrice * 0.2);
    }

    public LocalDate toLocalDate(LocalDateTime localDateTime) {
        return LocalDate.from(localDateTime);
    }

    public LocalTime toLocalTime(LocalDateTime localDateTime) {
        return LocalTime.from(localDateTime);
    }
}

