package main.java.JSONParser;

import main.java.dao.GGDAO;
import main.java.entity.Utility;
import main.java.process.GoogleApiProcess;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
    public void parseJSONToObject(JSONArray arr, JSONObject obj) {
        GGDAO ggdao = new GGDAO();
        GoogleApiProcess googleApiProcess = new GoogleApiProcess();
        for (int j = 0; j < arr.length(); j++) {
            Utility utility = googleApiProcess.getUtilityLatLng(obj,j);
            String utilityName = googleApiProcess.getUtilityName(obj,j);
            String vicinity = "";
            vicinity = googleApiProcess.getVicinity(obj,j);
            int typeId;
            //for in name of type
            JSONArray nameList = googleApiProcess.getNameList(obj,j);
            for (int k = 0; k < nameList.length(); k++) {
                String name = nameList.get(k).toString();
                if (googleApiProcess.checkLocation(obj,j)) {
                    String utilityNameLower = utilityName.toLowerCase();
                    typeId = ggdao.getUtilityIdFromAUType(name);
                    googleApiProcess.checkUtility(utility,typeId,utilityNameLower,vicinity,utilityName);
                }
            }
        }
    }

}
