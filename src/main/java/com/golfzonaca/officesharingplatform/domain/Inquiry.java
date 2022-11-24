package com.golfzonaca.officesharingplatform.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Entity
@NoArgsConstructor
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "INQUIRY_TITLE", length = 40, nullable = false)
    private String title;

    @Column(name = "INQUIRY_CONTENT", length = 400, nullable = false)
    private String content;

    @Column(name = "INQUIRY_STATUS", nullable = false)
    private boolean answerPresent;

    @Column(name = "INQUIRY_TIME", nullable = false)
    private LocalDateTime dateTime;

    @Builder
    public Inquiry(User user, String title, String content, boolean answerPresent, LocalDateTime dateTime) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.answerPresent = answerPresent;
        this.dateTime = dateTime;
    }

    public void UpdateInquiry(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
