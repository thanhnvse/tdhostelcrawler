package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.Sample;
import main.java.entity.StreetWard;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MogiDetailProcess {
    private JacksonObj jacksonObj = new JacksonObj();
    private MogiProcess mogiProcess = new MogiProcess();
    private SampleHostelDAO hostelDAO = new SampleHostelDAO();
    private SampleProcess sampleProcess = new SampleProcess();
    private MogiDataProcess mogiDataProcess = new MogiDataProcess();

    public void getMogiInfo(Element sampleElement, Sample sample, boolean flag, StreetWard streetWard) {
        try {
            String streetName = sampleElement.select(jacksonObj.readYamlForMogi().getStreetName()).text();
            String[] addressList = streetName.split(",");
            String district = "";
            if (addressList.length < 2) {
                flag = false;
            } else {
                district = mogiDataProcess.getDistrict(addressList);
            }
            int districtId = mogiDataProcess.getWardIdAndDistrictId(addressList, district).getDistrictId();
            int wardId = mogiDataProcess.getWardIdAndDistrictId(addressList, district).getWardId();
            streetWard.setWardId(wardId);
            if (districtId == 0 || wardId == 0) {
                flag = false;
            }
            int streetId = mogiDataProcess.getStreetId(addressList);
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
            String postTimeAnalysis = sampleElement.select(jacksonObj.readYamlForMogi().getPostTimeAnalysis()).text();
            postTimeAnalysis = mogiDataProcess.getPostAt(postTimeAnalysis);
            long postAt = mogiProcess.getMillisecondFromPostAt(postTimeAnalysis);
            sample.setPostAt(postAt);
            String latLongCrawler = sampleProcess.getStreetCrawlerToGetLatLong(addressList[0], district
                    , mogiDataProcess.getWardIdAndDistrictId(addressList, district).getWard());
            if (!latLongCrawler.isEmpty()) {
                sample.setLatitude(mogiDataProcess.getLatitude(latLongCrawler));
                sample.setLongitude(mogiDataProcess.getLongitude(latLongCrawler));
            }
            getMogiDetail(sampleElement, sample, flag, streetWard);
        } catch (Exception e) {
            Logger.getLogger(MogiDetailProcess.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void getMogiDetail(Element sampleElement, Sample sample, boolean flag, StreetWard streetWard) {
        try {
            String detailUrl = sampleElement.select(jacksonObj.readYamlForMogi().getDetailUrl()).attr("href");
            Document detailDoc = Jsoup.connect(detailUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36").get();
            String description = detailDoc.select(jacksonObj.readYamlForMogi().getDescription()).text().toLowerCase();
            sample.setFacilities(mogiDataProcess.getFaSeIds(description).getFacilityInteger());
            sample.setServices(mogiDataProcess.getFaSeIds(description).getServiceInteger());
            if(flag) {
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
        } catch (Exception e) {
            Logger.getLogger(MogiDetailProcess.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
