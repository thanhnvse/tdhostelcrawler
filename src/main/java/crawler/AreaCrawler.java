package main.java.crawler;

import main.java.dao.AreaDAO;
import main.java.entity.District;
import main.java.entity.Street;
import main.java.entity.Ward;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AreaCrawler {
    public void getSGApiInfo(){
        try {
            String fileName = "sg.json";
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),
                    "UTF8"));
            String str = in.readLine();
            JSONObject obj = new JSONObject(str);
            AreaDAO areaDAO = new AreaDAO();
            List<String> streetListAll = new ArrayList<>();
            //district
            District districtDTO = new District();
            JSONArray districtList = obj.getJSONArray("district");
//            System.out.println("District size: "+ districtList.length());
            //set default value id
            int id = 0;
            int wardId = 0;
            for(int i = 0; i < districtList.length(); i++) {
                String district = districtList.getJSONObject(i).get("name").toString();
//                System.out.println("District : " + district);
                String pre = districtList.getJSONObject(i).get("pre").toString();
//                System.out.println("Pre : " + pre);
                String districtData = pre + " " + district;
                id++;
                districtDTO.setDistrictId(id);
                districtDTO.setDistrictName(districtData.trim());
                districtDTO.setProvinceId(1);
//                areaDAO.insertDistrict(districtDTO);
                //ward
                JSONArray wardList = districtList.getJSONObject(i).getJSONArray("ward");
//                System.out.println("Ward size : "+wardList.length());
                for(int j = 0; j < wardList.length(); j++){
                    wardId++;
                    String ward = wardList.getJSONObject(j).get("name").toString();
//                    System.out.println("Ward : "+ ward);
                    Ward wardDTO = new Ward();
                    wardDTO.setWardId(wardId);
                    wardDTO.setWardName("Phường "+ward.trim());
                    wardDTO.setDistrictId(id);
//                    areaDAO.insertWard(wardDTO);
                }
                //street
                JSONArray streetList = districtList.getJSONObject(i).getJSONArray("street");
                for(int k = 0; k < streetList.length(); k++){
                    String street = streetList.get(k).toString();
//                    System.out.println("Street : "+ street);
                    streetListAll.add(street);
                }
            }
            //delete duplicate value
//            System.out.println("Size 1 : "+ streetListAll.size());
            List<String> arrTemp = new ArrayList<>();
            for (int i = 0; i < streetListAll.size(); i++) {
                if (!arrTemp.contains(streetListAll.get(i))) {
                    arrTemp.add(streetListAll.get(i));
                }
            }
            streetListAll.clear();
            streetListAll.addAll(arrTemp);
//            System.out.println("Size 2 : "+ streetListAll.size());
            int streetId = 0;
            for (int a = 0; a < streetListAll.size(); a++){
//                System.out.println("Street : "+ streetListAll.get(a));
                Street street = new Street();
                streetId++;
                street.setStreetId(streetId);
                street.setStreetName(streetListAll.get(a).trim());
                areaDAO.insertStreet(street);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
