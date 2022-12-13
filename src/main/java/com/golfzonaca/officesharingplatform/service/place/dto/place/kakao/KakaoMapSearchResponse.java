package com.golfzonaca.officesharingplatform.service.place.dto.place.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class KakaoMapSearchResponse {
    Map<Object, Object> response;
}


//Map<String,List<KakaoMapSearchPlaceInfo>>