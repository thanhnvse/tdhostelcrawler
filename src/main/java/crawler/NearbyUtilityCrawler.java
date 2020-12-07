package main.java.crawler;

import main.java.dao.GGDAO;
import main.java.dao.SampleHostelDAO;
import main.java.entity.Location;

import java.util.List;

public class NearbyUtilityCrawler {
    public void crawlNearbyUtility() {
        GGDAO ggdao = new GGDAO();
        GoogleApiCrawler googleApiCrawler = new GoogleApiCrawler();
        List<Location> locationList = ggdao.getLocationList();
        List<String> typeList = ggdao.createTypeListToCrawl();
        int count = 0;
        for(Location locationA : locationList){
            for(String type : typeList){
                googleApiCrawler.getGoogleApiInfo(locationA.getLatitude(),locationA.getLongitude(),8000,type);
                System.out.println("Finish :" + type);
            }
            System.out.println("End location :"+ locationA.getLatitude()+" "+locationA.getLongitude());
            System.out.println("count :" + count++);
        }
    }
}
