package main.java.process;

import main.java.dao.GGDAO;
import main.java.entity.Utility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GoogleApiProcess {
    public String getNextPageTokenUrl(double latitude, double longitude, int radius, String type){
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                latitude + "," + longitude + "&radius=" + radius + "&type=" +
                type + "&key=AIzaSyBN-EnJaJx_lU8aw-PnGBdumqGRTG8u2dQ" + "&language=vi";
    }

    public JSONObject readDataObj(String mainUrl) {
        JSONObject obj = new JSONObject();
        try {
            Document googleApiWeb = Jsoup.connect(mainUrl)
                    .ignoreContentType(true).get();
            String jsonGoogleApi = googleApiWeb.body().text();
            obj = new JSONObject(jsonGoogleApi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public JSONArray readDataArr(JSONObject obj) {
        return obj.getJSONArray("results");
    }

    public String getDataNextPageTokenUrl(String nextPageToken){
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBN-EnJaJx_lU8aw-PnGBdumqGRTG8u2dQ" +
                "&language=vi&pagetoken=" + nextPageToken;
    }

    public Utility getUtilityLatLng(JSONObject obj, int j){
        Utility utility = new Utility();
        Double utilityLatitude = Double.parseDouble(obj.getJSONArray("results").getJSONObject(j).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
        Double utilityLongitude = Double.parseDouble(obj.getJSONArray("results").getJSONObject(j).getJSONObject("geometry").getJSONObject("location").get("lng").toString());
        utility.setLatitude(utilityLatitude);
        utility.setLongitude(utilityLongitude);
        return utility;
    }

    public String getUtilityName(JSONObject obj, int j){
        return obj.getJSONArray("results").getJSONObject(j).getString("name");
    }

    public String getVicinity(JSONObject obj, int j){
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
        return vicinity;
    }

    public boolean checkLocation(JSONObject obj, int j){
        boolean flag = false;
        if(obj.getJSONArray("results").getJSONObject(j).getJSONObject("plus_code").get("compound_code").toString().contains("Thành phố Hồ Chí Minh")){
            flag = true;
        }
        return flag;
    }

    public JSONArray getNameList(JSONObject obj, int j) {
        return obj.getJSONArray("results").getJSONObject(j).getJSONArray("types");
    }

    public void checkUtility(Utility utility,int typeId, String utilityNameLower, String vicinity, String utilityName){
        GGDAO ggdao = new GGDAO();
        if (typeId != 0) {
            if (typeId == 1 && ggdao.checkConvenienceStore(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 2 && ggdao.checkSupermarket(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 3 && ggdao.checkUniversity(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 4 && ggdao.checkPrimary(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 5 && ggdao.checkSecondary(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 7) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 8 && ggdao.checkBank(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 9 && ggdao.checkHospital(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
            if (typeId == 10 && ggdao.checkDrugStore(utilityNameLower)) {
                insertUtility(utility,typeId,vicinity,utilityName);
            }
        }
    }

    public Utility setAllUtility(Utility utility, int typeId, String vicinity, String utilityName){
        utility.setTypeId(typeId);
        //utilityName + address
        String utilityAndVicinity;
        if(vicinity == ""){
            utilityAndVicinity = utilityName;
        }else{
            utilityAndVicinity = utilityName + " - " + vicinity;
        }
        utility.setName(utilityAndVicinity);
        return utility;
    }

    public void insertUtility(Utility utility,int typeId, String vicinity, String utilityName){
        GGDAO ggdao = new GGDAO();
        Utility temp = setAllUtility(utility,typeId,vicinity, utilityName);
        utility = temp;
        if (!ggdao.checkInsert(utility.getLatitude(), utility.getLongitude(), utility.getName(), typeId)) {
            ggdao.insertAUtility(utility);
        }
        System.out.println(utility);
    }
}
