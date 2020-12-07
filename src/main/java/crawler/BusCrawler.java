package main.java.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.dao.AreaDAO;
import main.java.dao.GGDAO;
import main.java.entity.*;
import main.java.process.BusProcess;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BusCrawler {
    private List<Bus> checkStationList = new ArrayList<>();
    private List<BusStation> busStationList = new ArrayList<>();
    private List<Station> stationFinishList = new ArrayList<>();

    public void getBusInfo() {
        BusProcess busProcess = new BusProcess();
        JSONArray routeList = busProcess.readJsonFile();
        for (int i = 0; i < routeList.length(); i++) {
            JSONArray stationList = busProcess.getStationList(routeList,i);
            List<Bus> busList = new ArrayList<>();
            for (int j = 0; j < stationList.length(); j++) {
                String stationName = busProcess.getStationName(stationList,j);
                Bus bus = busProcess.setBusFromFile(stationList, j,stationName);
                if (!busProcess.checkDuplicatedStationName(stationName,checkStationList)) {
                    checkStationList.add(bus);
                }
                busList.add(bus);
            }
            BusStation busStation = busProcess.setBusStationFromFile(routeList,i,busList);
            busStationList.add(busStation);
        }
        System.out.println("Size check : " + checkStationList.size());
        //Get list station
        //check duplicate route
        stationFinishList = busProcess.checkDuplicateRoute(busProcess.getStationFinishList(checkStationList,busStationList));
        //insert bus
        busProcess.insertBus(stationFinishList);
    }
}
