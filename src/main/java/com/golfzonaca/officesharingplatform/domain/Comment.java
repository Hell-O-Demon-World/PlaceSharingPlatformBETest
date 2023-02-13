package com.golfzonaca.officesharingplatform.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATING_ID")
    private Rating rating;

    @Column(name = "COMMENT_TEXT", nullable = false, length = 40)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User writer;

    @Column(name = "COMMENT_DATETIME", nullable = false)
    private LocalDateTime dateTime;

    @Builder
    public Comment(Rating rating, String text, User writer, LocalDateTime dateTime) {
        this.rating = rating;
        this.text = text;
        this.writer = writer;
        this.dateTime = dateTime;
    }

    public void updateComment(String text) {
        this.text = text;
    }
}
