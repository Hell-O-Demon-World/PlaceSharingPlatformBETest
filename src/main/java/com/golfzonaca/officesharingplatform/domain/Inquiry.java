package com.golfzonaca.officesharingplatform.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long userId;
    @Column(name = "inquiry_title", length = 40, nullable = false)
    private String title;
    @Column(name = "inquiry_content", length = 400, nullable = false)
    private String content;
    @Column(name = "inquiry_status", nullable = false)
    private boolean status;
    @Column(name = "inquiry_time", nullable = false)
    private LocalDateTime time;
}
