package main.java;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.JSONParser.JSONParser;
import main.java.crawler.GoogleApiCrawler;
import main.java.crawler.PhongtotCrawler;
import main.java.dao.GGDAO;
import main.java.dao.SampleHostelDAO;
import main.java.entity.*;
import main.java.util.DBUtil;
import org.hibernate.HibernateException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetDocumentFromURL {

    public static void main(String[] args){
        try{
//            GetDocumentFromURL getDocumentFromURL = new GetDocumentFromURL();
//            getDocumentFromURL.thuenhatro360();
//            GGDAO ggdao = new GGDAO();
//            ggdao.insertEntity();

            PhongtotCrawler phongtotCrawler = new PhongtotCrawler();
            phongtotCrawler.getSampleHostelDataFromPhongTot();

//            SampleHostelDAO hostelDAO = new SampleHostelDAO();
//            List<Service> facilityList =  hostelDAO.getAllServices();
//            System.out.println("Size :" + facilityList.size());
//            for(int i=0; i<facilityList.size();i++){
//                System.out.println("ALLLL :"+ facilityList.get(i).getId() +" "+ facilityList.get(i).getName());
//            }

//            GoogleApiCrawler googleApiCrawler = new GoogleApiCrawler();
//            JSONParser jsonParser = new JSONParser();
//            GGDAO ggdao = new GGDAO();
//            List<String> typeList = ggdao.createTypeListToCrawl();
//            for(String type : typeList){
//                jsonParser.parseJSONToObject(googleApiCrawler.getGoogleApiInfo(10.8035455,106.6182532,1500000,type));
//                System.out.println("Finish :" + type);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void thuephongtro() {
        try {
            String mainUrl = "https://thuephongtro.com/cho-thue-phong-tro-ho-chi-minh";
            String changeUrl = "https://thuephongtro.com";
            int sizeAll = 0;
            //loop for crawl paging
//            for (int i = 1; i < 1520; i++) {
            for (int i = 1; i < 2; i++) {
                Document doc = Jsoup.connect(mainUrl + "/?page=" + i).timeout(10000).get();

                Elements infoList = doc.getElementsByClass("news-info");
                sizeAll += infoList.size();
                System.out.println("List trong trang chủ : " + infoList.size());
                //loop for crawl url to detail page
                for (Element info : infoList) {
                    String url = changeUrl + info.getElementsByClass("news-title").select("a").attr("href").trim();
                    System.out.println("Url đến detail là : " + url);

                    Document detailUrl = Jsoup.connect(url).timeout(10000).get();
                    Elements detailHostel = detailUrl.body().select("div.block-content-meta > div");
//                    System.out.println("kết quả : " + detailHostel.size());
                    for (Element table : detailHostel) {
                        Elements trList = table.getElementsByClass("table_overview").select("tr");
//                        System.out.println(trList);
                        for (int j = 0; j < trList.size(); j++) {
                            if (j == 0) {
                                String address = trList.get(j).select("td").get(1).text();
                                System.out.println("address :" + address);
                                String[] partAddressList = address.split(",");
                                for (String partAddress : partAddressList) {
                                    System.out.println("PartList : " + partAddress);
                                }
                            } else if (j == 2) {
                                String sampleHostelId = trList.get(j).select("td").get(1).text();
                                System.out.println("id hostel :" + sampleHostelId);
                            } else if (j == 3) {
                                String superficiality = trList.get(j).select("td").get(1).text();
                                System.out.println("Superficiality :" + superficiality);
                            } else if (j == 4) {
                                String price = trList.get(j).select("td").get(1).text();
                                System.out.println("Price :" + price);
                            } else if (j == 6) {
                                String postAt = trList.get(j).select("td").get(1).text();
                                System.out.println("post at :" + postAt);
                            }
                        }
                    }
/*                    String price = info.getElementsByClass("price").select("b").text();
                    String superficiality = info.getElementsByClass("clearfix").last().select("b").first().text();
                    String address = info.getElementsByClass("clearfix").last().select("b").last().text();
                    String district = address.split(",")[0].toString();
                    String postAt = info.getElementsByClass("time").select("span").text();*/
//                    System.out.println(name + " =  " + url + " =  " + price + " =  " + superficiality + " =  " + district + " =  " + postAt);
                }
            }
            System.out.println("Tổng là :" + sizeAll);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public List<SampleHostel> thuenhatro360() throws IOException, NumberFormatException{
        String mainUrl = "https://thuenhatro360.com/khu-vuc/ho-chi-minh";
        String changeUrl = "https://thuenhatro360.com";
        //biến hỗ trợ lưu sample hostel
        boolean flag = true;
        int itemCount = 0;
        List<SampleHostel> sampleHostelList = new ArrayList<>();
        //cào
        for (int pageSize = 1; pageSize < 2; pageSize++) {
            Document doc = Jsoup.connect(mainUrl+"?page="+pageSize).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            Elements urlList = doc.getElementsByClass("advert_hoz_item");
            for (Element url : urlList) {
                //mặc định biến cờ ban đầu
                flag = true;
                SampleHostel sampleHostel = new SampleHostel();
                //lấy id
                String idString = url.getElementsByClass("advert_hoz_item").attr("id");
                Pattern pa = Pattern.compile("[0-9]+");
                Matcher ma = pa.matcher(idString);
                if(ma.find()){
                    int id = Integer.parseInt(ma.group());
                    sampleHostel.setSampleHostelId(id);
//                System.out.println("ID : "+ id);
                }else{
                    flag = false;
                    System.out.println("Not Found id");
                }

                String link = changeUrl + url.select("a").attr("href");
//            System.out.println("Link: "+ link);
                Document linkConnect = Jsoup.connect(link).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                Elements linkConnectList = linkConnect.getElementsByClass("advert_detail");
                for (Element linkDetail : linkConnectList) {
                    //Lấy price
                    Elements priceList = linkDetail.select("h2");
                    String str = priceList.get(0).text();
                    //cắt chuỗi trong price
                    str = str.replaceAll("[^0-9,-\\.]", ",");
                    String[] item = str.split(",");
                    int count = 0;
                    String priceString = "";
                    for (int i = 0; i < item.length; i++) {
                        try {
                            Double.parseDouble(item[i]);
                            if(count==0){
                                priceString = priceString+String.valueOf(item[i]);
                                count ++;
                            }
                            else if(count==1){
                                priceString = priceString+"."+String.valueOf(item[i]);
                                count ++;
                            }
                            if(count == 2){
                                count = 0;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    double price = Double.parseDouble(priceString);
                    sampleHostel.setPrice(price);
//                System.out.println("Price: "+ price);
                    //Lấy superficiality, address, time
                    Elements allP = linkConnectList.select("p");
                    for (int i = 0 ; i < 6; i ++){
                        if(i == 2){
                            String allAddress = allP.get(i).text();
                            String[] addressList = allAddress.split(",");
//                        System.out.println("Size "+ addressList.length);
                            if(addressList.length >2) {
                                String street = addressList[0];
                                String ward = addressList[1];
                                String district = addressList[2];
                                sampleHostel.setStreetId(street + "/" + ward+ "/" + district);
//                            System.out.println("All address: " + street + ward + district);
                            }else{
                                flag = false;
                                break;
//                            System.out.println("NO SUITABLE");
                            }
                        }else if(i == 4){
                            String superficialityString = allP.get(i).select("span").text();
                            superficialityString =  superficialityString.replaceAll("[^0-9,-\\.]", ",");
                            String[] slist = superficialityString.split(",");
                            int superficiality = Integer.parseInt(slist[0]);
                            sampleHostel.setSuperficiality(superficiality);
//                        System.out.println("Superficiality: "+ superficiality);
                        }
                    }
                    // Lấy longitute và latitute
                    String location = linkConnect.select("amp-iframe").attr("src");

                    Pattern p = Pattern.compile("[0-9].[0-9,0-9.0-9]+");
                    Matcher m = p.matcher(location);
                    if(m.find()){
                        String[] locationList = m.group().split(",");
                        double longitude = Double.parseDouble(locationList[1]);
                        double latitude = Double.parseDouble(locationList[0]);
                        sampleHostel.setLongitude(longitude);
                        sampleHostel.setLatitude(latitude);
//                    System.out.println("Longitute : "+ longitude);
//                    System.out.println("Latitute : "+ latitude);
                    }else{
                        flag = false;
                        break;
//                    System.out.println("Not Found");
                    }

                    //lấy thiết bị và dịch vụ
                    Elements fasList = linkConnect.getElementsByClass("utilities").select("div>div>div>div");
//                    if(fasList.size()==0){
//                        flag = false;
//                        break;
////                    System.out.println("Not found fas");
//                    }else{
                        List<String> facilities = new ArrayList<>();
                        List<String> services = new ArrayList<>();
                        List<Integer> facilitiesID = new ArrayList<>();
                        List<Integer> serviceID = new ArrayList<>();
                        for (Element fas : fasList){
                            String checkActive = fas.attr("class").trim();
                            if(!checkActive.isEmpty()){
//                            System.out.println("Xem trạng thái: "+ checkActive);
                                if(checkActive.equals("utility")){
                                    String fasFinal = fas.select("p").text();
                                    if(fasFinal.equals("Đậu xe")){
                                        fasFinal = "Giữ xe";
                                        services.add(fasFinal);
//                                    System.out.println("Services : "+ fasFinal);
                                    }else if(fasFinal.equals("Wifi")) {
                                        fasFinal = "internet";
                                        services.add(fasFinal);
//                                    System.out.println("Services : "+ fasFinal);
                                    }else if(fasFinal.equals("Nhà tắm")){
                                        fasFinal = "WC riêng";
                                        facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                                    }else if(fasFinal.equals("Nhà bếp")){
                                        fasFinal = "Bếp gas";
                                        facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                                    }else if(fasFinal.equals("Điều hòa")){
                                        fasFinal = "Máy lạnh";
                                        facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                                    }else if(fasFinal.equals("Tủ lạnh") || fasFinal.equals("Máy giặt")){
                                        facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                                    }
                                }
                            }
                        }
//                        System.out.println("faciliti  "+ facilities);
//                        for(int f = 0 ; f < facilities.size(); f++){
//                            for (Facility facility : facilityList){
//                                System.out.println("Faci so sanh :"+ facilities.get(f));
//                                System.out.println("Faci dc so sanh :"+ facility.getName());
//                                if(facilities.get(f).equals(facility.getName())){
//                                    facilitiesID.add(facility.getId());
//                                }
//                            }
//                        }
                        sampleHostel.setServices(SampleHostelDAO.getAllServices(services));
                        sampleHostel.setFacilities(SampleHostelDAO.getAllFacilities(facilities));
//                        sampleHostel.setFacilities(facilitiesID);
//                        sampleHostel.setFacilities(facilities);
//                    }
                    //end
                }
                if(flag){
                    sampleHostelList.add(sampleHostel);
                    flag = true;
                    itemCount++;
                    System.out.println("Crawled: "+ itemCount+ " hostels");
                }else{
                    System.out.println("No suitable");
                }
            }//end for
        }
        //change string facility và string service thành int

        for(SampleHostel hostel : sampleHostelList){
            System.out.println("Hostel đạt chuẩn: " + hostel.toString());
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        }
        System.out.println("Tổng data đạt chuẩn: "+ sampleHostelList.size());
        return sampleHostelList;
    }

    public List<Sample> getSample() throws IOException, NumberFormatException{
        GetDocumentFromURL getDocumentFromURL = new GetDocumentFromURL();
        List<SampleHostel> sampleHostelList = getDocumentFromURL.thuenhatro360();
        //cái List sample hoàn chỉnh cần phải có-------------------------
        List<Sample> sampleList = new ArrayList<>();
        //---------------------------------------------------------------
        int streetId = 1;
        for (SampleHostel sampleHostel : sampleHostelList){
            Sample sample = new Sample();
            Street street =  new Street();
            //set id
            sample.setSampleHostelId(sampleHostel.getSampleHostelId());

            //set street id
            sample.setStreetId(streetId);
            street.setStreetId(streetId);
            street.setStreetName(sampleHostel.getStreetId().split("/")[0]);
//            street.setWarđId(SampleHostelDAO.);
            streetId++;

        }
        return sampleList;
    }

}