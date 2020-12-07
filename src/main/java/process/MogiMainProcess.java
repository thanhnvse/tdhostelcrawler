package main.java.process;

import main.java.JSONParser.JacksonObj;
import main.java.entity.Sample;
import main.java.entity.StreetWard;
import org.jsoup.nodes.Element;

public class MogiMainProcess {
    public void getMogiMain(Element sampleElement){
        JacksonObj  jacksonObj = new JacksonObj();
        MogiDetailProcess mogiDetailProcess = new MogiDetailProcess();
        boolean flag = true;
        if (sampleElement.hasClass("bath") || sampleElement.hasClass("bed") || sampleElement.hasClass("land")) {
            flag = false;
        } else {
            Sample sample = new Sample();
            StreetWard streetWard = new StreetWard();
            String name = sampleElement.select(jacksonObj.readYamlForMogi().getName()).text().toLowerCase();
            if (!name.contains("căn hộ")) {
                mogiDetailProcess.getMogiInfo(sampleElement,sample, flag, streetWard);
            }
        }
    }
}
