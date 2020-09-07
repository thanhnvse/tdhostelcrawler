package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Street implements Serializable {
    private int streetId;
    private int warđId;
    private String streetName;
}
