package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Service implements Serializable {
    private int id;
    private String name;
}
