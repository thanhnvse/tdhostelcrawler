package main.java.crawler;

import main.java.dao.GGDAO;
import main.java.dao.SampleHostelDAO;
import main.java.entity.Location;
import main.java.process.AllDataProcess;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllDataCrawler {
    public void crawlData(){
        AllDataProcess allDataProcess = new AllDataProcess();
        SampleCrawler sampleCrawler = new SampleCrawler();
//        NearbyUtilityCrawler nearbyUtilityCrawler = new NearbyUtilityCrawler();
//        try{
//            //crawl json file to get district, ward, street
//            AreaCrawler areaCrawler = new AreaCrawler();
//            areaCrawler.getSGAPIInFor();
//
//            //get data crawl from phongtot
//            Timer timer = new Timer ();
//            TimerTask t = new TimerTask () {
//                @Override
//                public void run () {
//                    //crawl sample
                    sampleCrawler.getSample();
//                }
//            };
//            timer.schedule (t, allDataProcess.getTomorrowMorning0AM());
//
//            //set default value of UCategory, UType and store in db
//            GGDAO ggdao = new GGDAO();
//            if(ggdao.checkUCategoryEmpty()){
//                ggdao.insertUCategoryList();
//            }
//            if(ggdao.checkUTypeEmpty()){
//                ggdao.insertAUType();
//            }
//            //crawl and parse nearby utilities using gg map api
//            nearbyUtilityCrawler.crawlNearbyUtility();
//
//            //crawl bus station
//            if(ggdao.checkBusEmpty()) {
//                BusCrawler busCrawler = new BusCrawler();
//                busCrawler.getBusInfo();
//            }
//        }catch (Exception e){
//            Logger.getLogger(AllDataCrawler.class.getName()).log(Level.SEVERE, null, e);
//        }
    }
}
