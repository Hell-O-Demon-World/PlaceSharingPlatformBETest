package com.golfzonaca.officesharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.Room;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalResponse;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReadyRequest;
import com.golfzonaca.officesharingplatform.domain.type.PayWay;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

interface KakaoPayUtility {

    void accumulationMileage(User user, long payPrice);

    long calculateDeposit(long payPrice);

    LocalDate toLocalDate(LocalDateTime localDateTime);

    LocalTime toLocalTime(LocalDateTime localDateTime);

    String calculatePayPrice(Reservation reservation, RoomKind roomKind);

    String taxFreeAmount(String calculatePayPrice);

    String vatAmount(String calculatePayPrice);

    void savePaymentInfo(PaymentRepository paymentRepository, Reservation reservation, User user, Room room, KakaoPayApprovalResponse kakaoPayApprovalResponse);

    PayWay checkPayStatus(Reservation reservation);

    MultiValueMap<String, String> multiValueMapConverter(ObjectMapper objectMapper, Object dto);

    HttpHeaders makeHttpHeader(HttpHeaders httpHeaders);

    KakaoPayApprovalResponse toEntity(String host, HttpEntity<MultiValueMap<String, String>> body);

    String kakaoPayReadyToEntity(String host, HttpEntity<MultiValueMap<String, String>> body);

    KakaoPayReadyRequest kakaoPayReadyRequestApprove(String host, HttpEntity<MultiValueMap<String, String>> body);

}
