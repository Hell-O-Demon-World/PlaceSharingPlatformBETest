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

    @ManyToOne
    @JoinColumn(name = "Place_ID")
    private Place place;

    @Column(name = "RATING_SCORE", nullable = false)
    private float ratingScore;

    @Column(name = "RATING_REVIEW", nullable = false)
    private String ratingReview;

    @ManyToOne
    @JoinColumn(name = "RATING_WRITER")
    private User ratingWriter;

    @Column(name = "RATING_TIME", nullable = false)
    private LocalDateTime ratingTime;

    @Builder
    public Rating(Place place, float ratingScore, String ratingReview, User ratingWriter, LocalDateTime ratingTime) {
        this.place = place;
        this.ratingScore = ratingScore;
        this.ratingReview = ratingReview;
        this.ratingWriter = ratingWriter;
        this.ratingTime = ratingTime;
    }

    public void UpdateRating(float ratingScore, String ratingReview) {
        this.ratingScore = ratingScore;
        this.ratingReview = ratingReview;
    }
}
