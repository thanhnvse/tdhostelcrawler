package main.java.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import main.java.entity.Mogi;
import main.java.entity.Phongtot;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JacksonObj {
    public Phongtot readYamlForPhongtot(){
        Phongtot phongtot = new Phongtot();
        try{
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            phongtot = mapper.readValue(new File("src/main/resources/phongtot.yaml"), Phongtot.class);
        }catch (Exception e){
            Logger.getLogger(JacksonObj.class.getName()).log(Level.SEVERE, null, e);
        }
        return phongtot;
    }
    public Mogi readYamlForMogi(){
        Mogi mogi = new Mogi();
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mogi = mapper.readValue(new File("src/main/resources/mogi.yaml"), Mogi.class);
        }catch (Exception e){
            Logger.getLogger(JacksonObj.class.getName()).log(Level.SEVERE, null, e);
        }
        return mogi;
    }
}
