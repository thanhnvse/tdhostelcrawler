package main.java.process;

import main.java.entity.Facility;
import main.java.entity.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

public class MogiProcess {
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
            String urlLatLong =getUrlLatLng(convertLanguage);
            JSONObject obj = readDataObj(urlLatLong);
            JSONArray arr = readDataArr(obj);
            for (int i = 0; i < arr.length(); i++) {
                String districtEqual = obj.getJSONArray("results").getJSONObject(i).getString("formatted_address").toLowerCase().trim();
                String districtLower = district.toLowerCase().trim();
                String wardLower = ward.toLowerCase().trim();
                if (districtEqual.contains(districtLower) || districtEqual.contains(wardLower)) {
                    String latitude = obj.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                    String longitude = obj.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                    result = latitude + "x" + longitude;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getUrlLatLng(String convertLanguage){
        return "https://maps.googleapis.com/maps/api/geocode/json?address=" + convertLanguage
                + "&key=AIzaSyDNBmxVGbZ4Je5XHPRqqaZPmDFKjKPPhXk&fbclid=IwAR3ikgMxRMez3HQa8w6_FHNL0uvW-KVx0n8U30aRRiT_Mx8fk15pk45oCyk";
    }

    public JSONObject readDataObj(String urlLatLong) {
        JSONObject obj = new JSONObject();
        try {
            Document mapAddress = Jsoup.connect(urlLatLong).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").ignoreContentType(true).get();
            String mapAddressBody = mapAddress.body().text();
            obj = new JSONObject(mapAddressBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public JSONArray readDataArr(JSONObject obj) {
        return obj.getJSONArray("results");
    }

    public long getMilisecondFromPostAt(String time) throws ParseException {
        long millis = 0;
        if (time.toLowerCase().contains("hôm nay")) {
            millis = getMilisNow();
        } else if (time.toLowerCase().contains("hôm qua")) {
            millis = getMilisYesterday();
        } else {
            millis = getMilisOthers(time);
        }
        return millis;
    }

    public long getMilisNow() {
        long millis = 0;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String myDate = dtf.format(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse(myDate);
            System.out.println("Date :" + date);
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMilisYesterday() {
        long millis = 0;
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMilisOthers(String time) {
        long millis = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar c1 = Calendar.getInstance();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            c1.setTime(date);
            date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }
}
