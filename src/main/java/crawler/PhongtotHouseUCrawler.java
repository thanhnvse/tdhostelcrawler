package main.java.crawler;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import main.java.process.PhongtotDataProcess;
import main.java.process.PhongtotHouseMainProcess;
import main.java.process.PhongtotProcess;
import main.java.process.SampleProcess;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhongtotHouseUCrawler {
    public void getSampleHostelDataFromPhongTot() {
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        JacksonObj jacksonObj = new JacksonObj();
        PhongtotProcess phongtotProcess = new PhongtotProcess();
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        PhongtotHouseMainProcess phongtotHouseMainProcess = new PhongtotHouseMainProcess();
        try {
            boolean endFlag = false;
            Integer pageNum = phongtotDataProcess.getMaxPageNumberHouse();
            for (int pageNumber = 0; pageNumber < pageNum; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                System.out.println("phongtothouseu");
                Elements urlList = phongtotDataProcess.readUrlListHouse(pageNumber);
                for (Element sampleElement : urlList) {
                    String postTimeAnalysis = sampleElement.select(jacksonObj.readYamlForPhongtot().getPostTimeAnalysis()).text();
                    long postAt = phongtotProcess.getMillisecondFromPostAtPhongTot(postTimeAnalysis);
                    long lastPostAt = hostelDAO.getThelastPostAt();
                    if (postAt >= lastPostAt) {
                        phongtotHouseMainProcess.getPhongTotHouseMain(sampleElement);
                    } else {
                        endFlag = true;
                        break;
                    }
                }
                if (endFlag) {
                    break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PhongtotHouseUCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
