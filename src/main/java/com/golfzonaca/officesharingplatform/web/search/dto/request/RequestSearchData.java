package com.golfzonaca.officesharingplatform.web.search.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSearchData {
    @NotNull
    private String searchWord;
}
