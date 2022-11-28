package com.golfzonaca.officesharingplatform.service.payment.requestform;

import com.siot.IamportRestClient.Iamport;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.request.escrow.EscrowLogisData;
import com.siot.IamportRestClient.request.naver.*;
import com.siot.IamportRestClient.response.*;
import com.siot.IamportRestClient.response.escrow.EscrowLogisInvoice;
import com.siot.IamportRestClient.response.naver.NaverProductOrder;
import com.siot.IamportRestClient.response.naver.NaverReview;
import retrofit2.Call;

import java.util.List;

public class IamportAPIService implements Iamport {
    @Override
    public Call<IamportResponse<AccessToken>> token(AuthData auth) {
        return null;
    }

    @Override
    public Call<IamportResponse<PaymentBalance>> balance_by_imp_uid(String token, String imp_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<Payment>> payment_by_imp_uid(String token, String imp_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<PagedDataList<Payment>>> payments_by_status(String token, String payment_status) {
        return null;
    }

    @Override
    public Call<IamportResponse<Payment>> cancel_payment(String token, CancelData cancel_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<Prepare>> post_prepare(String token, PrepareData prepare_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<Prepare>> get_prepare(String token, String merchant_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<BillingCustomer>> delete_billing_customer(String token, String customer_uid, String reason, String extra) {
        return null;
    }

    @Override
    public Call<IamportResponse<BillingCustomer>> post_billing_customer(String token, String customer_uid, BillingCustomerData billing_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<Payment>> onetime_payment(String token, OnetimePaymentData onetime_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<Payment>> again_payment(String token, AgainPaymentData again_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<ScheduleList>> get_payment_schedule(String token, int schedule_from, int schedule_to, String schedule_status, int page, int limit) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<Schedule>>> schedule_subscription(String token, ScheduleData schedule_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<Schedule>>> unschedule_subscription(String token, UnscheduleData unschedule_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<BillingCustomer>> get_billing_customer(String token, String customer_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<Certification>> certification_by_imp_uid(String token, String imp_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<EscrowLogisInvoice>> post_escrow_logis(String token, String imp_uid, EscrowLogisData logis_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_product_orders(String token, String imp_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<NaverProductOrder>> naver_single_product_order(String token, String product_order_id) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverReview>>> naver_reviews(String token) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_cancel(String token, String imp_uid, NaverCancelData naver_cancel_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_ship(String token, String imp_uid, NaverShipData naver_ship_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_place(String token, String imp_uid, NaverPlaceData naver_place_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<EmptyResponse>> naver_confirm(String token, String imp_uid) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_request_return(String token, String imp_uid, NaverRequestReturnData naver_request_return_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_approve_return(String token, String imp_uid, NaverApproveReturnData naver_approve_return_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_reject_return(String token, String imp_uid, NaverRejectReturnData naver_reject_return_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_withhold_return(String token, String imp_uid, NaverWithholdReturnData naver_withhold_return_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<List<NaverProductOrder>>> naver_resolve_return(String token, String imp_uid, NaverResolveReturnData naver_resolve_return_data) {
        return null;
    }

    @Override
    public Call<IamportResponse<EmptyResponse>> naver_point(String token, String imp_uid) {
        return null;
    }
}
