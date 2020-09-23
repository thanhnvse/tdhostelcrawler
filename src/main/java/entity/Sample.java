package main.java.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Sample implements Serializable {
    private int sampleHostelId;
    private int streetId;
    private double superficiality;
    private double price;
    private double longitude;
    private double latitude;
    private List<Integer> facilities;
    private List<Integer> services;


}
