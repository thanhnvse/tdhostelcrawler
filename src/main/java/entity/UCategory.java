package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UCategory implements Serializable {
    private int categoryId;

    private String name;

    private String code;

    public UCategory(int categoryId, String name, String code) {
        this.categoryId = categoryId;
        this.name = name;
        this.code = code;
    }
}
