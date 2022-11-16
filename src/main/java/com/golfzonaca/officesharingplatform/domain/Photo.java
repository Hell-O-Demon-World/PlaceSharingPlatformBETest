package com.golfzonaca.officesharingplatform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@ToString
@RequiredArgsConstructor
@Entity
@NoArgsConstructor
public class Photo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String path;
}
