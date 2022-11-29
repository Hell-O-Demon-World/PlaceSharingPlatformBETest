package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.*;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApproval;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReady;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringJpaKakaoPayService implements KakaoPayService {

    private static final String HOST = "https://kapi.kakao.com/";
    private static final HttpHeaders httpheaders = new HttpHeaders();

    private KakaoPayReady kakaoPayReady = new KakaoPayReady();

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

        //서버요청 헤더
        kakaoPayUtility.makeHttpHeader(httpheaders);

        //서버요청 바디
        RequestBodyReadyConverter requestBodyReadyConverter = requestBodyReadyConverter(reservation, roomKind, "1", calculatePayPrice);
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyReadyConverter), httpheaders);

        kakaoPayReady = kakaoPayUtility.kakaoPayGetTid(HOST, body);
        return kakaoPayReady.getNext_redirect_pc_url();
    }

    public Reservation findReservation(long reservationId) {
        if (reservationRepository.findById(reservationId).isPresent()) {
            return reservationRepository.findById(reservationId).get();
        }
        return null;
    }

    public RequestBodyReadyConverter requestBodyReadyConverter(Reservation reservation,
                                                               RoomKind roomKind,
                                                               String quantity,
                                                               String calculatePayPrice) {
        return RequestBodyReadyConverter.builder()
                .cid(CompanyId.KAKAOPAYCID)
                .partnerOrderId(String.valueOf(reservation.getId()))
                .partnerUserId(String.valueOf(reservation.getId()))
                .itemName(roomKind.getRoomType())
                .quantity(quantity)
                .totalAmount(calculatePayPrice)
                .taxFreeAmount(calculatePayPrice)
                .approvalUrl("http://localhost:8080/" + reservation.getId() + "/kakaoPaySuccess")
                .cancelUrl("http://localhost:8080/kakaoPayCancel")
                .failUrl("http://localhost:8080/kakaoPaySuccessFail").build();
    }

    // input:    카카오페이가 승인해주고 난 뒤에 받은 정보
    // output : KakaoPayApprovalForm
    @Override
    public KakaoPayApproval kakaoPayInfo(long reservationId, String pg_token) {
        Reservation reservation = getReservation(reservationId);
        HttpEntity<MultiValueMap<String, String>> body = getBody(reservation, pg_token);
        try {
            KakaoPayApproval kakaoPayApproval = kakaoPayUtility.toEntity(HOST, body);
            log.info("" + kakaoPayApproval);
            User user = reservation.getUser();
            Room room = reservation.getRoom();
            kakaoPayUtility.savePaymentInfo(paymentRepository, reservation, user, room, kakaoPayApproval);
            return kakaoPayApproval;

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
    public KakaoPayApproval save(KakaoPayApproval KakaoPayApproval) {
        return KakaoPayApproval;
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntity(RequestBodyApproveConverter requestBodyApproveConverter) {
        return new HttpEntity<>(kakaoPayUtility.multiValueMapConverter(new ObjectMapper(), requestBodyApproveConverter), httpheaders);
    }

    private RequestBodyApproveConverter RequestBodyApproveConverter(String partnerOrderId, String partnerUserId, String pgToken, String totalAmount) {
        RequestBodyApproveConverter requestBodyApproveConverter = new RequestBodyApproveConverter();

        return requestBodyApproveConverter.toEntity(CompanyId.KAKAOPAYCID, kakaoPayReady.getTid()
                , partnerOrderId, partnerUserId, pgToken, totalAmount);
    }

    private Reservation getReservation(long reservationId) {
        return reservationRepository.findById(reservationId).get();
    }
}

