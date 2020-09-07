package main.java.entity;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.criteria.Fetch;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
public class UType implements Serializable {
    private int typeId;

    private int displayOrder;

    private String name;

    private int categoryId;

    public UType() {
    }

    public UType(int displayOrder, String name, int categoryId) {
        this.displayOrder = displayOrder;
        this.name = name;
        this.categoryId = categoryId;
    }

    public UType(int typeId, int displayOrder, String name, int categoryId) {
        this.typeId = typeId;
        this.displayOrder = displayOrder;
        this.name = name;
        this.categoryId = categoryId;
    }
}
