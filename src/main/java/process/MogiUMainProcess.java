package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.dao.SampleHostelDAO;
import main.java.entity.Sample;
import main.java.entity.StreetWard;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MogiUMainProcess {
    public void getMogiUMain(Element sampleElement, boolean flag){
        JacksonObj jacksonObj = new JacksonObj();
        MogiDetailProcess mogiDetailProcess = new MogiDetailProcess();

        Sample sample = new Sample();
        StreetWard streetWard = new StreetWard();
        String name = sampleElement.select(jacksonObj.readYamlForMogi().getName()).text().toLowerCase();
        System.out.println(name);
        if (!name.contains("căn hộ")) {
            mogiDetailProcess.getMogiInfo(sampleElement,sample, flag, streetWard);
        }
    }
}
