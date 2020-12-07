package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.dto.FaSeIdsDTO;
import main.java.entity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.constants.StaticDistrict.*;
import static main.java.constants.StaticUrl.MOGI;

public class MogiDataProcess {
    private SampleHostelDAO hostelDAO = new SampleHostelDAO();
    private List<Street> streetAllList = hostelDAO.getAllStreet();
    private List<Ward> wardList = hostelDAO.getAllWard();
    private List<District> districtList = hostelDAO.getAllDistrict();
    public Elements readUrlList(int pageNumber) {
        JacksonObj jacksonObj = new JacksonObj();
        Elements urlList = new Elements();
        try {
            Document mogiDoc = Jsoup.connect(MOGI +"?cp="+ pageNumber).timeout(50000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            urlList = mogiDoc.select(jacksonObj.readYamlForMogi().getUrlList());
        }catch (Exception e){
            Logger.getLogger(MogiDataProcess.class.getName()).log(Level.SEVERE, "Url", e);
        }
        return urlList;
    }

    public String getDistrict(String[] addressList){
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
            Logger.getLogger(MogiDataProcess.class.getName()).log(Level.SEVERE, null, e);
        }
        return district;
    }

    public DistrictWard getWardIdAndDistrictId(String[] addressList, String district){
        DistrictWard districtWard = new DistrictWard();
        int districtId = 0;
        int wardId = 0;
        String ward = addressList[1].trim();
        ward = ward.replaceAll("^(?!00[1-9])0", "");
        if (!ward.toLowerCase().contains("phường")) {
            ward = "Phường " + ward;
        }
        districtWard.setWard(ward);
        for (int wardItem = 0; wardItem < wardList.size(); wardItem++) {
            if (ward.trim().equalsIgnoreCase(wardList.get(wardItem).getWardName().trim())) {
                wardId = wardList.get(wardItem).getWardId();
                for (int districtItem = 0; districtItem < districtList.size(); districtItem++) {
                    String districtNameToEqual = districtList.get(districtItem).getDistrictName().trim();
                    if (district.trim().equalsIgnoreCase(districtNameToEqual)) {
                        districtId = districtList.get(districtItem).getDistrictId();
                        if (wardList.get(wardItem).getDistrictId() == districtId) {
                            districtWard.setDistrictId(districtId);
                            districtWard.setWardId(wardId);
                        }
                    }
                }
            }
        }
        return districtWard;
    }

    public int getStreetId(String[] addressList){
        String streetNameAll = "";
        int streetId = 0;
        String street = addressList[0].trim();
        if (street.contains("Đường")) {
            street.replace("Đường", "");
        } else if (street.contains("đường")) {
            street.replace("đường", "");
        }
        streetNameAll = street;
        for (int streetItem = 0; streetItem < streetAllList.size(); streetItem++) {
            if (streetAllList.get(streetItem).getStreetName().equalsIgnoreCase(streetNameAll)) {
                streetId = streetAllList.get(streetItem).getStreetId();
            }
        }
        return streetId;
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
        if (priceString.contains("triệu") && !priceString.contains("nghìn")) {
            priceString = priceString.replaceAll("[^0-9]+", "/");
            String[] item = priceString.split("/");
            priceStringAll = item[0] + "";
        }
        if (priceString.contains("triệu") && priceString.contains("nghìn")) {
            priceString = priceString.replaceAll("[^0-9]+", "/");
            String[] item = priceString.split("/");
            priceStringAll = item[0] + "." + item[1];
        }
        if (priceString.contains("đ")) {
            priceString = priceString.replaceAll("[^0-9]+", "/");
            String[] item = priceString.split("/");
            priceStringAll = "0." + item[0];
        }
        double price = Double.parseDouble(priceStringAll);
        return  price;
    }

    public String getPostAt(String postTimeAnalysis){
        return  postTimeAnalysis.replace("Ngày đăng:", "").trim();
    }

    public double getLatitude(String latLongCrawler){
        String[] latLong = latLongCrawler.split("x");
        return Double.parseDouble(latLong[0]);
    }

    public double getLongitude(String latLongCrawler){
        String[] latLong = latLongCrawler.split("x");
        return Double.parseDouble(latLong[1]);
    }

    public FaSeIdsDTO getFaSeIds(String description){
        SampleProcess sampleProcess = new SampleProcess();
        FaSeProcess faSeProcess = new FaSeProcess();
        FaSeIdsDTO faSeIdsDTO = new FaSeIdsDTO();
        try{
            List<Facility> facilityList = hostelDAO.getAllFacilities();
            List<Service> serviceList = hostelDAO.getAllServices();
            List<Integer> facilityInteger = sampleProcess.getFacilityIdFromFacilityName(faSeProcess.getFacilities(description), facilityList);
            List<Integer> serviceInteger = sampleProcess.getServiceIdFromServiceName(faSeProcess.getServices(description), serviceList);
            faSeIdsDTO.setFacilityInteger(facilityInteger);
            faSeIdsDTO.setServiceInteger(serviceInteger);
        }catch (Exception e){
            Logger.getLogger(MogiDataProcess.class.getName()).log(Level.SEVERE, null, e);
        }
        return  faSeIdsDTO;
    }
}
