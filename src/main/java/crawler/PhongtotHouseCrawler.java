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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static main.java.constants.StaticDistrict.*;

public class PhongtotHouseCrawler {
    public List<Sample> getSampleHostelDataFromPhongTot() {
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        String mainUrl = "http://phongtot.vn/nha-nguyen-can?fill=79";
        try {
            List<Street> streetAllList = hostelDAO.getAllStreet();
            List<Ward> wardList = hostelDAO.getAllWard();
            List<District> districtList = hostelDAO.getAllDistrict();
            List<Facility> facilityList = hostelDAO.getAllFacilities();
            List<Service> serviceList = hostelDAO.getAllServices();
            boolean endFlag = false;
            for (int pageNumber = 0; pageNumber < 27; pageNumber++) {
                System.out.println("Page :" + pageNumber);
                Document phongtotDoc = Jsoup.connect(mainUrl + "&page=" + pageNumber).timeout(50000).userAgent("Mozilla/5.0 " +
                        "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                Elements urlList = phongtotDoc.getElementsByClass("room-item");
                for (Element sampleElement : urlList) {
                    boolean flag = true;
                    Sample sample = new Sample();
                    StreetWard streetWard = new StreetWard();
                    //postAt
                    String postTimeAnalysis = sampleElement.getElementsByClass("block-room-item-info").select("a").get(1).text();
                    long postAt = getMilisecondFromPostAt(postTimeAnalysis);
                    //the last post at
                    long lastPostAt = hostelDAO.getThelastPostAt();
                    System.out.println("postAt : " + postAt + " last :" + lastPostAt);
                    if (postAt >= lastPostAt) {

                        //street
                        String streetName = sampleElement.getElementsByClass("block-room-item-address").select("a").text();
                        System.out.println("Street Name all: " + streetName);
                        //split string
                        String[] addressList = streetName.split("-");
//                    System.out.println("Data split : "+ addressList[0] + " * " + addressList[1]+ " * " + addressList[2] );
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

                        //superficiality
                        String superString = sampleElement.getElementsByClass("pull-left").select("a").text();
                        superString = superString.replaceAll("[^0-9]+", "/");
                        String[] superItem = superString.split("/");
                        double superficiality = Double.parseDouble(superItem[0]);
//                    System.out.println("Superficiality: "+ superficiality);
                        sample.setSuperficiality(superficiality);

                        //price
                        String priceString = sampleElement.getElementsByClass("block-room-item-price").select("a").text();
                        priceString = priceString.replaceAll("[^0-9,-\\.]", "");
                        String[] item = priceString.split(",");
                        String priceStringAll;
                        if (item.length == 2 || item.length == 1) {
                            priceStringAll = "0." + item[0];
                        } else {
                            priceStringAll = item[0] + "." + item[1];
                        }
                        double price = Double.parseDouble(priceStringAll);
//                    System.out.println("Price: "+price);
                        if (price == 0) {
                            flag = false;
                        }
                        sample.setPrice(price);

                        //postAt
                        sample.setPostAt(postAt);

                        //detail page
                        String detailUrl = sampleElement.getElementsByClass("block-room-item-title").select("a").attr("href");
                        Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                                " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                        Elements detailUrlList = detailDoc.getElementsByClass("col-md-9");
                        for (Element detailElement : detailUrlList) {
                            String streetNameAll = "";
                            Elements streetList = detailElement.getElementsByClass("address").select("a");
                            System.out.println("AAA : " + streetList.size());
                            if (streetList.size() < 2) {
                                flag = false;
                            } else {
                                for (int i = 0; i < streetList.size(); i++) {
                                    String street = detailElement.getElementsByClass("address").select("a").get(1).text();
                                    if (!street.toLowerCase().contains("đường")) {
//                                street = "Đường "+ detailElement.getElementsByClass("address").select("a").get(1).text();
                                        street = detailElement.getElementsByClass("address").select("a").get(1).text();
                                    }
//                            System.out.println("Street name :"+ street);
                                    streetNameAll = street;
                                    break;
                                }
                            }
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
                            //facility, service
                            Elements faseList = detailElement.getElementsByClass("type").last().select("a");
                            List<String> facilities = new ArrayList<>();
                            List<String> services = new ArrayList<>();
                            for (Element e : faseList) {
                                String fasFinal = e.text().trim();
                                if (fasFinal.equals("Chỗ để xe")) {
                                    fasFinal = "giữ xe";
                                    services.add(fasFinal);
                                } else if (fasFinal.equals("Internet")) {
                                    fasFinal = "internet";
                                    services.add(fasFinal);
                                } else if (fasFinal.equals("Thang máy")) {
                                    fasFinal = "thang máy";
                                    services.add(fasFinal);
                                } else if (fasFinal.equals("Điều hòa")) {
                                    fasFinal = "Máy lạnh";
                                    facilities.add(fasFinal);
                                } else if (fasFinal.equals("Bình nóng lạnh")) {
                                    fasFinal = "Máy nước nóng";
                                    facilities.add(fasFinal);
                                } else if (fasFinal.equals("Máy giặt")) {
                                    facilities.add(fasFinal);
                                } else if (fasFinal.equals("Tivi")) {
                                    facilities.add(fasFinal);
                                }
                            }
                            List<Integer> facilityInteger = getFacilityIdFromFacilityName(facilities, facilityList);
                            List<Integer> serviceInteger = getServiceIdFromServiceName(services, serviceList);
                            sample.setFacilities(facilityInteger);
                            sample.setServices(serviceInteger);
                            //longitude, latitude
//                        System.out.println("All :" + addressList[0] + "Dis :" + district + "Ward :" + ward + "Street :" + streetNameAll);
                            String latLongCrawler = getStreetCrawlerToGetLatLong(addressList[0], district, ward);
//                        System.out.println("Street Crawler :" + latLongCrawler);
                            if (!latLongCrawler.isEmpty()) {
                                String[] latlong = latLongCrawler.split("x");

                                double latitude = Double.parseDouble(latlong[0]);
                                double longitude = Double.parseDouble(latlong[1]);
                                sample.setLatitude(latitude);
                                sample.setLongitude(longitude);

//                            System.out.println("Lat Long : "+ latitude +":"+ longitude);
                            }
                            //check insert street ward in db
                            System.out.println("Street ward : " + streetWard.toString());
                            System.out.println("Result check insert street ward (true = ko vao): " + hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId()));
                            if (flag) {
                                if (!hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId())) {
                                    hostelDAO.insertStreetWard(streetWard);
                                }
                            }
                            sample.setStreetId(hostelDAO.getStreetWardId(streetWard.getWardId(), streetWard.getStreetId()));
                            //insert sample
                            System.out.println("Sample : " + sample.toString());
                            System.out.println("flag : " + flag);
                            sample.setCategoryId(2);
                            if (flag) {
                                if (!hostelDAO.checkInsertSample(sample.getPrice(), sample.getSuperficiality(), sample.getStreetId())) {
                                    hostelDAO.insertSample(sample);
                                }
                            }
                            System.out.println("-------------------------------");
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
//                System.out.println("result wardLower : "+ wardLower);
//            if (districtEqual.contains(districtLower)||districtEqual.contains(wardLower)||(districtEqual.contains(districtLower) && districtEqual.contains(wardLower))){\
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

    public long getMilisecondFromPostAt(String time) {
        String[] timeList = time.split(" ");
        int number = Integer.parseInt(timeList[0]);
        String postAt = timeList[1].toLowerCase();
        long millis = 0;
        try {
            if (postAt.toLowerCase().contains("phút") || postAt.toLowerCase().contains("giờ")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDateTime now = LocalDateTime.now();
                String myDate = dtf.format(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date date = sdf.parse(myDate);
                millis = date.getTime();
//                System.out.println("MiLi : "+ millis);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Calendar c1 = Calendar.getInstance();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDateTime now = LocalDateTime.now();
                String myDate = dtf.format(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date date = sdf.parse(myDate);
                c1.setTime(date);
                if ((postAt.toLowerCase().contains("ngày"))) {
                    if (now.getDayOfMonth() - number > 0) {
                        c1.roll(Calendar.DATE, -number);
                        date = sdf.parse(dateFormat.format(c1.getTime()));
                        millis = date.getTime();
                    } else {
                        c1.roll(Calendar.DATE, -number);
                        c1.roll(Calendar.MONTH, -1);
                        date = sdf.parse(dateFormat.format(c1.getTime()));
                        millis = date.getTime();
                    }
//                    System.out.println(millis);
                } else if ((postAt.toLowerCase().contains("tuần"))) {
                    if (now.getDayOfMonth() - (7 * number) > 0) {
                        c1.roll(Calendar.DATE, -7 * number);
                        date = sdf.parse(dateFormat.format(c1.getTime()));
                        millis = date.getTime();
                    } else {
                        c1.roll(Calendar.DATE, -7 * number);
                        c1.roll(Calendar.MONTH, -1);
                        date = sdf.parse(dateFormat.format(c1.getTime()));
                        millis = date.getTime();
                    }
//                    System.out.println(millis);
                } else if ((postAt.toLowerCase().contains("tháng"))) {
                    if (now.getMonthValue() - number > 0) {
                        c1.roll(Calendar.MONTH, -number);
                        date = sdf.parse(dateFormat.format(c1.getTime()));
                        millis = date.getTime();
                    } else {
                        c1.roll(Calendar.MONTH, -number);
                        c1.roll(Calendar.YEAR, -1);
                        date = sdf.parse(dateFormat.format(c1.getTime()));
                        millis = date.getTime();
                    }
//                    System.out.println(millis);
                } else if ((postAt.toLowerCase().contains("năm"))) {
                    c1.roll(Calendar.YEAR, -number);
                    date = sdf.parse(dateFormat.format(c1.getTime()));
                    millis = date.getTime();
//                    System.out.println(millis);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }
}
