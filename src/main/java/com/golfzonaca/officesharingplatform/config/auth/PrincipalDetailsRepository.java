package com.golfzonaca.officesharingplatform.config.auth;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PrincipalDetailsRepository {
    private static final ConcurrentHashMap<Long, PrincipalDetails> store = new ConcurrentHashMap<>();

    public PrincipalDetails save(Long id, PrincipalDetails principalDetails) {

        store.put(id, principalDetails);

        return principalDetails;
    }
}
