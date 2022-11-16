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
    @Column(nullable = false)
    private long placeId;
    @Column(name = "comment_text", nullable = false, length = 40)
    private String text;
    @Column(name = "comment_writer", nullable = false, length = 40)
    private String writer;
    @Column(name = "comment_datetime", nullable = false)
    private LocalDateTime dateTime;
}
