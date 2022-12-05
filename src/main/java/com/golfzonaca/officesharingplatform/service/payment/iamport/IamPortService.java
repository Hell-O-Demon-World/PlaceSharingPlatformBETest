package com.golfzonaca.officesharingplatform.service.payment.iamport;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import java.io.IOException;

public interface IamPortService {

    IamportResponse<Payment> nicePay(CardInfo cardInfo) throws IamportResponseException, IOException;

    IamportResponse<Payment> nicePayCancel() throws IamportResponseException, IOException;
}
