package com.golfzonaca.officesharingplatform.IamportRestClient.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class PaymentBalance {

	@SerializedName("amount")
	BigDecimal amount;
	
	@SerializedName("cash_receipt")
    Balance cash_receipt;
	
	@SerializedName("primary")
    Balance primary;
	
	@SerializedName("secondary")
    Balance secondary;
	
	@SerializedName("discount")
    Balance discount;
	
	@SerializedName("histories")
	List<PaymentBalanceEntry> histories;

	public BigDecimal getAmount() {
		return amount;
	}

	public List<PaymentBalanceEntry> getHistories() {
		return histories;
	}
	
	public Balance getCashReceipt() {
		return cash_receipt;
	}

	public Balance getPrimary() {
		return primary;
	}

	public Balance getSecondary() {
		return secondary;
	}

	public Balance getDiscount() {
		return discount;
	}

}
