package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@Entity
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;
    @Column(name = "COMMENT_TEXT", nullable = false, length = 40)
    private String text;
    @Column(name = "COMMENT_WRITER", nullable = false, length = 40)
    private String writer;
    @Column(name = "COMMENT_DATETIME", nullable = false)
    private LocalDateTime dateTime;
}
