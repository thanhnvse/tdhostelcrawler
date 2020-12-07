package main.java.crawler;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import main.java.process.PhongtotDataProcess;
import main.java.process.PhongtotMainProcess;
import main.java.process.PhongtotProcess;
import main.java.process.SampleProcess;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static main.java.constants.StaticDistrict.*;
import static main.java.constants.StaticUrl.*;

public class PhongtotUCrawler {
    public void getSampleHostelDataFromPhongTot() {
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        JacksonObj jacksonObj = new JacksonObj();
        PhongtotProcess phongtotProcess = new PhongtotProcess();
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        PhongtotMainProcess phongtotMainProcess = new PhongtotMainProcess();
        try {
            boolean endFlag = false;
            Integer pageNum = phongtotDataProcess.getMaxPageNumber();
            for (int pageNumber = 0; pageNumber < pageNum; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                System.out.println("phongtotu");
                Elements urlList = phongtotDataProcess.readUrlList(pageNumber);
                for (Element sampleElement : urlList) {
                    String postTimeAnalysis = sampleElement.select(jacksonObj.readYamlForPhongtot().getPostTimeAnalysis()).text();
                    long postAt = phongtotProcess.getMillisecondFromPostAtPhongTot(postTimeAnalysis);
                    long lastPostAt = hostelDAO.getThelastPostAt();
                    if (postAt >= lastPostAt) {
                        phongtotMainProcess.getPhongTotMain(sampleElement);
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
            Logger.getLogger(PhongtotUCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
