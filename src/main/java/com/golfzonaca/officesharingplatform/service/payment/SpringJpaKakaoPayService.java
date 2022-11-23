package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.payment.CompanyId;
import com.golfzonaca.officesharingplatform.domain.payment.kakaopay.KakaoPayApprovalForm;
import com.golfzonaca.officesharingplatform.domain.payment.kakaopay.KakaoPayReady;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.service.payment.requestform.RequestBodyApproveConverter;
import com.golfzonaca.officesharingplatform.service.payment.requestform.RequestBodyReadyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringJpaKakaoPayService implements KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";
    private static final HttpHeaders httpheaders = new HttpHeaders();
    private KakaoPayReady kakaoPayReady;
    private KakaoPayApprovalForm kakaoPayApprovalForm;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final KakaoPayUtilityImpl kakaoPayUtility;

    @Override
    public String kakaoPayReady(long reservationId) {
        log.info("Started kakaoPayReady method");

        RestTemplate restTemplate = new RestTemplate();

        Reservation reservation = null;
        if (reservationRepository.findById(reservationId).isPresent()) {
            reservation = reservationRepository.findById(reservationId).get();
        }
        User user = reservation.getUser();
        RoomKind roomKind = reservation.getRoom().getRoomKind();
        String calculatePayPrice = kakaoPayUtility.calculatePayPrice(reservation, roomKind);

        //서버요청 헤더
        kakaoPayUtility.makeHttpHeader(httpheaders);

        //서버요청 바디
        RequestBodyReadyConverter requestBodyConverter = RequestBodyReadyConverter.builder()
                .cid(CompanyId.kakaoPayCid)
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getId()))
                .itemName(roomKind.getRoomType())
                .quantity("1")
                .totalAmount(calculatePayPrice)
                .taxFreeAmount(calculatePayPrice)
                .approvalUrl("http://localhost:8080/" + reservationId + "/kakaoPaySuccess")
                .cancelUrl("http://localhost:8080/kakaoPayCancel")
                .failUrl("http://localhost:8080/kakaoPaySuccessFail").build();

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyConverter), httpheaders);
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
    public KakaoPayApprovalForm kakaoPayInfo(long reservationId, String pg_token) {
        log.info("Started kakaoPayInfo method");

        RestTemplate restTemplate = new RestTemplate();
        Reservation reservation = null;
        if (reservationRepository.findById(reservationId).isPresent()) {
            reservation = reservationRepository.findById(reservationId).get();
        }

        User user = reservation.getUser();
        Room room = reservation.getRoom();
        RoomKind roomKind = reservation.getRoom().getRoomKind();
        String calculatePayPrice = kakaoPayUtility.calculatePayPrice(reservation, roomKind);

        // 서버로 요청할 Body
        RequestBodyApproveConverter requestBodyApproveConverter = RequestBodyApproveConverter.builder()
                .cid(CompanyId.kakaoPayCid)
                .tid(kakaoPayReady.getTid())
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getId()))
                .pgToken(pg_token)
                .totalAmount(calculatePayPrice)
                .build();

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyApproveConverter), httpheaders);

        try {
            kakaoPayApprovalForm = restTemplate.postForObject(new URI(HOST + "/v1/payment/approve"), body, KakaoPayApprovalForm.class);
            log.info("" + kakaoPayApprovalForm);

            kakaoPayUtility.savePaymentInfo(paymentRepository, reservation, user, room, kakaoPayApprovalForm);
            return kakaoPayApprovalForm;

        } catch (RestClientException e) {
            log.error(e.toString());
        } catch (URISyntaxException e) {
            log.error(e.toString());
        }
        return null;
    }
}

