package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.Sample;
import main.java.entity.StreetWard;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PhongtotDetailProcess {
    public void getPhongTotDetail(Element detailElement, boolean flag, StreetWard streetWard, Sample sample
    , String[] addressList, String district){
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        JacksonObj jacksonObj = new JacksonObj();
        SampleProcess sampleProcess = new SampleProcess();
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        Elements streetList = detailElement.select(jacksonObj.readYamlForPhongtot().getStreetList());
        int streetId = phongtotDataProcess.getStreetIdPhongTot(streetList);
        streetWard.setStreetId(streetId);
        if (streetId == 0) {
            flag = false;
        }
        Elements faseList = detailElement.select(jacksonObj.readYamlForPhongtot().getFaseList());
        sample.setFacilities(phongtotDataProcess.getFaSeIds(faseList).getFacilityInteger());
        sample.setServices(phongtotDataProcess.getFaSeIds(faseList).getServiceInteger());
        String latLongCrawler = sampleProcess.getStreetCrawlerToGetLatLong(addressList[0], district,
                phongtotDataProcess.getWardIdAndDistrictIdPhongTot(addressList, district).getWard());
        if (!latLongCrawler.isEmpty()) {
            sample.setLatitude(phongtotDataProcess.getLatitude(latLongCrawler));
            sample.setLongitude(phongtotDataProcess.getLongitude(latLongCrawler));
        }
//        System.out.println("Street id 1:"+ phongtotDataProcess.getStreetIdPhongTot(streetList));
//        System.out.println("Street id 2:"+ streetWard.getStreetId() + "Ward id : "+ streetWard.getWardId());
        System.out.println("Result check insert street ward (true = ko vao): " + hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId()));
        if (flag) {
            if (!hostelDAO.checkInsert(streetWard.getWardId(), streetWard.getStreetId())) {
                hostelDAO.insertStreetWard(streetWard);
            }
        }
        sample.setStreetId(hostelDAO.getStreetWardId(streetWard.getWardId(), streetWard.getStreetId()));
        sample.setCategoryId(1);
        System.out.println("Sample : " + sample.toString());
        if (flag) {
            if (!hostelDAO.checkInsertSample(sample.getPrice(), sample.getSuperficiality(), sample.getStreetId())) {
                hostelDAO.insertSample(sample);
            }
        }
    }
}
