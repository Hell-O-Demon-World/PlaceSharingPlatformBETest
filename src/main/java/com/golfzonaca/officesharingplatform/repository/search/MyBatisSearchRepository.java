package com.golfzonaca.officesharingplatform.repository.search;

import com.golfzonaca.officesharingplatform.repository.mybatis.SearchMapper;
import com.golfzonaca.officesharingplatform.web.search.form.SearchPlaceResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisSearchRepository implements SearchRepository {
    private final SearchMapper searchMapper;

    @Override
    public List<SearchPlaceResultData> findPlaceBySearchWord(String searchWord) {
        return searchMapper.findPlaceBySearchWord(searchWord);
    }
}
