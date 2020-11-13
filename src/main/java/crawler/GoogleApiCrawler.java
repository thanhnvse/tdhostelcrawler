package main.java.crawler;

import main.java.JSONParser.JSONParser;
import main.java.dao.GGDAO;
import main.java.entity.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class GoogleApiCrawler {
    private JSONParser jsonParser = new JSONParser();
    private  GGDAO ggdao = new GGDAO();
    public String getNextPageToken(double latitude, double longitude, int radius, String type) {
        String mainUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                latitude + "," + longitude + "&radius=" + radius + "&type=" +
                type + "&key=AIzaSyBN-EnJaJx_lU8aw-PnGBdumqGRTG8u2dQ" + "&language=vi";
        System.out.println("LINK : " +mainUrl);
        String nextPageToken = "";
        try {
            Document googleApiWeb = Jsoup.connect(mainUrl)
                    .ignoreContentType(true).get();
            String jsonGoogleApi = googleApiWeb.body().text();
            JSONObject obj = new JSONObject(jsonGoogleApi);
            JSONArray arr = obj.getJSONArray("results");
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
                String urlWithNextPageToken = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyBN-EnJaJx_lU8aw-PnGBdumqGRTG8u2dQ" +
                        "&language=vi&pagetoken=" + nextPageToken;
                System.out.println("LINK : " +urlWithNextPageToken);
                Document googleApiWebNextToken = Jsoup.connect(urlWithNextPageToken)
                        .ignoreContentType(true).get();
                String jsonGoogleApiNextToken = googleApiWebNextToken.body().text();
                JSONObject objNextToken = new JSONObject(jsonGoogleApiNextToken);
                JSONArray arrNextToken = objNextToken.getJSONArray("results");
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
