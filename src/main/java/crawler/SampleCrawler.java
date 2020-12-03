package main.java.crawler;

import main.java.dao.SampleHostelDAO;

public class SampleCrawler {
    public void getSample() {
        SampleHostelDAO hostelDAO = new SampleHostelDAO();
        if (hostelDAO.checkSampleEmpty()) {
            PhongtotCrawler phongtotCrawler = new PhongtotCrawler();
            phongtotCrawler.getSampleHostelDataFromPhongTot();
            System.out.println("-------------------------------------------------");
            PhongtotHouseCrawler phongtotHouseCrawler = new PhongtotHouseCrawler();
            phongtotHouseCrawler.getSampleHostelDataFromPhongTot();
            System.out.println("-------------------------------------------------");
            MogiCrawler mogiCrawler = new MogiCrawler();
            mogiCrawler.getSampleHostelDataFromMogi();
        } else {
            PhongtotUCrawler phongtotUCrawler = new PhongtotUCrawler();
            phongtotUCrawler.getSampleHostelDataFromPhongTot();
            System.out.println("-------------------------------------------------");
            PhongtotHouseUCrawler phongtotHouseUCrawler = new PhongtotHouseUCrawler();
            phongtotHouseUCrawler.getSampleHostelDataFromPhongTot();
            System.out.println("-------------------------------------------------");
            MogiUCrawler mogiUCrawler = new MogiUCrawler();
            mogiUCrawler.getSampleHostelDataFromMogi();
        }
    }
}
