package com.golfzonaca.officesharingplatform.IamportRestClient.serializer;

import com.golfzonaca.officesharingplatform.IamportRestClient.request.ScheduleEntry;
import com.golfzonaca.officesharingplatform.IamportRestClient.response.Schedule;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class ScheduleEntrySerializer implements JsonSerializer<ScheduleEntry>, JsonDeserializer<Schedule> {

	public JsonElement serialize(ScheduleEntry entry, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject jsonObject = new JsonObject();
	    jsonObject.addProperty("merchant_uid", entry.getMerchantUid());
	    jsonObject.addProperty("schedule_at", entry.getScheduleAt().getTime() / 1000L);
	    jsonObject.addProperty("amount", entry.getAmount());

	    return jsonObject;
	}

	public Schedule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		if ( json.isJsonObject() ) {
			JsonObject obj = (JsonObject)json;
			
			long unix_time = obj.get("schedule_at").getAsLong();
			Date schedule_dt = new Date( unix_time * 1000L );
			
			return new Schedule(obj.get("customer_uid").getAsString(), obj.get("merchant_uid").getAsString(), schedule_dt, obj.get("amount").getAsBigDecimal());
		}
		
		return null;
	}

}
