package main.java.crawler;

import main.java.dao.SampleHostelDAO;
import main.java.entity.Facility;
import main.java.entity.Sample;
import main.java.entity.SampleHostel;
import main.java.entity.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhongtotCrawler {
    public List<Sample> getSampleHostelDataFromPhongTot() throws IOException, SQLException, NamingException, ClassNotFoundException {
        List<Sample> sampleList = new ArrayList<>();
        SampleHostelDAO hostelDAO =  new SampleHostelDAO();
        List<Facility> facilityList = hostelDAO.getAllFacilities();
        List<Service> serviceList = hostelDAO.getAllServices();
        String mainUrl = "http://phongtot.vn/phong-tro-nha-tro?fill=79";
        for(int pageNumber = 0 ; pageNumber < 1; pageNumber++){
            Document phongtotDoc = Jsoup.connect(mainUrl+"&page="+pageNumber).timeout(10000).userAgent("Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            Elements urlList = phongtotDoc.getElementsByClass("room-item");
            for(Element sampleElement : urlList){
                Sample sample = new Sample();
                //street
                String streetName = sampleElement.getElementsByClass("block-room-item-address").select("a").text();
                System.out.println("Street Name all: "+ streetName);
                //ward
                String[] addressList = streetName.split("-");
                String ward = addressList[1];
                if (!ward.toLowerCase().contains("phường")){
                    ward = "Phường"+ addressList[1];
                }
                System.out.println("Ward :"+ ward);
                //district
                String district = addressList[2];
                if (!district.toLowerCase().contains("quận")){
                    district = "Quận"+ addressList[2];
                }
                System.out.println("District :"+ district);

                //dien tich
                String superString = sampleElement.getElementsByClass("pull-left").select("a").text();
                superString = superString.replaceAll("[^0-9]+", "/");
                String[] superItem = superString.split("/");
                double superficiality = Double.parseDouble(superItem[0]);
                System.out.println("Superficiality: "+ superficiality);
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
                System.out.println("Price: "+price);
                sample.setPrice(price);

                //chi tiet
                String detailUrl = sampleElement.getElementsByClass("block-room-item-title").select("a").attr("href");
                Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                        " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
                Elements detailUrlList = detailDoc.getElementsByClass("col-md-9");
                for(Element detailElement : detailUrlList){
//                    System.out.println(detailElement);
                    Elements streetList = detailElement.getElementsByClass("address").select("a");
                    for(int i = 0; i < streetList.size();i++){
                        String street = detailElement.getElementsByClass("address").select("a").get(1).text();
                        if (!street.toLowerCase().contains("đường")){
                            street = "Đường "+ detailElement.getElementsByClass("address").select("a").get(1).text();
                        }
                        System.out.println("Street name :"+ street);
                        break;
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
//                                    System.out.println("Services : "+ fasFinal);
                        }else if(fasFinal.equals("Internet")) {
                            fasFinal = "internet";
                            services.add(fasFinal);
//                                    System.out.println("Services : "+ fasFinal);
                        }else if(fasFinal.equals("Thang máy")) {
                            fasFinal = "thang máy";
                            services.add(fasFinal);
//                                    System.out.println("Services : "+ fasFinal);
                        }else if(fasFinal.equals("Điều hòa")){
                            fasFinal = "Máy lạnh";
                            facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                        }else if(fasFinal.equals("Bình nóng lạnh")) {
                            fasFinal = "Máy nước nóng";
                            facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                        }else if(fasFinal.equals("Máy giặt")){
                            facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                        }else if(fasFinal.equals("Tivi")){
                            facilities.add(fasFinal);
//                                    System.out.println("Facilities : "+ fasFinal);
                        }
                    }
//                    System.out.println("Size truoc khi get int la : "+ services.size());
                    List<Integer> facilityInteger = getFacilityIdFromFacilityName(facilities, facilityList);
                    List<Integer> serviceInteger = getServiceIdFromServiceName(services, serviceList);
                    sample.setFacilities(facilityInteger);
                    sample.setServices(serviceInteger);
                    for(Integer faci : facilityInteger){
                        System.out.println("Faci :" + faci);
                    }

                    for(Integer ser : serviceInteger){
                        System.out.println("Ser :" + ser);
                    }
                    //longitude, latitude
                    double latitude = Double.parseDouble(detailElement.getElementsByClass("gllpLatitude").attr("value"));
                    double longitude = Double.parseDouble(detailElement.getElementsByClass("gllpLongitude").attr("value"));
                    System.out.println("Latitude name :"+ latitude);
                    System.out.println("Longitude name :"+ longitude);
                    sample.setLatitude(latitude);
                    sample.setLongitude(longitude);
                    sample.setStreetId(1);
                    hostelDAO.insertSample(sample);
                    System.out.println("-------------------------------");
                }
            }
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
}
