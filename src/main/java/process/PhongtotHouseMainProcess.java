package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.entity.Sample;
import main.java.entity.StreetWard;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PhongtotHouseMainProcess {
    public void getPhongTotHouseMain(Element sampleElement){
        JacksonObj jacksonObj = new JacksonObj();
        PhongtotProcess phongtotProcess = new PhongtotProcess();
        PhongtotDataProcess phongtotDataProcess = new PhongtotDataProcess();
        PhongtotHouseDetailProcess phongtotHouseDetailProcess = new PhongtotHouseDetailProcess();
        boolean flag = true;
        Sample sample = new Sample();
        StreetWard streetWard = new StreetWard();
        String postTimeAnalysis = sampleElement.select(jacksonObj.readYamlForPhongtot().getPostTimeAnalysis()).text();
        long postAt = phongtotProcess.getMillisecondFromPostAtPhongTot(postTimeAnalysis);
        String[] addressList = sampleElement.select(jacksonObj.readYamlForPhongtot().getStreetName()).text().split("-");
        String district = "";
        if (addressList.length < 2) {
            flag = false;
        } else {
            district = phongtotDataProcess.getDistrictPhongTot(addressList);
        }
        int districtId = phongtotDataProcess.getWardIdAndDistrictIdPhongTot(addressList, district).getDistrictId();
        int wardId = phongtotDataProcess.getWardIdAndDistrictIdPhongTot(addressList, district).getWardId();
        streetWard.setWardId(wardId);
        if (districtId == 0 || wardId == 0) {
            flag = false;
        }
        String superString = sampleElement.select(jacksonObj.readYamlForPhongtot().getSuperString()).text();
        double superficiality = phongtotDataProcess.getSuperficiality(superString);
        sample.setSuperficiality(superficiality);
        String priceString = sampleElement.select(jacksonObj.readYamlForPhongtot().getPriceString()).text();
        double price = phongtotDataProcess.getPrice(priceString);
        if (price == 0) {
            flag = false;
        }
        sample.setPrice(price);
        sample.setPostAt(postAt);
        String detailUrl = sampleElement.select(jacksonObj.readYamlForPhongtot().getDetailUrl()).attr("href");
        Elements detailUrlList = phongtotDataProcess.readDetailUrlList(detailUrl);
        for (Element detailElement : detailUrlList) {
            phongtotHouseDetailProcess.getPhongTotHouseDetail(detailElement,flag,streetWard,sample,addressList,district);
        }
    }
}
