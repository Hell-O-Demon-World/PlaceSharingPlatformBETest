package com.golfzonaca.officesharingplatform.IamportRestClient.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagedDataList<T> {

	@SerializedName("total")
	int total;
	
	@SerializedName("previous")
	int previous;
	
	@SerializedName("next")
	int next;
	
	@SerializedName("list")
	List<T> list;
	
	public int getTotal() {
		return total;
	}
	
	public int getPrevious() {
		return previous;
	}
	
	public int getNext() {
		return next;
	}
	
	public List<T> getList() {
		return list;
	}
	
}
