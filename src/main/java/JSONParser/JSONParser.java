package main.java.JSONParser;

import main.java.dao.GGDAO;
import main.java.entity.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
    public void parseJSONToObject(JSONArray arr, JSONObject obj) {
        GGDAO ggdao = new GGDAO();
        for (int j = 0; j < arr.length(); j++) {
            //create utility
            Utility utility = new Utility();
            //set utility
            Double utilityLatitude = Double.parseDouble(obj.getJSONArray("results").getJSONObject(j).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
            Double utilityLongitude = Double.parseDouble(obj.getJSONArray("results").getJSONObject(j).getJSONObject("geometry").getJSONObject("location").get("lng").toString());
            String utilityName = obj.getJSONArray("results").getJSONObject(j).getString("name");
            String vicinity = "";
            if (obj.getJSONArray("results").getJSONObject(j).has("vicinity")) {
                String vicinityAll = obj.getJSONArray("results").getJSONObject(j).getString("vicinity");
                String[] vicinities = vicinityAll.split(",");
                if (vicinities.length >= 2){
                    vicinity = vicinities[0] + ", " + vicinities[1];
                }else{
                    vicinity = "";
                }
                System.out.println("------------------------------------");
                System.out.println("Vicinity :" + vicinity);
            }
            int typeId;
            utility.setLatitude(utilityLatitude);
            utility.setLongitude(utilityLongitude);
            //declare
            String utilityAndVicinity;
            //for in name of type
            JSONArray nameList = obj.getJSONArray("results").getJSONObject(j).getJSONArray("types");
            for (int k = 0; k < nameList.length(); k++) {
                String name = nameList.get(k).toString();
                if (obj.getJSONArray("results").getJSONObject(j).getJSONObject("plus_code").get("compound_code").toString().contains("Thành phố Hồ Chí Minh")) {
                    String utilityNameLower = utilityName.toLowerCase();
                    typeId = ggdao.getUtilityIdFromAUType(name);
                    if (typeId != 0) {
                        if (typeId == 1 && ggdao.checkConvenienceStore(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 2 && ggdao.checkSupermarket(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 3 && ggdao.checkUniversity(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 4 && ggdao.checkPrimary(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 5 && ggdao.checkSecondary(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
//                        if (typeId == 6) {
//                            utility.setTypeId(typeId);
//                            //utilityName + address
//                            if(vicinity == ""){
//                                utilityAndVicinity = utilityName;
//                            }else{
//                                utilityAndVicinity = utilityName + " - " + vicinity;
//                            }
//                            utility.setName(utilityAndVicinity);
//                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
//                                ggdao.insertAUtility(utility);
//                            }
//                            System.out.println(utility);
//                        }
                        if (typeId == 7) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 8 && ggdao.checkBank(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 9 && ggdao.checkHospital(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                        if (typeId == 10 && ggdao.checkDrugStore(utilityNameLower)) {
                            utility.setTypeId(typeId);
                            //utilityName + address
                            if(vicinity == ""){
                                utilityAndVicinity = utilityName;
                            }else{
                                utilityAndVicinity = utilityName + " - " + vicinity;
                            }
                            utility.setName(utilityAndVicinity);
                            if (!ggdao.checkInsert(utilityLatitude, utilityLongitude, utilityAndVicinity, typeId)) {
                                ggdao.insertAUtility(utility);
                            }
                            System.out.println(utility);
                        }
                    }
                }
            }
        }
    }

}
