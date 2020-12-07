package main.java.process;

import main.java.dao.AreaDAO;
import main.java.dao.GGDAO;
import main.java.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusProcess {
    public JSONArray readJsonFile() {
        JSONArray routeList = new JSONArray();
        try {
            String fileName = "data.json";
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), "UTF8"));
            String str = in.readLine();
            JSONObject obj = new JSONObject(str);
            routeList = obj.getJSONArray("Routes");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routeList;
    }

    public Bus setBusFromFile(JSONArray stationList, int j, String stationName) {
        Bus bus = new Bus();
        double latitude = stationList.getJSONObject(j).getDouble("Lat");
        double longitude = stationList.getJSONObject(j).getDouble("Lng");
        bus.setStationName(stationName);
        bus.setLatitude(latitude);
        bus.setLongitude(longitude);
        return bus;
    }

    public BusStation setBusStationFromFile(JSONArray routeList, int i, List<Bus> busList) {
        BusStation busStation = new BusStation();
        String routeNo = routeList.getJSONObject(i).getString("RouteNo");
        busStation.setRouteNo(routeNo);
        busStation.setBusList(busList);
        return busStation;
    }

    public List<Station> getStationFinishList(List<Bus> checkStationList, List<BusStation> busStationList){
        List<Station> stationFinishList = new ArrayList<>();
        for (int k = 0; k < checkStationList.size(); k++) {
            String nameAndNo = "";
            String stationNameBefore = checkStationList.get(k).getStationName();
            nameAndNo = stationNameBefore;
            for (int m = 0; m < busStationList.size(); m++) {
                int size = busStationList.get(m).getBusList().size();
                for (int n = 0; n < size; n++) {
                    if (stationNameBefore.toLowerCase().equals(busStationList.get(m).getBusList().get(n).getStationName().toLowerCase())) {
                        nameAndNo = nameAndNo + "--" + busStationList.get(m).getRouteNo();
                    }
                }
            }
            Station station = new Station();
            station.setStationName(nameAndNo);
            station.setLatitude(checkStationList.get(k).getLatitude());
            station.setLongitude(checkStationList.get(k).getLongitude());
            station.setUTypeId(6);
            stationFinishList.add(station);
        }
        return  stationFinishList;
    }

    public List<Station> checkDuplicateRoute(List<Station> stationFinishList){
        for (int a = 0; a < stationFinishList.size(); a++) {
            String[] stationSplit = stationFinishList.get(a).getStationName().split("--");
            String stationNameComplete = stationSplit[0];
            List<String> routeNoList = new ArrayList<>();
            for (int b = 1; b < stationSplit.length; b++) {
                routeNoList.add(stationSplit[b]);
            }
            List<String> newList = routeNoList.stream().distinct().collect(Collectors.toList());
            for (int c = 0; c < newList.size(); c++) {
                stationNameComplete = stationNameComplete + " | " + newList.get(c);
            }
            stationFinishList.get(a).setStationName(stationNameComplete);
        }
        return  stationFinishList;
    }

    public void insertBus(List<Station> stationFinishList){
        GGDAO ggdao = new GGDAO();
        for (Station station : stationFinishList) {
            if (!ggdao.checkInsert(station.getLatitude(), station.getLongitude(), station.getStationName(), station.getUTypeId())) {
                Utility utility = new Utility();
                utility.setLatitude(station.getLatitude());
                utility.setLongitude(station.getLongitude());
                utility.setName(station.getStationName());
                utility.setTypeId(station.getUTypeId());
                ggdao.insertAUtility(utility);
            }
        }
    }

    public String getStationName(JSONArray stationList, int j) {
        return stationList.getJSONObject(j).getString("StationName").trim();
    }

    public JSONArray getStationList(JSONArray routeList, int i) {
        return routeList.getJSONObject(i).getJSONArray("Stations");
    }

    public boolean checkDuplicatedStationName(String stationName, List<Bus> checkStationList) {
        boolean check = false;
        if (checkStationList.size() == 0) {
            check = false;
        } else {
            for (int i = 0; i < checkStationList.size(); i++) {
                if (stationName.equals(checkStationList.get(i).getStationName())) {
                    check = true;
                }
            }
        }
        return check;
    }
}
