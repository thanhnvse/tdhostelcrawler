package main.java.entity;

import lombok.Data;

@Data
public class Station {
    private String stationName;
    private double latitude;
    private double longitude;
    private int uTypeId;
}
