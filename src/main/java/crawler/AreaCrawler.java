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
    public void getSGApiInfo(boolean districtFlag, boolean wardFlag, boolean streetFlag) {
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
            //set default value id
            int id = 0;
            int wardId = 0;
            for (int i = 0; i < districtList.length(); i++) {
                String district = districtList.getJSONObject(i).get("name").toString();
                String pre = districtList.getJSONObject(i).get("pre").toString();
                String districtData = pre + " " + district;
                id++;
                districtDTO.setDistrictId(id);
                districtDTO.setDistrictName(districtData.trim());
                districtDTO.setProvinceId(1);
                if (districtFlag) {
//                    areaDAO.insertDistrict(districtDTO);
                }

                //ward
                if (wardFlag) {
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
                //street
                if (streetFlag) {
                    JSONArray streetList = districtList.getJSONObject(i).getJSONArray("street");
                    for (int k = 0; k < streetList.length(); k++) {
                        String street = streetList.get(k).toString();
                        streetListAll.add(street);
                    }
                }
            }
            //delete duplicate value
            if (streetFlag) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSGAPIInFor() {
        AreaDAO areaDAO = new AreaDAO();
        if (areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()){
            getSGApiInfo(true,true,true);
        }else if (areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()){
            getSGApiInfo(true,false,true);
        }else if (areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()){
            getSGApiInfo(true,true,false);
        }else if (areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()){
            getSGApiInfo(true,false,false);
        }else if (!areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()){
            getSGApiInfo(false,false,false);
        }else if (!areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()){
            getSGApiInfo(false,true,true);
        }else if (!areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()){
            getSGApiInfo(false,false,true);
        }else if (!areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()){
            getSGApiInfo(false,true,false);
        }
    }
}
