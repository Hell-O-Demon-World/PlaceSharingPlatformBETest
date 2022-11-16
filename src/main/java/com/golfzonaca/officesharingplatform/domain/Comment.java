package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Comment {

    private long id;
    private long placeId;
    private String commentText;
    private String commentWriter;
    private LocalDateTime commentTime;
}
