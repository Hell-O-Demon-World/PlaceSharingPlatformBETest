package com.golfzonaca.officesharingplatform.repository.mybatis;

import com.golfzonaca.officesharingplatform.web.search.form.SearchPlaceResultData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SearchMapper {
    List<SearchPlaceResultData> findPlaceBySearchWord(String searchWord);
}
