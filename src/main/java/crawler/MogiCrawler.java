package main.java.crawler;

import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
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
import java.util.regex.Pattern;

import static main.java.constants.StaticDistrict.*;

public class MogiCrawler {
    public List<Sample> getSampleHostelDataFromMogi() {
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        String mainUrl = "https://mogi.vn/thue-phong-tro-o-ghep?cp=";
        try {
            List<Street> streetAllList = hostelDAO.getAllStreet();
            List<Ward> wardList = hostelDAO.getAllWard();
            List<District> districtList = hostelDAO.getAllDistrict();
            List<Facility> facilityList = hostelDAO.getAllFacilities();
            List<Service> serviceList = hostelDAO.getAllServices();
            for (int pageNumber = 4; pageNumber < 5; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                Document mogiDoc = Jsoup.connect(mainUrl + pageNumber).timeout(50000).userAgent("Mozilla/5.0 " +
                        "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                Elements urlList = mogiDoc.select(".props li");
                int count = 1;
                for (Element sampleElement : urlList) {
                    boolean flag = true;
                    if (sampleElement.hasClass("bath") || sampleElement.hasClass("bed") || sampleElement.hasClass("land")) {
                        flag = false;
                    } else {
                        System.out.println("-----------------------");
                        System.out.println("count : " + count++);
                        Sample sample = new Sample();
                        StreetWard streetWard = new StreetWard();
                        String name = sampleElement.getElementsByClass("link-overlay").text().toLowerCase();
                        System.out.println(name);
                        if (!name.contains("căn hộ")) {
                            //street
                            String streetName = sampleElement.getElementsByClass("prop-addr").text();
                            System.out.println("Street Name all: " + streetName);
                            //split string
                            String[] addressList = streetName.split(",");
//                            System.out.println("Data split : " + addressList[0] + " * " + addressList[1] + " * " + addressList[2]);
                            //district
                            int districtId = 0;
                            String district = "";
                            try {
                                if (addressList.length < 2) {
                                    flag = false;
                                } else {
                                    int districtIndex = 2;
                                    district = addressList[districtIndex];
                                    if (district.toLowerCase().contains(BINH_CHANH) || district.toLowerCase().contains(CAN_GIO) ||
                                            district.toLowerCase().contains(CU_CHI) || district.toLowerCase().contains(HOC_MON) ||
                                            district.toLowerCase().contains(NHA_BE)) {
                                        district = "Huyện" + addressList[districtIndex];
//                        System.out.println("District :"+ district);
                                    } else {
                                        if (!district.toLowerCase().contains("quận")) {
                                            district = "Quận" + addressList[districtIndex];
                                        }
                                        System.out.println("District :" + district);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //ward
                            int wardIndex = 1;
                            String ward = addressList[wardIndex].trim();
                            ward = ward.replaceAll("^(?!00[1-9])0", "");
//                    System.out.println("Ward sau khi regex : "+ ward);
                            if (!ward.toLowerCase().contains("phường")) {
                                ward = "Phường " + ward;
                            }
                            System.out.println("Ward :" + ward);
                            System.out.println("Data : " + district + " " + ward);
                            int wardId = 0;
                            for (int wardItem = 0; wardItem < wardList.size(); wardItem++) {
                                if (ward.trim().equalsIgnoreCase(wardList.get(wardItem).getWardName().trim())) {
                                    wardId = wardList.get(wardItem).getWardId();
                                    for (int districtItem = 0; districtItem < districtList.size(); districtItem++) {
                                        String districtNameToEqual = districtList.get(districtItem).getDistrictName().trim();
                                        if (district.trim().equalsIgnoreCase(districtNameToEqual)) {
                                            districtId = districtList.get(districtItem).getDistrictId();
                                            if (wardList.get(wardItem).getDistrictId() == districtId) {
                                                System.out.println("District id : " + districtId);
                                                System.out.println("Ward id : " + wardId);
                                                streetWard.setWardId(wardId);
                                            }
                                        }
                                    }
                                }
                            }
                            if (districtId == 0 || wardId == 0) {
                                flag = false;
                            }

                            //street
                            int streetIndex = 0;
                            String streetNameAll = "";
                            String street = addressList[streetIndex].trim();
                            if (street.contains("Đường")) {
                                street.replace("Đường", "");
                            } else if (street.contains("đường")) {
                                street.replace("đường", "");
                            }
//                            System.out.println("Street name :" + street);
                            streetNameAll = street;
                            //equal with street in db
                            int streetId = 0;
                            for (int streetItem = 0; streetItem < streetAllList.size(); streetItem++) {
                                if (streetAllList.get(streetItem).getStreetName().equalsIgnoreCase(streetNameAll)) {
                                    streetId = streetAllList.get(streetItem).getStreetId();
                                    streetWard.setStreetId(streetId);
//                                System.out.println("Street id : "+ streetAllList.get(streetItem).getStreetId());
                                }
                            }
                            if (streetId == 0) {
                                flag = false;
                            }
                            System.out.println("Street ward : " + streetWard.toString());
                            //superficiality
                            String superString = sampleElement.getElementsByClass("land").text();
                            superString = superString.replaceAll("[^0-9]+", "/");
                            String[] superItem = superString.split("/");
                            double superficiality = Double.parseDouble(superItem[0]);
                            System.out.println("Superficiality: " + superficiality);
                            sample.setSuperficiality(superficiality);

                            //price
                            String priceString = sampleElement.getElementsByClass("price").text();
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
                            System.out.println("Price: " + price);
                            if (price == 0) {
                                flag = false;
                            }
                            sample.setPrice(price);

                            //postAt
                            String postTimeAnalysis = sampleElement.getElementsByClass("prop-created").text();
                            postTimeAnalysis = postTimeAnalysis.replace("Ngày đăng:", "");
                            postTimeAnalysis = postTimeAnalysis.trim();
                            System.out.println("Post Time Analysis : " + postTimeAnalysis);
                            long postAt = getMilisecondFromPostAt(postTimeAnalysis);
                            sample.setPostAt(postAt);
                            System.out.println("Post At : " + postAt);

                            //longitude, latitude
                            System.out.println("All :" + addressList[0] + "Dis :" + district + "Ward :" + ward + "Street :" + streetNameAll);
                            String latLongCrawler = getStreetCrawlerToGetLatLong(addressList[0], district, ward);
                            System.out.println("Street Crawler :" + latLongCrawler);
                            if (!latLongCrawler.isEmpty()) {
                                String[] latlong = latLongCrawler.split("x");

                                double latitude = Double.parseDouble(latlong[0]);
                                double longitude = Double.parseDouble(latlong[1]);
                                sample.setLatitude(latitude);
                                sample.setLongitude(longitude);
                                System.out.println("Lat Long : " + latitude + ":" + longitude);
                            }

                            //detail page
                            String detailUrl = sampleElement.getElementsByClass("prop-title").select("a").attr("href");
                            Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                            String description = detailDoc.getElementsByClass("prop-info-content").text().toLowerCase();
                            System.out.println("Mô tả: " + description);
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
                            System.out.println("Faci : "+facilities);
                            System.out.println("Ser : "+services);
                            List<Integer> facilityInteger = getFacilityIdFromFacilityName(facilities, facilityList);
                            List<Integer> serviceInteger = getServiceIdFromServiceName(services, serviceList);
                            sample.setFacilities(facilityInteger);
                            sample.setServices(serviceInteger);

                            //check insert street ward in db
                            System.out.println("Street ward : " + streetWard.toString());
                            System.out.println("Result check insert street ward (true = ko vao): " + hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId()));
                            if (flag) {
                                if (!hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId())) {
//                                    hostelDAO.insertStreetWard(streetWard);
                                }
                            }
                            sample.setStreetId(hostelDAO.getStreetWardId(streetWard.getWardId(), streetWard.getStreetId()));
                            //insert sample
                            sample.setCategoryId(3);
                            System.out.println("Sample : " + sample.toString());
                            System.out.println("flag : " + flag);
                            if (flag) {
                                if (!hostelDAO.checkInsertSample(sample.getPrice(), sample.getSuperficiality(), sample.getStreetId())) {
//                                    hostelDAO.insertSample(sample);
                                    System.out.println("insert dc");
                                }
                            }
                            System.out.println("-------------------------------");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sampleList;
    }

    public List<Integer> getFacilityIdFromFacilityName(List<String> facilityString, List<Facility> facilityList) {
        List<Integer> facilityInteger = new ArrayList<>();
        for (int fa = 0; fa < facilityList.size(); fa++) {
            for (int faS = 0; faS < facilityString.size(); faS++) {
                if (facilityString.get(faS).equalsIgnoreCase(facilityList.get(fa).getName())) {
                    facilityInteger.add(facilityList.get(fa).getId());
                }
            }
        }
        return facilityInteger;
    }

    public List<Integer> getServiceIdFromServiceName(List<String> serviceString, List<Service> serviceList) {
        List<Integer> serviceInteger = new ArrayList<>();
        for (int fa = 0; fa < serviceList.size(); fa++) {
            for (int faS = 0; faS < serviceString.size(); faS++) {
                if (serviceString.get(faS).equalsIgnoreCase(serviceList.get(fa).getName())) {
                    serviceInteger.add(serviceList.get(fa).getId());
                }
            }
        }
        return serviceInteger;
    }

    public String getStreetCrawlerToGetLatLong(String data, String district, String ward) {
        String result = "";
        try {
            String streetCrawler = data.replaceAll(" ", "%20").replaceAll(",", "%20");
            String temp = Normalizer.normalize(streetCrawler, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String convertLanguage = pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d");
            String urlLatLong = "https://maps.googleapis.com/maps/api/geocode/json?address=" + convertLanguage
                    + "&key=AIzaSyDNBmxVGbZ4Je5XHPRqqaZPmDFKjKPPhXk&fbclid=IwAR3ikgMxRMez3HQa8w6_FHNL0uvW-KVx0n8U30aRRiT_Mx8fk15pk45oCyk";
            Document mapAddress = Jsoup.connect(urlLatLong).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").ignoreContentType(true).get();
            String mapAddressBody = mapAddress.body().text();
            JSONObject obj = new JSONObject(mapAddressBody);
            JSONArray arr = obj.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                String districtEqual = obj.getJSONArray("results").getJSONObject(i).getString("formatted_address").toLowerCase().trim();
//                System.out.println("result districtEqual : "+ districtEqual);
                String districtLower = district.toLowerCase().trim();
//                System.out.println("result districtLower : "+ districtLower);
                String wardLower = ward.toLowerCase().trim();
                if (districtEqual.contains(districtLower) || districtEqual.contains(wardLower)) {
                    String latitude = obj.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                    String longitude = obj.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                    result = latitude + "x" + longitude;
//                    System.out.println("result lat long : "+ result);
                } else {
//                    System.out.println("Fail");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public long getMilisecondFromPostAt(String time) throws ParseException {
        long millis = 0;
        try {
            if (time.toLowerCase().contains("hôm nay")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDateTime now = LocalDateTime.now();
                String myDate = dtf.format(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date date = sdf.parse(myDate);
                System.out.println("Date :" + date);
                millis = date.getTime();
//                System.out.println("MiLi : "+ millis);
            } else if (time.toLowerCase().contains("hôm qua")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Calendar c1 = Calendar.getInstance();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDateTime now = LocalDateTime.now();
                String myDate = dtf.format(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date date = sdf.parse(myDate);
                c1.setTime(date);
                c1.roll(Calendar.DATE, -1);
                date = sdf.parse(dateFormat.format(c1.getTime()));
                System.out.println("Date :" + date);
                millis = date.getTime();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Calendar c1 = Calendar.getInstance();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                c1.setTime(date);
                date = sdf.parse(dateFormat.format(c1.getTime()));
                millis = date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }
}
