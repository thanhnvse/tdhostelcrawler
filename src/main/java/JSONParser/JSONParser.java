package main.java.JSONParser;

import main.java.dao.GGDAO;
import main.java.entity.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
    public void parseJSONToObject(String json){
        JSONObject obj = new JSONObject(json);
        JSONArray arr = obj.getJSONArray("results");

        //set default value of UCategory, UType
        GGDAO ggdao = new GGDAO();
//        ggdao.insertUCategoryList();
//        ggdao.insertAUType();
        for(int i = 0; i < arr.length(); i++){
            String name = obj.getJSONArray("results").getJSONObject(i).getJSONArray("types").get(0).toString();
            //create utility
            Utility utility = new Utility();
            //set utility
            utility.setLatitude(Double.parseDouble(obj.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lat").toString()));
            utility.setLongitude(Double.parseDouble(obj.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lng").toString()));
            utility.setName(obj.getJSONArray("results").getJSONObject(i).getString("name"));
            utility.setTypeId(ggdao.getUtilityIdFromAUType(name));
            ggdao.insertAUtility(utility);
//            System.out.println(utility.getName());
        }
    }
}
