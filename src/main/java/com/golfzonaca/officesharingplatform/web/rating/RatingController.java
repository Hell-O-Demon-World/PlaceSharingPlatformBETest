package com.golfzonaca.officesharingplatform.web.rating;

import com.golfzonaca.officesharingplatform.annotation.TokenUserId;
import com.golfzonaca.officesharingplatform.service.rating.RatingService;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingSaveData;
import com.golfzonaca.officesharingplatform.web.rating.dto.RatingUpdateData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{placeId}/rating/add")
    public String saveRating(@TokenUserId Long userId, @PathVariable long placeId, @Validated @RequestBody RatingSaveData ratingSaveData, BindingResult bindingResult) {
        ratingService.save(userId, placeId, ratingSaveData);
        return "ok";
    }

    @GetMapping("/{placeId}/rating/{ratingId}")
    public String ratingDetail(@PathVariable long placeId, @PathVariable long ratingId) {
        ratingService.findById(ratingId);
        return "ok";
    }

    @PostMapping("/{placeId}/rating/{ratingId}/edit")
    public String editRating(@TokenUserId Long userId, @PathVariable long placeId, @PathVariable long ratingId, @Validated @RequestBody RatingUpdateData updateData, BindingResult bindingResult) {
        ratingService.update(userId, ratingId, updateData);
        return "ok";
    }

    @GetMapping("/{placeId}/rating/{ratingId}/delete")
    public String deleteRating(@TokenUserId Long userId, @PathVariable long placeId, @PathVariable long ratingId, BindingResult bindingResult) {
        ratingService.delete(userId, ratingId);
        return "ok";
    }
}
