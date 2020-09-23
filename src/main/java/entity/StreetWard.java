package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class StreetWard implements Serializable {
    private int streetWardId;
    private int wardId;
    private int streetId;

    public StreetWard() {
    }

    public StreetWard(int wardId, int streetId) {
        this.wardId = wardId;
        this.streetId = streetId;
    }
}
