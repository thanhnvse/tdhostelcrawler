package main.java.process;

import main.java.dao.AreaDAO;
import main.java.entity.District;
import main.java.entity.Street;
import main.java.entity.Ward;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AreaProcess {
    public JSONArray readJsonFile(){
        JSONArray districtList = new JSONArray();
        try {
            String fileName = "sg.json";
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),
                    "UTF8"));
            String str = in.readLine();
            JSONObject obj = new JSONObject(str);
            districtList = obj.getJSONArray("district");

        }catch (Exception e){
            e.printStackTrace();
        }
        return districtList;
    }

    public District setDataForDistrictFromFile(JSONArray districtList, int i, int id){
        District districtDTO = new District();
        String district = districtList.getJSONObject(i).get("name").toString();
        String pre = districtList.getJSONObject(i).get("pre").toString();
        String districtData = pre + " " + district;
        districtDTO.setDistrictId(id);
        districtDTO.setDistrictName(districtData.trim());
        districtDTO.setProvinceId(1);
        return districtDTO;
    }

    public void setDataForWardFromFile(JSONArray districtList, int i, int id){
        AreaDAO areaDAO =  new AreaDAO();
        int wardId = 0;
        JSONArray wardList = districtList.getJSONObject(i).getJSONArray("ward");
        for (int j = 0; j < wardList.length(); j++) {
            wardId++;
            String ward = wardList.getJSONObject(j).get("name").toString();
            Ward wardDTO = new Ward();
            wardDTO.setWardId(wardId);
            wardDTO.setWardName("Phường " + ward.trim());
            wardDTO.setDistrictId(id);
            areaDAO.insertWard(wardDTO);
        }
    }

    public List<String> setDataForStreetFromFile(JSONArray districtList, int i){
        List<String> streetListAll = new ArrayList<>();
        JSONArray streetList = districtList.getJSONObject(i).getJSONArray("street");
        for (int k = 0; k < streetList.length(); k++) {
            String street = streetList.get(k).toString();
            streetListAll.add(street);
        }
        return streetListAll;
    }

    public void checkDuplicateAndInsertStreet(List<String> streetListAll){
        AreaDAO areaDAO =  new AreaDAO();
        List<String> arrTemp = new ArrayList<>();
        for (int i = 0; i < streetListAll.size(); i++) {
            if (!arrTemp.contains(streetListAll.get(i))) {
                arrTemp.add(streetListAll.get(i));
            }
        }
        streetListAll.clear();
        streetListAll.addAll(arrTemp);
        int streetId = 0;
        for (int a = 0; a < streetListAll.size(); a++) {
            Street street = new Street();
            streetId++;
            street.setStreetId(streetId);
            street.setStreetName(streetListAll.get(a).trim());
            areaDAO.insertStreet(street);
        }
    }
}
