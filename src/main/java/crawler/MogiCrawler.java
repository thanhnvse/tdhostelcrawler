package main.java.crawler;

import main.java.process.MogiDataProcess;
import main.java.process.MogiMainProcess;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MogiCrawler {
    public void getSampleHostelDataFromMogi() {
        MogiDataProcess mogiDataProcess = new MogiDataProcess();
        MogiMainProcess mogiMainProcess = new MogiMainProcess();
        try {
            for (int pageNumber = 1; pageNumber < 17; pageNumber++) {
                Elements urlList = mogiDataProcess.readUrlList(pageNumber);
                for (Element sampleElement : urlList) {
                    mogiMainProcess.getMogiMain(sampleElement);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(MogiCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
