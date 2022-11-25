package com.golfzonaca.officesharingplatform.IamportRestClient;

import com.golfzonaca.officesharingplatform.IamportRestClient.request.payco.OrderStatusData;
import com.golfzonaca.officesharingplatform.IamportRestClient.response.IamportResponse;
import com.golfzonaca.officesharingplatform.IamportRestClient.response.payco.OrderStatus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PaycoImpl {

	@POST("/payco/orders/status/{imp_uid}")
	Call<IamportResponse<OrderStatus>> updateStatus(
		@Header("Authorization") String token,
		@Path("imp_uid") String imp_uid,
		@Body OrderStatusData statusData
	);
	
}
