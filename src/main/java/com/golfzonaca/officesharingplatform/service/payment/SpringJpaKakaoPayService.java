package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayCancelResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyRequest;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.service.payment.requestform.RequestBodyApproveConverter;
import com.golfzonaca.officesharingplatform.service.payment.requestform.RequestBodyCancelConverter;
import com.golfzonaca.officesharingplatform.service.payment.requestform.RequestBodyReadyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.*;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SpringJpaKakaoPayService implements KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";
    private static final HttpHeaders httpheaders = new HttpHeaders();

    private KakaoPayReadyRequest kakaoPayReadyRequest = new KakaoPayReadyRequest();

    private final KakaoPayUtility kakaoPayUtility;

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public String kakaoPayReady(long reservationId) {
        log.info("Started kakaoPayReady method");

        Reservation reservation = findReservation(reservationId);

        User user = reservation.getUser();
        RoomKind roomKind = reservation.getRoom().getRoomKind();
        String calculatePayPrice = kakaoPayUtility.calculatePayPrice(reservation, roomKind);
        String taxFreeAmount = kakaoPayUtility.taxFreeAmount(calculatePayPrice);
        String vatAmount = kakaoPayUtility.vatAmount(calculatePayPrice);

        //서버요청 헤더
        kakaoPayUtility.makeHttpHeader(httpheaders);

        //서버요청 바디
        RequestBodyReadyConverter requestBodyReadyConverter = requestBodyReadyConverter(reservation, roomKind, "1", calculatePayPrice, taxFreeAmount, vatAmount);
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyReadyConverter), httpheaders);

        kakaoPayReadyRequest = kakaoPayUtility.kakaoPayReadyRequestApprove(HOST, body);
        return kakaoPayReadyRequest.getNext_redirect_pc_url();
    }

    public Reservation findReservation(long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public RequestBodyReadyConverter requestBodyReadyConverter(Reservation reservation,
                                                               RoomKind roomKind,
                                                               String quantity,
                                                               String calculatePayPrice, String taxFreeAmount, String vatAmount) {
        return RequestBodyReadyConverter.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getId()))
                .itemName(roomKind.getRoomType())
                .quantity(quantity)
                .totalAmount(calculatePayPrice)
                .taxFreeAmount(taxFreeAmount)
                .vatAmount(vatAmount)
                .approvalUrl("http://localhost:8080/" + reservation.getId() + "/kakaoPaySuccess")
                .cancelUrl("http://localhost:8080/kakaoPayCancel")
                .failUrl("http://localhost:8080/kakaoPaySuccessFail").build();
    }

    // input:    카카오페이가 승인해주고 난 뒤에 받은 정보
    // output : KakaoPayApprovalForm
    @Override
    public KakaoPayApprovalResponse kakaoPayInfo(long reservationId, String pg_token) {
        Reservation reservation = getReservation(reservationId);
        HttpEntity<MultiValueMap<String, String>> body = getBody(reservation, pg_token);
        try {
            KakaoPayApprovalResponse kakaoPayApprovalResponse = kakaoPayUtility.toEntity(HOST, body);
            log.info("" + kakaoPayApprovalResponse);
            User user = reservation.getUser();
            Room room = reservation.getRoom();
            kakaoPayUtility.savePaymentInfo(paymentRepository, reservation, user, room, kakaoPayApprovalResponse);
            return kakaoPayApprovalResponse;

        } catch (RestClientException e) {
            log.error(e.toString());
        }
        return null;
    }


    private HttpEntity<MultiValueMap<String, String>> getBody(Reservation reservation, String pg_token) {
        RoomKind roomKind = reservation.getRoom().getRoomKind();
        String setPartnerOrderId = String.valueOf(reservation.getId());
        String partnerUserId = String.valueOf(reservation.getId());
        String calculatePayPrice = kakaoPayUtility.calculatePayPrice(reservation, roomKind);
        RequestBodyApproveConverter requestBodyApproveConverter = RequestBodyApproveConverter(setPartnerOrderId, partnerUserId, pg_token, calculatePayPrice);
        return getHttpEntity(requestBodyApproveConverter);
    }

    @Override
    public KakaoPayApprovalResponse save(KakaoPayApprovalResponse KakaoPayApprovalResponse) {
        return KakaoPayApprovalResponse;
    }

    @Override
    public KakaoPayCancelResponse cancel(long reservationId) {
//        kakaoPayUtility.makeHttpHeader(httpheaders);
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Payment findPayment = null;

        for (Payment payment : payments) {
            if (payment.getPayDate().equals(currentDate)) {
                if ((Duration.between(currentTime, payment.getPayTime())).getSeconds() < 3600) {
                    findPayment = payment;
                }
            }
        }

        RequestBodyCancelConverter requestBodyCancelConverter = RequestBodyCancelConverter.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .tid(findPayment.getApiCode())
                .cancelAmount((int) findPayment.getPrice())
                .cancelTaxFreeAmount((int) (findPayment.getPrice()))
                .build();
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyCancelConverter), httpheaders);

        try {
            KakaoPayCancelResponse kakaoPayCancelResponse = new RestTemplate().postForObject(new URI(HOST + "/v1/payment/cancel"), body, KakaoPayCancelResponse.class);

            for (Payment payment : paymentRepository.findByReservationId(reservationId)) {
                payment.updatePayStatus(false);
            }
            reservationRepository.findById(reservationId).updateStatus(false);

            return kakaoPayCancelResponse;
        } catch (RestClientException | URISyntaxException e) {
            log.error(e.toString());
        }
        return null; // return을 하면 메소드가 끝이기 때문.
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntity(RequestBodyApproveConverter requestBodyApproveConverter) {
        return new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyApproveConverter), httpheaders);
    }

    private RequestBodyApproveConverter RequestBodyApproveConverter(String partnerOrderId, String partnerUserId, String pgToken, String totalAmount) {
        RequestBodyApproveConverter requestBodyApproveConverter = new RequestBodyApproveConverter();

        return requestBodyApproveConverter.toEntity(CompanyId.KAKAOPAYCID, kakaoPayReadyRequest.getTid()
                , partnerOrderId, partnerUserId, pgToken, totalAmount);
    }

    private Reservation getReservation(long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}

