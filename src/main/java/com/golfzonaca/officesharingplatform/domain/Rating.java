package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "RATING_SCORE", nullable = false)
    private Integer ratingScore;
    @Column(name = "RATING_REVIEW", nullable = false)
    private String ratingReview;
    @Column(name = "RATING_WRITER", nullable = false)
    private String ratingWriter;
    @Column(name = "RATING_TIME", nullable = false)
    private LocalDateTime ratingTime;
}
