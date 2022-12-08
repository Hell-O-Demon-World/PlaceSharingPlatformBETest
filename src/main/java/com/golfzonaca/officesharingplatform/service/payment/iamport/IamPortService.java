package com.golfzonaca.officesharingplatform.service.payment.iamport;

import com.golfzonaca.officesharingplatform.domain.Payment;
import com.golfzonaca.officesharingplatform.repository.mileage.MileageRepository;
import com.golfzonaca.officesharingplatform.repository.payment.PaymentRepository;
import com.golfzonaca.officesharingplatform.repository.reservation.ReservationRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamPortService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final MileageRepository mileageRepository;


    public Payment checkAvailablePaymentInHour(long reservationId) {
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

        return findPayment;
    }

    // todo : update 구문에 조건 더 필요 id + ...
    /*public void updatePaymentStatus(long reservationId, String tid) {

        for (Payment payment : paymentRepository.findByReservationId(reservationId)) {
            payment.updatePayStatus(false);
        }
        reservationRepository.findById(reservationId).updateStatus(false);
    }*/


    public IamportResponse<com.siot.IamportRestClient.response.Payment> nicePay(CardInfo cardInfo) throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient("3356213051155874", "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf");
        OnetimePaymentData onetimeData = new OnetimePaymentData("1q2w3e4r", new BigDecimal(1000), cardInfo);
//        onetimeData.setPg("jtnet");
        onetimeData.setPg("nice");
        return iamportClient.onetimePayment(onetimeData);
    }

    public IamportResponse<com.siot.IamportRestClient.response.Payment> nicePayCancel() throws IamportResponseException, IOException {
        IamportClient iamportClient = new IamportClient("3356213051155874", "c8AvU2odFqdwyfvFV7xcA880WWKm3CE8bah5mbR60DV3RN2DUpmXYjtd0mzbC5Y0ieMaRnB95EpXfvrf");
        CancelData cancelData = new CancelData("1q2w3e4r", false, new BigDecimal(1000));
        cancelData.setTax_free(new BigDecimal(0));
        cancelData.setChecksum(null);
        cancelData.setReason("test");

        return iamportClient.cancelPaymentByImpUid(cancelData);
    }
}
