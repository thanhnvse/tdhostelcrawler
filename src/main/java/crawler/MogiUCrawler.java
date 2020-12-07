package main.java.crawler;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import main.java.process.MogiDataProcess;
import main.java.process.MogiProcess;
import main.java.process.MogiUMainProcess;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MogiUCrawler {
    public List<Sample> getSampleHostelDataFromMogi() {
        JacksonObj jacksonObj = new JacksonObj();
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        MogiProcess mogiProcess = new MogiProcess();
        MogiDataProcess mogiDataProcess = new MogiDataProcess();
        MogiUMainProcess mogiUMainProcess = new MogiUMainProcess();
        boolean endFlag = false;
        try {
            int number = Integer.parseInt(jacksonObj.readYamlForMogi().getNumber());
            for (int pageNumber = 0; pageNumber < number; pageNumber++) {
                Elements urlList = mogiDataProcess.readUrlList(pageNumber);
                for (Element sampleElement : urlList) {
                    boolean flag = true;
                    if (sampleElement.hasClass("bath") || sampleElement.hasClass("bed") || sampleElement.hasClass("land")) {
                        flag = false;
                    }
                    else {
                        String postTimeAnalysis = sampleElement.select(jacksonObj.readYamlForMogi().getPostTimeAnalysis()).text();
                        postTimeAnalysis = mogiDataProcess.getPostAt(postTimeAnalysis);
                        long postAt = mogiProcess.getMillisecondFromPostAt(postTimeAnalysis);
                        long lastPostAt = hostelDAO.getThelastPostAt();
                        if (postAt >= lastPostAt) {
                            mogiUMainProcess.getMogiUMain(sampleElement,flag);
                        }else {
                            endFlag = true;
                            break;
                        }
                    }
                }
                if (endFlag) {
                    break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(MogiUCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
        return sampleList;
    }
}
