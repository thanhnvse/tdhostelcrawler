package main.java.dto;

import lombok.Data;

import java.util.List;

@Data
public class FaSeIdsDTO {
    private List<Integer> facilityInteger;
    private List<Integer> serviceInteger;
}
