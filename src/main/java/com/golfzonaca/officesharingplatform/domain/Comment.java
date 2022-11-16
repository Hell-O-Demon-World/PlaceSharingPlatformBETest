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
    @Column(name = "PLACE_ID", nullable = false)
    private long placeId;
    @Column(name = "COMMENT_TEXT", nullable = false, length = 40)
    private String text;
    @Column(name = "COMMENT_WRITER", nullable = false, length = 40)
    private String writer;
    @Column(name = "COMMENT_DATETIME", nullable = false)
    private LocalDateTime dateTime;
}
