package main.java.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
public class Utility implements Serializable {
    private int utilityId;

    private Double latitude;

    private Double longitude;

    private String name;

    private int typeId;

    public Utility() {
    }

    public Utility(int utilityId, double latitude, double longitude, String name, int typeId) {
        this.utilityId = utilityId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.typeId = typeId;
    }
}
