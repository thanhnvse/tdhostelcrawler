package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Ward implements Serializable {
    private int wardId;
    private String wardName;
    private int districtId;

    public Ward() {
    }

    public Ward(int wardId, String wardName, int districtId) {
        this.wardId = wardId;
        this.wardName = wardName;
        this.districtId = districtId;
    }
}
