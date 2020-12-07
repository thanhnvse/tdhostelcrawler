package main.java.crawler;

import main.java.JSONParser.JSONParser;
import main.java.dao.GGDAO;
import main.java.entity.Utility;
import main.java.process.GoogleApiProcess;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class GoogleApiCrawler {
    private JSONParser jsonParser = new JSONParser();
    private GoogleApiProcess googleApiProcess = new GoogleApiProcess();
    public String getNextPageToken(double latitude, double longitude, int radius, String type) {
        String mainUrl = googleApiProcess.getNextPageTokenUrl(latitude,longitude,radius,type);
        System.out.println("LINK : " +mainUrl);
        String nextPageToken = "";
        try {
            JSONObject obj = googleApiProcess.readDataObj(mainUrl);
            JSONArray arr = googleApiProcess.readDataArr(obj);
            //for to get utility from results
            jsonParser.parseJSONToObject(arr,obj);
            //get next page token
            if (!obj.has("next_page_token")) {
                nextPageToken = "";
            }
            if (obj.has("next_page_token")) {
                nextPageToken = obj.getString("next_page_token");
                Thread.sleep(4000);
            }
            System.out.println("Token lan 1: " + nextPageToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextPageToken;
    }

    public void getGoogleApiInfo(double latitude, double longitude, int radius, String type) {
        String nextPageToken = getNextPageToken(latitude,longitude,radius,type);
        try {
            while (nextPageToken != "") {
                String urlWithNextPageToken = googleApiProcess.getDataNextPageTokenUrl(nextPageToken);
                System.out.println("LINK : " +urlWithNextPageToken);
                JSONObject objNextToken = googleApiProcess.readDataObj(urlWithNextPageToken);
                JSONArray arrNextToken = googleApiProcess.readDataArr(objNextToken);
                //for to get utility from results
                jsonParser.parseJSONToObject(arrNextToken,objNextToken);
                if (!objNextToken.has("next_page_token")) {
                    nextPageToken = "";
                }
                if (objNextToken.has("next_page_token")) {
                    nextPageToken = objNextToken.getString("next_page_token");
                }
                System.out.println("Token ra: " + nextPageToken);
                Thread.sleep(4000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
