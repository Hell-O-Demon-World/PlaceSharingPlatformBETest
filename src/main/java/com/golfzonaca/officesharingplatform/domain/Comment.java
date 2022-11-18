package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;

    @Column(name = "COMMENT_TEXT", nullable = false, length = 40)
    private String text;

    @Column(name = "COMMENT_WRITER", nullable = false, length = 40)
    private String writer;
    
    @Column(name = "COMMENT_DATETIME", nullable = false)
    private LocalDateTime dateTime;

    @Builder
    public Comment(Place place, String text, String writer, LocalDateTime dateTime) {
        this.place = place;
        this.text = text;
        this.writer = writer;
        this.dateTime = dateTime;
    }
}
