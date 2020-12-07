package main.java.crawler;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import main.java.process.MogiDataProcess;
import main.java.process.MogiProcess;
import main.java.process.SampleProcess;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.Normalizer;
import java.text.ParseException;
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

public class MogiUCrawler {
    public List<Sample> getSampleHostelDataFromMogi() {
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        MogiProcess mogiProcess = new MogiProcess();
        SampleProcess sampleProcess = new SampleProcess();
        MogiDataProcess mogiDataProcess = new MogiDataProcess();
        boolean endFlag = false;
        try {
            List<Street> streetAllList = hostelDAO.getAllStreet();
            List<Ward> wardList = hostelDAO.getAllWard();
            List<District> districtList = hostelDAO.getAllDistrict();
            List<Facility> facilityList = hostelDAO.getAllFacilities();
            List<Service> serviceList = hostelDAO.getAllServices();
            JacksonObj jacksonObj = new JacksonObj();
            for (int pageNumber = 0; pageNumber < 16; pageNumber++) {
                Elements urlList = mogiDataProcess.readUrlList(pageNumber);
                int count = 1;
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
                            Sample sample = new Sample();
                            StreetWard streetWard = new StreetWard();
                            String name = sampleElement.select(jacksonObj.readYamlForMogi().getName()).text().toLowerCase();
                            System.out.println(name);
                            if (!name.contains("căn hộ")) {
                                String streetName = sampleElement.select(jacksonObj.readYamlForMogi().getStreetName()).text();
                                String[] addressList = streetName.split(",");
                                String district = "";
                                if (addressList.length < 2) {
                                    flag = false;
                                } else {
                                    district = mogiDataProcess.getDistrict(addressList);
                                }
                                int districtId = mogiDataProcess.getWardIdAndDistrictId(addressList,wardList,districtList,district).getDistrictId();
                                int wardId = mogiDataProcess.getWardIdAndDistrictId(addressList,wardList,districtList,district).getWardId();
                                streetWard.setWardId(wardId);
                                if (districtId == 0 || wardId == 0) {
                                    flag = false;
                                }
                                int streetId = mogiDataProcess.getStreetId(addressList,streetAllList);
                                streetWard.setStreetId(streetId);
                                if (streetId == 0) {
                                    flag = false;
                                }
                                String superString = sampleElement.select(jacksonObj.readYamlForMogi().getSuperString()).text();
                                double superficiality = mogiDataProcess.getSuperficiality(superString);
                                sample.setSuperficiality(superficiality);
                                String priceString = sampleElement.select(jacksonObj.readYamlForMogi().getPriceString()).text();
                                double price = mogiDataProcess.getPrice(priceString);
                                if (price == 0) {
                                    flag = false;
                                }
                                sample.setPrice(price);
                                sample.setPostAt(postAt);
                                String latLongCrawler = sampleProcess.getStreetCrawlerToGetLatLong(addressList[0], district
                                        ,mogiDataProcess.getWardIdAndDistrictId(addressList,wardList,districtList,district).getWard());
                                if (!latLongCrawler.isEmpty()) {
                                    sample.setLatitude(mogiDataProcess.getLatitude(latLongCrawler));
                                    sample.setLongitude(mogiDataProcess.getLongitude(latLongCrawler));
                                }
                                String detailUrl = sampleElement.select(jacksonObj.readYamlForMogi().getDetailUrl()).attr("href");
                                Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                                        " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                                String description = detailDoc.select(jacksonObj.readYamlForMogi().getDescription()).text().toLowerCase();
                                sample.setFacilities(mogiDataProcess.getFaSeIds(description,facilityList,serviceList).getFacilityInteger());
                                sample.setServices(mogiDataProcess.getFaSeIds(description,facilityList,serviceList).getServiceInteger());
                                if (flag) {
                                    if (!hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId())) {
                                        hostelDAO.insertStreetWard(streetWard);
                                    }
                                }
                                sample.setStreetId(hostelDAO.getStreetWardId(streetWard.getWardId(), streetWard.getStreetId()));
                                sample.setCategoryId(3);
                                System.out.println("Sample : " + sample.toString());
                                if (flag) {
                                    if (!hostelDAO.checkInsertSample(sample.getPrice(), sample.getSuperficiality(), sample.getStreetId())) {
                                        hostelDAO.insertSample(sample);
                                    }
                                }
                            }
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
