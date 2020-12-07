package main.java.crawler;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import main.java.process.PhongtotDataProcess;
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
    public List<Sample> getSampleHostelDataFromPhongTot() {
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        JacksonObj jacksonObj = new JacksonObj();
        SampleProcess sampleProcess = new SampleProcess();
        PhongtotProcess phongtotProcess = new PhongtotProcess();
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        try {
            boolean endFlag = false;
            Integer pageNum = phongtotDataProcess.getMaxPageNumber();
            for (int pageNumber = 0; pageNumber < pageNum; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                Elements urlList = phongtotDataProcess.readUrlList(pageNumber);
                for (Element sampleElement : urlList) {
                    boolean flag = true;
                    Sample sample = new Sample();
                    StreetWard streetWard = new StreetWard();
                    String postTimeAnalysis = sampleElement.select(jacksonObj.readYamlForPhongtot().getPostTimeAnalysis()).text();
                    long postAt = phongtotProcess.getMillisecondFromPostAtPhongTot(postTimeAnalysis);
                    long lastPostAt = hostelDAO.getThelastPostAt();
                    if (postAt >= lastPostAt) {
                        String[] addressList = sampleElement.select(jacksonObj.readYamlForPhongtot().getStreetName()).text().split("-");
                        String district = "";
                        if (addressList.length < 2) {
                            flag = false;
                        } else {
                            district = phongtotDataProcess.getDistrictPhongTot(addressList);
                        }
                        int districtId = phongtotDataProcess.getWardIdAndDistrictIdPhongTot(addressList, district).getDistrictId();
                        int wardId = phongtotDataProcess.getWardIdAndDistrictIdPhongTot(addressList, district).getWardId();
                        streetWard.setWardId(wardId);
                        if (districtId == 0 || wardId == 0) {
                            flag = false;
                        }
                        String superString = sampleElement.select(jacksonObj.readYamlForPhongtot().getSuperString()).text();
                        double superficiality = phongtotDataProcess.getSuperficiality(superString);
                        sample.setSuperficiality(superficiality);
                        String priceString = sampleElement.select(jacksonObj.readYamlForPhongtot().getPriceString()).text();
                        double price = phongtotDataProcess.getPrice(priceString);
                        if (price == 0) {
                            flag = false;
                        }
                        sample.setPrice(price);
                        sample.setPostAt(postAt);
                        String detailUrl = sampleElement.select(jacksonObj.readYamlForPhongtot().getDetailUrl()).attr("href");
                        Elements detailUrlList = phongtotDataProcess.readDetailUrlList(detailUrl);
                        for (Element detailElement : detailUrlList) {
                            Elements streetList = detailElement.select(jacksonObj.readYamlForPhongtot().getStreetList());
                            int streetId = phongtotDataProcess.getStreetIdPhongTot(streetList);
                            streetWard.setStreetId(streetId);
                            if (streetId == 0) {
                                flag = false;
                            }
                            Elements faseList = detailElement.select(jacksonObj.readYamlForPhongtot().getFaseList());
                            sample.setFacilities(phongtotDataProcess.getFaSeIds(faseList).getFacilityInteger());
                            sample.setServices(phongtotDataProcess.getFaSeIds(faseList).getServiceInteger());
                            String latLongCrawler = sampleProcess.getStreetCrawlerToGetLatLong(addressList[0], district,
                                    phongtotDataProcess.getWardIdAndDistrictIdPhongTot(addressList, district).getWard());
                            if (!latLongCrawler.isEmpty()) {
                                sample.setLatitude(phongtotDataProcess.getLatitude(latLongCrawler));
                                sample.setLongitude(phongtotDataProcess.getLongitude(latLongCrawler));
                            }
                            System.out.println("Result check insert street ward (true = ko vao): " + hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId()));
                            if (flag) {
                                if (!hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId())) {
                                    hostelDAO.insertStreetWard(streetWard);
                                }
                            }
                            sample.setStreetId(hostelDAO.getStreetWardId(streetWard.getWardId(), streetWard.getStreetId()));
                            sample.setCategoryId(1);
                            System.out.println("Sample : " + sample.toString());
                            if (flag) {
                                if (!hostelDAO.checkInsertSample(sample.getPrice(), sample.getSuperficiality(), sample.getStreetId())) {
                                    hostelDAO.insertSample(sample);
                                }
                            }
                        }
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
        return sampleList;
    }
}
