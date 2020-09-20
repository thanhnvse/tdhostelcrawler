package main.java.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class District implements Serializable {
    private int districtId;
    private String districtName;
    private int provinceId;

    public District() {
    }

    public District(int districtId, String districtName, int provinceId) {
        this.districtId = districtId;
        this.districtName = districtName;
        this.provinceId = provinceId;
    }
}
