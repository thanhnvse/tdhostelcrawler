package main.java.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.dao.AreaDAO;
import main.java.entity.*;
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
        try {
            String fileName = "data.json";
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), "UTF8"));
            String str = in.readLine();
            JSONObject obj = new JSONObject(str);
            AreaDAO areaDAO = new AreaDAO();

            //Get all routes
            JSONArray routeList = obj.getJSONArray("Routes");
            for (int i = 0; i < routeList.length(); i++) {
                String routeNo = routeList.getJSONObject(i).getString("RouteNo");
                JSONArray stationList = routeList.getJSONObject(i).getJSONArray("Stations");
                List<Bus> busList = new ArrayList<>();
                for (int j = 0; j < stationList.length(); j++) {
                    String stationName = stationList.getJSONObject(j).getString("StationName").trim();
                    double latitude = stationList.getJSONObject(j).getDouble("Lat");
                    double longitude = stationList.getJSONObject(j).getDouble("Lng");
                    Bus bus = new Bus();
                    if (!checkDuplicatedStationName(stationName, latitude, longitude)) {
                        bus.setStationName(stationName);
                        bus.setLatitude(latitude);
                        bus.setLongitude(longitude);
                        checkStationList.add(bus);
                    }
                    bus.setStationName(stationName);
                    bus.setLatitude(latitude);
                    bus.setLongitude(longitude);
                    busList.add(bus);
                }
                BusStation busStation = new BusStation();
                busStation.setRouteNo(routeNo);
                busStation.setBusList(busList);
                busStationList.add(busStation);
            }
            System.out.println("Size check : " + checkStationList.size());
            // create writer
//            Writer writer = new FileWriter("StationDuplication.json");
//            // convert users list to JSON file
//            new Gson().toJson(checkStationList, writer);
//            // close writer
//            writer.close();
//            System.out.println(checkStationList);
//            System.out.println(busStationList);
            //Equality
            //Get list station

            for (int k = 0; k < checkStationList.size(); k++) {
                String nameAndNo = "";
                String stationNameBefore = checkStationList.get(k).getStationName();
                nameAndNo = stationNameBefore;
                double lat = checkStationList.get(k).getLatitude();
                double lng = checkStationList.get(k).getLongitude();
                //get list Bus station
                for (int m = 0; m < busStationList.size(); m++) {
                    int size = busStationList.get(m).getBusList().size();
                    for (int n = 0; n < size; n++) {
//                        if(stationNameBefore.equals(busStationList.get(m).getBusList().get(n).getStationName())
//                        && lat == busStationList.get(m).getBusList().get(n).getLatitude()
//                        && lng == busStationList.get(m).getBusList().get(n).getLongitude()){
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

            //check duplicate route
            for (int a = 0; a < stationFinishList.size(); a++) {
                String[] stationSplit = stationFinishList.get(a).getStationName().split("--");
                String stationNameComplete = stationSplit[0];
                List<String> routeNoList = new ArrayList<>();
                for (int b = 1; b < stationSplit.length; b++) {
                    routeNoList.add(stationSplit[b]);
                }
                List<String> newList = routeNoList.stream().distinct().collect(Collectors.toList());
                for (int c = 0; c < newList.size(); c++) {
                    stationNameComplete = stationNameComplete + "| " + newList.get(c);
                }
                stationFinishList.get(a).setStationName(stationNameComplete);
            }
            System.out.println(stationFinishList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkDuplicatedStationName(String stationName, double latitude, double longitude) {
        boolean check = false;
        if (checkStationList.size() == 0) {
            check = false;
        } else {
            for (int i = 0; i < checkStationList.size(); i++) {
//                if(stationName.equals(checkStationList.get(i).getStationName()) && latitude == checkStationList.get(i).getLatitude()
//                        && longitude == checkStationList.get(i).getLongitude()){
                if (stationName.equals(checkStationList.get(i).getStationName())) {
//                    System.out.println("station name : "+ stationName);
                    check = true;
                }
            }
        }
        return check;
    }

    public List<String> removeDuplicates(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String element : list) {
            if (!newList.equals(element)) {
                newList.add(element);
            }
        }
        return newList;
    }
}
