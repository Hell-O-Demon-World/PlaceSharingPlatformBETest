package com.golfzonaca.officesharingplatform.repository.place;

import com.golfzonaca.officesharingplatform.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringJpaPlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);

    List<Place> findByPlaceNameLike(String searchWord);
}
