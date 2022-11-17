package com.golfzonaca.officesharingplatform.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String path;

    @Builder
    public Photo(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
    }
}
