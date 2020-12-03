package main.java.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import main.java.entity.Mogi;
import main.java.entity.Phongtot;

import java.io.File;

public class JacksonObj {
    public Phongtot readYamlForPhongtot() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Phongtot phongtot = mapper.readValue(new File("src/main/resources/phongtot.yaml"), Phongtot.class);
        return phongtot;
    }
    public Mogi readYamlForMogi() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Mogi mogi = mapper.readValue(new File("src/main/resources/mogi.yaml"), Mogi.class);
        return mogi;
    }
}
