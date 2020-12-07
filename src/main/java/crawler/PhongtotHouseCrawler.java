package main.java.crawler;

import main.java.process.PhongtotDataProcess;
import main.java.process.PhongtotHouseMainProcess;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PhongtotHouseCrawler {
    public void getSampleHostelDataFromPhongTot() {
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        PhongtotHouseMainProcess phongtotHouseMainProcess = new PhongtotHouseMainProcess();
        try {
            Integer pageNum = phongtotDataProcess.getMaxPageNumberHouse();
            System.out.println("phongtothouse");
            for (int pageNumber = 0; pageNumber < pageNum; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                Elements urlList = phongtotDataProcess.readUrlListHouse(pageNumber);
                for (Element sampleElement : urlList) {
                    phongtotHouseMainProcess.getPhongTotHouseMain(sampleElement);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PhongtotHouseCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
