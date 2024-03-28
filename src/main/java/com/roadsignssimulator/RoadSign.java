package com.roadsignssimulator;

import javax.persistence.*;

@Entity
public class RoadSign {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;

    @Lob
    private byte[] image;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public byte[] getImage() {
        return image;
    }
}
