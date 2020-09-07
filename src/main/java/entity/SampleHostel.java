package main.java.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SampleHostel implements Serializable {
    private int sampleHostelId;
    private String streetId;
    private double superficiality;
    private double price;
    private double longitude;
    private double latitude;
    private List<Integer> facilities;
    private List<Integer> services;
}
