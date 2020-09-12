package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UCategory implements Serializable {
    private int categoryId;

    private int display_order;

    private String name;

    private String code;

    public UCategory(int categoryId,int display_order, String code, String name) {
        this.categoryId = categoryId;
        this.display_order = display_order;
        this.name = name;
        this.code = code;
    }
}
