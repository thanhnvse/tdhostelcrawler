package main.java.crawler;

import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static main.java.constants.StaticDistrict.*;

public class PhongtotCrawler {
    public List<Sample> getSampleHostelDataFromPhongTot() throws IOException, SQLException, NamingException, ClassNotFoundException {
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO =  new SampleHostelDAO();
        List<Street> streetAllList = hostelDAO.getAllStreet();
        List<Ward> wardList = hostelDAO.getAllWard();
        List<District> districtList = hostelDAO.getAllDistrict();
        List<Facility> facilityList = hostelDAO.getAllFacilities();
        List<Service> serviceList = hostelDAO.getAllServices();
        String mainUrl = "http://phongtot.vn/phong-tro-nha-tro?fill=79";
        try{
            for(int pageNumber = 0 ; pageNumber < 1; pageNumber++){
                Document phongtotDoc = Jsoup.connect(mainUrl+"&page="+pageNumber).timeout(10000).userAgent("Mozilla/5.0 " +
                        "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                Elements urlList = phongtotDoc.getElementsByClass("room-item");
                for(Element sampleElement : urlList){
                    Sample sample = new Sample();
                    StreetWard streetWard =  new StreetWard();
                    //street
                    String streetName = sampleElement.getElementsByClass("block-room-item-address").select("a").text();
                    System.out.println("Street Name all: "+ streetName);
                    //split string
                    String[] addressList = streetName.split("-");

                    //district
                    String district = addressList[2];
                    int districtId = 0;
                    if(district.toLowerCase().contains(BINH_CHANH)|| district.toLowerCase().contains(CAN_GIO)||
                            district.toLowerCase().contains(CU_CHI)|| district.toLowerCase().contains(HOC_MON)||
                            district.toLowerCase().contains(NHA_BE)){
                        district = "Huyện"+ addressList[2];
//                        System.out.println("District :"+ district);
                    }else{
                        if (!district.toLowerCase().contains("quận")){
                            district = "Quận"+ addressList[2];
                        }
//                        System.out.println("District :"+ district);
                    }
                    //ward
                    String ward = addressList[1].trim();
                    ward = ward.replaceAll("^(?!00[1-9])0","");
//                    System.out.println("Ward sau khi regex : "+ ward);

                    if (!ward.toLowerCase().contains("phường")){
                        ward = "Phường "+ ward;
                    }
//                    System.out.println("Ward :"+ ward);
                    int wardId = 0;
                    for (int wardItem = 0; wardItem < wardList.size(); wardItem++){
                        if(ward.trim().equalsIgnoreCase(wardList.get(wardItem).getWardName().trim())){
                            wardId = wardList.get(wardItem).getWardId();
                            streetWard.setWardId(wardId);
                            for (int districtItem = 0; districtItem < districtList.size(); districtItem++){
                                String districtNameToEqual = districtList.get(districtItem).getDistrictName().trim();
                                if(district.trim().equalsIgnoreCase(districtNameToEqual)){
                                    districtId = districtList.get(districtItem).getDistrictId();
                                    if(wardList.get(wardItem).getDistrictId() == districtId){
                                        System.out.println("District id : "+ districtId);
                                        System.out.println("Ward id : "+ wardId);
                                        streetWard.setWardId(wardId);
                                    }
                                }
                            }
                        }
                    }

                    //dien tich
                    String superString = sampleElement.getElementsByClass("pull-left").select("a").text();
                    superString = superString.replaceAll("[^0-9]+", "/");
                    String[] superItem = superString.split("/");
                    double superficiality = Double.parseDouble(superItem[0]);
//                    System.out.println("Superficiality: "+ superficiality);
                    sample.setSuperficiality(superficiality);

                    //gia ca
                    String priceString = sampleElement.getElementsByClass("block-room-item-price").select("a").text();
                    priceString = priceString.replaceAll("[^0-9,-\\.]", "");
                    String[] item = priceString.split(",");
                    String priceStringAll;
                    if(item.length == 2){
                        priceStringAll = "0."+item[0];
                    }else{
                        priceStringAll = item[0]+"."+item[1];
                    }
                    double price = Double.parseDouble(priceStringAll);
//                    System.out.println("Price: "+price);
                    sample.setPrice(price);

                    //detail page
                    String detailUrl = sampleElement.getElementsByClass("block-room-item-title").select("a").attr("href");
                    Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                    Elements detailUrlList = detailDoc.getElementsByClass("col-md-9");
                    for(Element detailElement : detailUrlList){
                        String streetNameAll = "";
                        Elements streetList = detailElement.getElementsByClass("address").select("a");
                        for(int i = 0; i < streetList.size();i++){
                            String street = detailElement.getElementsByClass("address").select("a").get(1).text();
                            if (!street.toLowerCase().contains("đường")){
//                                street = "Đường "+ detailElement.getElementsByClass("address").select("a").get(1).text();
                                street = detailElement.getElementsByClass("address").select("a").get(1).text();
                            }
//                            System.out.println("Street name :"+ street);
                            streetNameAll = street;
                            break;
                        }
                        //equal with street in db
                        for (int streetItem = 0; streetItem < streetAllList.size(); streetItem++){
                            if(streetAllList.get(streetItem).getStreetName().equalsIgnoreCase(streetNameAll)){
                                streetWard.setStreetId(streetAllList.get(streetItem).getStreetId());
                                System.out.println("Street id : "+ streetAllList.get(streetItem).getStreetId());
                                streetWard.setStreetId(streetAllList.get(streetItem).getStreetId());
                            }
                        }
                        //facility, service
                        Elements faseList = detailElement.getElementsByClass("type").last().select("a");
                        List<String> facilities = new ArrayList<>();
                        List<String> services = new ArrayList<>();
                        for(Element e : faseList){
                            String fasFinal = e.text().trim();
                            if(fasFinal.equals("Chỗ để xe")){
                                fasFinal = "Giữ xe";
                                services.add(fasFinal);
                            }else if(fasFinal.equals("Internet")) {
                                fasFinal = "internet";
                                services.add(fasFinal);
                            }else if(fasFinal.equals("Thang máy")) {
                                fasFinal = "thang máy";
                                services.add(fasFinal);
                            }else if(fasFinal.equals("Điều hòa")){
                                fasFinal = "Máy lạnh";
                                facilities.add(fasFinal);
                            }else if(fasFinal.equals("Bình nóng lạnh")) {
                                fasFinal = "Máy nước nóng";
                                facilities.add(fasFinal);
                            }else if(fasFinal.equals("Máy giặt")){
                                facilities.add(fasFinal);
                            }else if(fasFinal.equals("Tivi")){
                                facilities.add(fasFinal);
                            }
                        }
                        List<Integer> facilityInteger = getFacilityIdFromFacilityName(facilities, facilityList);
                        List<Integer> serviceInteger = getServiceIdFromServiceName(services, serviceList);
                        sample.setFacilities(facilityInteger);
                        sample.setServices(serviceInteger);
//                        for(Integer faci : facilityInteger){
//                            System.out.println("Faci :" + faci);
//                        }
//
//                        for(Integer ser : serviceInteger){
//                            System.out.println("Ser :" + ser);
//                        }
                        //longitude, latitude
                        double latitude = Double.parseDouble(detailElement.getElementsByClass("gllpLatitude").attr("value"));
                        double longitude = Double.parseDouble(detailElement.getElementsByClass("gllpLongitude").attr("value"));
//                        System.out.println("Latitude name :"+ latitude);
//                        System.out.println("Longitude name :"+ longitude);
                        sample.setLatitude(latitude);
                        sample.setLongitude(longitude);
                        //check insert street ward in db
                        System.out.println("Street ward : "+ streetWard.toString());
                        System.out.println("Result : "+ hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId()));
                        if(!hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId())){
                            hostelDAO.insertStreetWard(streetWard);
                        }
                        sample.setStreetId(hostelDAO.getStreetWardId(streetWard.getWardId(),streetWard.getStreetId()));
                        //insert sample
                        System.out.println("Sample : "+ sample.toString());
//                        if(!hostelDAO.checkInsertSample(sample.getPrice(),sample.getSuperficiality(),sample.getStreetId())){
//                            hostelDAO.insertSample(sample);
//                        }
                        System.out.println("-------------------------------");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sampleList;
    }

    public List<Integer> getFacilityIdFromFacilityName(List<String> facilityString, List<Facility> facilityList){
        List<Integer> facilityInteger = new ArrayList<>();
        for(int fa = 0; fa < facilityList.size(); fa++){
            for(int faS = 0; faS < facilityString.size(); faS++){
                if(facilityString.get(faS).equalsIgnoreCase(facilityList.get(fa).getName())){
                    facilityInteger.add(facilityList.get(fa).getId());
                }
            }
        }
        return facilityInteger;
    }

    public List<Integer> getServiceIdFromServiceName(List<String> serviceString, List<Service> serviceList){
        List<Integer> serviceInteger = new ArrayList<>();
        for(int fa = 0; fa < serviceList.size(); fa++){
            for(int faS = 0; faS < serviceString.size(); faS++){
                if(serviceString.get(faS).equalsIgnoreCase(serviceList.get(fa).getName())){
                    serviceInteger.add(serviceList.get(fa).getId());
                }
            }
        }
        return serviceInteger;
    }

//    public GeoPoint getLocationFromAddress(String strAddress){
//
//        Geocoder coder = new Geocoder(this);
//        List<Address> address;
//        GeoPoint p1 = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress,5);
//            if (address==null) {
//                return null;
//            }
//            Address location=address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
//                    (double) (location.getLongitude() * 1E6));
//
//            return p1;
//        }
//    }

}
