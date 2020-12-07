package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.crawler.PhongtotCrawler;
import main.java.dao.SampleHostelDAO;
import main.java.dto.FaSeIdsDTO;
import main.java.entity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.constants.StaticDistrict.*;
import static main.java.constants.StaticUrl.PHONG_TOT;
import static main.java.constants.StaticUrl.PHONG_TOT_HOUSE;
import static main.java.constants.Synounymous.*;

public class PhongtotDataProcess {
    private SampleHostelDAO hostelDAO = new SampleHostelDAO();
    private List<Street> streetAllList = hostelDAO.getAllStreet();
    private List<Ward> wardList = hostelDAO.getAllWard();
    private List<District> districtList = hostelDAO.getAllDistrict();
    private JacksonObj jacksonObj = new JacksonObj();

    public Integer getMaxPageNumber() {
        Integer pageNum = 0;
        try {
            Document phongtotDocForPage = Jsoup.connect(PHONG_TOT).timeout(50000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            pageNum = Integer.parseInt(phongtotDocForPage.select(jacksonObj.readYamlForPhongtot().getPageNum()).text());
        }catch (Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, "Max page number", e);
        }
        return pageNum;
    }

    public Elements readUrlList(int pageNumber) {
        Elements urlList = new Elements();
        try {
            Document phongtotDoc = Jsoup.connect(PHONG_TOT + "&page=" + pageNumber).timeout(50000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            urlList = phongtotDoc.select(jacksonObj.readYamlForPhongtot().getUrlList());
        }catch (Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, "Url", e);
        }
        return urlList;
    }

