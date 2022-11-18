package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RATING_SCORE", nullable = false)
    private float ratingScore;

    @Column(name = "RATING_REVIEW", nullable = false)
    private String ratingReview;

    @Column(name = "RATING_WRITER", nullable = false)
    private String ratingWriter;

    @Column(name = "RATING_TIME", nullable = false)
    private LocalDateTime ratingTime;

    @Builder
    public Rating(int ratingScore, String ratingReview, String ratingWriter, LocalDateTime ratingTime) {
        this.ratingScore = ratingScore;
        this.ratingReview = ratingReview;
        this.ratingWriter = ratingWriter;
        this.ratingTime = ratingTime;
    }
}
