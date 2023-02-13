package com.golfzonaca.officesharingplatform.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "ROOMIMAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UPLOAD_FILENAME", nullable = false)
    private String uploadFileName;

    @Column(name = "STORE_FILENAME", nullable = false)
    private String storeFileName;

    @Column(name = "SAVED_PATH", nullable = false)
    private String savedPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOMKIND_ID")
    private RoomKind roomKind;

    public RoomImage(String uploadFileName, String storeFileName, String savedPath, Place place, RoomKind roomKind) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.savedPath = savedPath;
        this.place = place;
        this.roomKind = roomKind;
    }
}
