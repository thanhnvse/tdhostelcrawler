package main.java.entity;

import lombok.Data;

import java.util.List;

@Data
public class BusStation {
    private String routeNo;
    private List<Bus> busList;
}
