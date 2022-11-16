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
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @Column(name = "INQUIRY_TITLE", length = 40, nullable = false)
    private String title;
    @Column(name = "INQUIRY_CONTENT", length = 400, nullable = false)
    private String content;
    @Column(name = "INQUIRY_STATUS", nullable = false)
    private boolean status;
    @Column(name = "INQUIRY_TIME", nullable = false)
    private LocalDateTime time;
}
