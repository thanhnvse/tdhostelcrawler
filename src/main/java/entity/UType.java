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

    private String name;

    private int categoryId;

    public UType() {
    }

    public UType(int typeId, String name, int categoryId) {
        this.typeId = typeId;
        this.name = name;
        this.categoryId = categoryId;
    }
}
