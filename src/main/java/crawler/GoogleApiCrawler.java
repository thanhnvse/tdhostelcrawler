package main.java.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GoogleApiCrawler {
    public String getGoogleApiInfo(double latitude, double longitude, int radius, String type){
        String mainUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+
                latitude + "," + longitude +"&radius=" + radius + "&type=" +
                type +"&keyword=cruise&key=AIzaSyBN-EnJaJx_lU8aw-PnGBdumqGRTG8u2dQ&language=vi";
        String jsonGoogleApi = "";
        try{
            Document googleApiWeb = Jsoup.connect(mainUrl).timeout(10000)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36")
                    .ignoreContentType(true).get();
            jsonGoogleApi = googleApiWeb.body().text();
        }catch (IOException e){
            e.printStackTrace();
        }
        return jsonGoogleApi;
    }
}