    public String getDistrictPhongTot(String[] addressList){
        String district = "";
        try {
            int districtIndex = 2;
            district = addressList[districtIndex];
            if (district.toLowerCase().contains(BINH_CHANH) || district.toLowerCase().contains(CAN_GIO) ||
                    district.toLowerCase().contains(CU_CHI) || district.toLowerCase().contains(HOC_MON) ||
                    district.toLowerCase().contains(NHA_BE)) {
                district = "Huyện" + addressList[districtIndex];
            } else {
                if (!district.toLowerCase().contains("quận")) {
                    district = "Quận" + addressList[districtIndex];
                }
                System.out.println("District :" + district);
            }
        } catch (Exception e) {
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, null, e);
        }
        return district;
    }

    public DistrictWard getWardIdAndDistrictIdPhongTot(String[] addressList, String district){
        DistrictWard districtWard = new DistrictWard();
        int districtId = 0;
        int wardId = 0;
        String ward = addressList[1].trim();
        ward = ward.replaceAll("^(?!00[1-9])0", "");
        if (!ward.toLowerCase().contains("phường")) {
            ward = "Phường " + ward;
        }
        System.out.println("District ngoài : "+ district);
        System.out.println("Ward ngoài : "+ ward);
        System.out.println(wardList.size()+" "+districtList.size());
        districtWard.setWard(ward);
        for (int wardItem = 0; wardItem < wardList.size(); wardItem++) {
            if (ward.trim().equalsIgnoreCase(wardList.get(wardItem).getWardName().trim())) {
                wardId = wardList.get(wardItem).getWardId();
                for (int districtItem = 0; districtItem < districtList.size(); districtItem++) {
                    String districtNameToEqual = districtList.get(districtItem).getDistrictName().trim();
                    if (district.trim().equalsIgnoreCase(districtNameToEqual)) {
                        districtId = districtList.get(districtItem).getDistrictId();
                        if (wardList.get(wardItem).getDistrictId() == districtId) {
                            System.out.println("District trong : "+ districtId);
                            System.out.println("Ward trong : "+ wardId);
                            districtWard.setDistrictId(districtId);
                            districtWard.setWardId(wardId);
                        }
                    }
                }
            }
        }
        return districtWard;
    }

    public double getSuperficiality(String superString1){
        String superString = superString1;
        superString = superString.replaceAll("[^0-9]+", "/");
        String[] superItem = superString.split("/");
        double superficiality = Double.parseDouble(superItem[0]);
        return superficiality;
    }

    public double getPrice(String priceString1){
        String priceString = priceString1;
        String priceStringAll = "";
        priceString = priceString.replaceAll("[^0-9,-\\.]", "");
        String[] item = priceString.split(",");
        if (item.length == 2 || item.length == 1) {
            priceStringAll = "0." + item[0];
        } else {
            priceStringAll = item[0] + "." + item[1];
        }
        double price = Double.parseDouble(priceStringAll);
        return  price;
    }

    public Elements readDetailUrlList(String detailUrl) {
        Elements detailUrlList = new Elements();
        try {
            Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            detailUrlList = detailDoc.select(jacksonObj.readYamlForPhongtot().getDetailUrlList());
        }catch (Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, "Detail Url", e);
        }
        return detailUrlList;
    }

    public int getStreetIdPhongTot(Elements streetList){
        String streetNameAll = "";
        for (int i = 0; i < streetList.size(); i++) {
            String street = streetList.get(1).text();
            if (!street.toLowerCase().contains("đường")) {
                street = streetList.get(1).text();
            }
            streetNameAll = street;
            break;
        }
        //equal with street in db
        int streetId = 0;
        for (int streetItem = 0; streetItem < streetAllList.size(); streetItem++) {
            if (streetAllList.get(streetItem).getStreetName().equalsIgnoreCase(streetNameAll)) {
                streetId = streetAllList.get(streetItem).getStreetId();
            }
        }
        return streetId;
    }

    public FaSeIdsDTO getFaSeIds(Elements faseList){
        FaSeIdsDTO faSeIdsDTO = new FaSeIdsDTO();
        try {
            List<Facility> facilityList = hostelDAO.getAllFacilities();
            List<Service> serviceList = hostelDAO.getAllServices();
            SampleProcess sampleProcess = new SampleProcess();
            FaSeProcess faSeProcess = new FaSeProcess();
            List<Integer> facilityInteger = sampleProcess.getFacilityIdFromFacilityName(faSeProcess.getFacilities(faseList), facilityList);
            List<Integer> serviceInteger = sampleProcess.getServiceIdFromServiceName(faSeProcess.getServices(faseList),serviceList);
            faSeIdsDTO.setFacilityInteger(facilityInteger);
            faSeIdsDTO.setServiceInteger(serviceInteger);
        }catch(Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, null, e);
        }
        return  faSeIdsDTO;
    }

    public double getLatitude(String latLongCrawler){
        String[] latLong = latLongCrawler.split("x");
        return Double.parseDouble(latLong[0]);
    }

    public double getLongitude(String latLongCrawler){
        String[] latLong = latLongCrawler.split("x");
        return Double.parseDouble(latLong[1]);
    }

    public Integer getMaxPageNumberHouse() {
        Integer pageNum = 0;
        try {
            Document phongtotDocForPage = Jsoup.connect(PHONG_TOT_HOUSE).timeout(50000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            pageNum = Integer.parseInt(phongtotDocForPage.select(jacksonObj.readYamlForPhongtot().getPageNum()).text());
        }catch (Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, "Max page number for house", e);
        }
        return pageNum;
    }

    public Elements readUrlListHouse(int pageNumber) {
        Elements urlList = new Elements();
        try {
            Document phongtotDoc = Jsoup.connect(PHONG_TOT_HOUSE + "&page=" + pageNumber).timeout(50000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            urlList = phongtotDoc.select(jacksonObj.readYamlForPhongtot().getUrlList());
        }catch (Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, "Url for house", e);
        }
        return urlList;
    }
    public Elements readDetailUrlListHouse(String detailUrl) {
        Elements detailUrlList = new Elements();
        try {
            Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            detailUrlList = detailDoc.select(jacksonObj.readYamlForPhongtot().getDetailUrlList());
        }catch (Exception e){
            Logger.getLogger(PhongtotDataProcess.class.getName()).log(Level.SEVERE, "Detail url for house", e);
        }
        return detailUrlList;
    }
}
