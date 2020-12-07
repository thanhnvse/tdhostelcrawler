package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.dto.FaSeIdsDTO;
import main.java.entity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static main.java.constants.StaticDistrict.*;
import static main.java.constants.StaticUrl.MOGI;

public class MogiDataProcess {
    public Elements readUrlList(int pageNumber) {
        JacksonObj jacksonObj = new JacksonObj();
        Elements urlList = new Elements();
        try {
            Document mogiDoc = Jsoup.connect(MOGI +"?cp="+ pageNumber).timeout(50000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            urlList = mogiDoc.select(jacksonObj.readYamlForMogi().getUrlList());
        }catch (Exception e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return district;
    }

    public DistrictWard getWardIdAndDistrictId(String[] addressList, List<Ward> wardList, List<District> districtList, String district){
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

    public int getStreetId(String[] addressList, List<Street> streetAllList){
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

    public FaSeIdsDTO getFaSeIds(String description, List<Facility> facilityList, List<Service> serviceList){
        FaSeIdsDTO faSeIdsDTO = new FaSeIdsDTO();
        MogiProcess mogiProcess = new MogiProcess();
        List<String> facilities = new ArrayList<>();
        List<String> services = new ArrayList<>();
        if (description.contains("chỗ để xe") || description.contains("nhà xe") || description.contains("xe")) {
            services.add("Giữ xe");
        }
        if (description.contains("đổ rác")) {
            services.add("đổ rác");
        }
        if (description.contains("thang máy")) {
            services.add("thang máy");
        }
        if (description.contains("dọn vệ sinh") || description.contains("dọn vs")) {
            services.add("dọn vệ sinh");
        }
        if (description.contains("internet") || description.contains("mạng") || description.contains("wifi")) {
            services.add("internet");
        }
        if (description.contains("điện")) {
            services.add("điện");
        }
        if (description.contains("nước")) {
            services.add("nước");
        }
        if (description.contains("máy lạnh") || description.contains("điều hòa")) {
            facilities.add("Máy lạnh");
        }
        if (description.contains("tủ lạnh")) {
            facilities.add("Tủ lạnh");
        }
        if (description.contains("gác") || description.contains("lầu") || description.contains("tầng lửng") || description.contains("gác lửng")) {
            facilities.add("Gác");
        }
        if (description.contains("máy giặt")) {
            facilities.add("Máy giặt");
        }
        if (description.contains("bếp gas") || description.contains("bếp")) {
            facilities.add("Bếp gas");
        }
        if (description.contains("wc riêng") || description.contains("toilet") || description.contains("wc")
                || description.contains("nhà vệ sinh") || description.contains("nhà vs")) {
            facilities.add("WC riêng");
        }
        if (description.contains("giường ngủ") || description.contains("giường")) {
            facilities.add("Giường ngủ");
        }
        if (description.contains("tủ đồ") || description.contains("tủ") || description.contains("ngăn")) {
            facilities.add("Tủ đồ");
        }
        if (description.contains("cửa sổ") || description.contains("cửa thông gió") || description.contains("cửa")) {
            facilities.add("Cửa sổ");
        }
        if (description.contains("tivi") || description.contains("vô tuyến") || description.contains("ti vi") || description.contains("tv")) {
            facilities.add("Tivi");
        }
        if (description.contains("máy nước nóng") || description.contains("máy nóng lạnh")) {
            facilities.add("Máy nước nóng");
        }
        if (description.contains("balcon") || description.contains("ban công")) {
            facilities.add("Balcon");
        }
        List<Integer> facilityInteger = mogiProcess.getFacilityIdFromFacilityName(facilities, facilityList);
        List<Integer> serviceInteger = mogiProcess.getServiceIdFromServiceName(services, serviceList);
        faSeIdsDTO.setFacilityInteger(facilityInteger);
        faSeIdsDTO.setServiceInteger(serviceInteger);
        return  faSeIdsDTO;
    }
}
