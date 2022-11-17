package com.golfzonaca.officesharingplatform.service.search;

import com.golfzonaca.officesharingplatform.domain.Address;
import com.golfzonaca.officesharingplatform.repository.address.AddressRepository;
import com.golfzonaca.officesharingplatform.web.search.form.SearchPlaceResultData;
import com.golfzonaca.officesharingplatform.web.search.form.SearchRequestData;
import com.golfzonaca.officesharingplatform.web.search.form.SearchResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBatisSearchService implements SearchService {

    private final AddressRepository addressRepository;

    @Override
    public List<SearchResponseData> findRoomByWord(SearchRequestData searchRequestData) {
        List<SearchResponseData> responseDataList = new LinkedList<>();
        List<Long> addressIds = new ArrayList<>();
        List<SearchPlaceResultData> placeSearchList = searchRepository.findPlaceBySearchWord(searchRequestData.getSearchWord());

        for (SearchPlaceResultData resultData : placeSearchList) {
            addressIds.add(resultData.getAddressId());
        }

        List<Address> addressSearchList = addressRepository.findByAddressIds(addressIds);

        for (SearchPlaceResultData resultData : placeSearchList) {
            for (Address address : addressSearchList) {
                if (resultData.getAddressId() == address.getId()) {
                    SearchResponseData responseData = new SearchResponseData();
                    responseData.setKey(resultData.getId());
                    responseData.setName(resultData.getPlaceName());
                    responseData.setOption(resultData.getPlaceAddinfo());
                    responseData.setAddress(address.getAddress());
                    responseData.setPostcode(address.getPostalCode());
                    responseDataList.add(responseData);
                }
            }
        }
        return responseDataList;
    }
}
