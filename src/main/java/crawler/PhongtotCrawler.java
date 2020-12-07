package main.java.crawler;

import main.java.process.PhongtotDataProcess;
import main.java.process.PhongtotMainProcess;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PhongtotCrawler {
    public void getSampleHostelDataFromPhongTot() {
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        PhongtotMainProcess phongtotMainProcess = new PhongtotMainProcess();
        try {
            Integer pageNum = phongtotDataProcess.getMaxPageNumber();
            for (int pageNumber = 0; pageNumber < pageNum; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                System.out.println("phongtot");
                Elements urlList = phongtotDataProcess.readUrlList(pageNumber);
                for (Element sampleElement : urlList) {
                    phongtotMainProcess.getPhongTotMain(sampleElement);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PhongtotCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
