package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Facility implements Serializable {
    private int id;
    private String name;
}
