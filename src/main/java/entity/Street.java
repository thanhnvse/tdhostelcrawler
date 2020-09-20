package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Street implements Serializable {
    private int streetId;
    private String streetName;

    public Street() {
    }

    public Street(int streetId, String streetName) {
        this.streetId = streetId;
        this.streetName = streetName;
    }
}
