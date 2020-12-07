package main.java;

import main.java.crawler.AllDataCrawler;

public class GetDocumentFromURL {
    public static void main(String[] args){
        AllDataCrawler allDataCrawler = new AllDataCrawler();
        allDataCrawler.crawlData();
    }
}