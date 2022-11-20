package com.golfzonaca.officesharingplatform.service.payment;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.domain.Reservation;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import com.golfzonaca.officesharingplatform.domain.User;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayApprovalForm;
import com.golfzonaca.officesharingplatform.domain.payment.KakaoPayReady;
import com.golfzonaca.officesharingplatform.domain.type.PayStatus;
import com.golfzonaca.officesharingplatform.domain.type.PayType;
import com.golfzonaca.officesharingplatform.repository.mileage.MileageRepository;
import com.golfzonaca.officesharingplatform.repository.mybatis.dto.MileageUpdateDto;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.golfzonaca.officesharingplatform.repository.roomkind.RoomKindRepository;
import com.golfzonaca.officesharingplatform.repository.user.UserRepository;
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
    private final RoomKindRepository roomKindRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;

    @Override
    public String kakaoPayReady() {

        log.info("Started kakaoPayReady method");

        RestTemplate restTemplate = new RestTemplate();

        Reservation reservation = reservationRepository.findById(12L);
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
        params.add("partner_order_id", String.valueOf(reservation.getId()));
        params.add("partner_user_id", String.valueOf(reservation.getUserId()));
        params.add("item_name", roomKind.getRoomType());
        params.add("quantity", "1");
        params.add("total_amount", calculatePayPrice);
        params.add("tax_free_amount", taxFreeAmount(calculatePayPrice));
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
        log.info("Started kakaoPayInfo method");

        RestTemplate restTemplate = new RestTemplate();

        Reservation reservation = reservationRepository.findById(12L);
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
            log.error(e.toString());
        } catch (URISyntaxException e) {
            log.error(e.toString());
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
//        long roomId = Integer.parseInt(kakaoPayApprovalForm.getItem_code()); // roomId는 not null인데  null로 들어옴
        long roomId = Integer.parseInt(kakaoPayApprovalForm.getPartner_user_id());
        LocalDateTime localDateTime = kakaoPayApprovalForm.getApproved_at();
        LocalDate payDate = toLocalDate(localDateTime);
        LocalTime payTime = toLocalTime(localDateTime);
        long payPrice = kakaoPayApprovalForm.getAmount().getTotal();
//        String payStatus = kakaoPayApprovalForm.getPayment_method_type(); -> 막은이유 : DB에서 enum으로 선언되어있어서 enum타입에 맞는 애들이 들어가야함
        String payStatus = checkPayStatus(userId, roomId); // 한 사람이 같은 방을 2번(9~10am , 1pm~2pm 이런식으로) 예약한 경우도 생각해야하나?
//        String payStatus = "선결제";
        long payMileage = kakaoPayApprovalForm.getAmount().getPoint();

        String payType = String.valueOf(PayType.FullPayment.getDescription());
        if (payStatus.equals(PayStatus.PREPAYMENT.getDescription())) { // 선결제일 때
            // 마일리지 5퍼
            // 페이타입 고치기
            // String payType = kakaoPayApprovalForm.getPayment_method_type(); -> 막은이유 : DB에서 enum으로 선언되어있어서 enum타입에 맞는 애들이 들어가야함

            // 선결제이면 총 가격의 5% 적립
            // 마일리지 테이블을 없애고 , 유저 테이블에 포인트 필드를 넣는거는 어떨까?
            accumulationMileage(userId, payPrice);

        } else { // 현장결제일 때

            if (!kakaoPayApprovalForm.getItem_name().contains("OFFICE")) {
                // 페이타입 고치기
                payType = String.valueOf(PayType.Deposit.getDescription());
                payPrice = calculateDeposit(payPrice);
            }
        }
        String payApiCode = kakaoPayApprovalForm.getTid();

        Payment payment = new Payment(userId, roomId, payDate, payTime, payPrice, payStatus, payMileage, payType, payApiCode);
        log.info(payment.toString());

        paymentRepository.save(payment);
    }

    public String checkPayStatus(long userId, long roomId) {
        String payStatus;

        if (reservationRepository.findByUserIdAndRoomId(userId, roomId) == null) {
            payStatus = "선결제";
        } else {
            payStatus = "현장결제";
        }
        return payStatus;
    }

    public void accumulationMileage(long userId, long payPrice) {
        // 5프로 적립 쿼리 날리기

        int addMileage = (int) (payPrice * 0.05);

        User user = userRepository.findById(userId);
        int startMileage = mileageRepository.findById(user.getMileageId()).getPoint();

        int point = addMileage + startMileage;
        MileageUpdateDto mileageUpdateDto = new MileageUpdateDto(point);
        log.info(mileageUpdateDto.toString());

        mileageRepository.update(user.getMileageId(), mileageUpdateDto);

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

