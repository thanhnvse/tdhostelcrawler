package main.java.crawler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;

public class AreaCrawler {
    public void getSGApiInfo(){
        try {
            String fileName = "sg.json";
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),
                    "UTF8"));
            String str = in.readLine();
            JSONObject obj = new JSONObject(str);
            //district
            JSONArray districtList = obj.getJSONArray("district");
            System.out.println("District size: "+ districtList.length());
            for(int i = 0; i < districtList.length(); i++) {
                String district = districtList.getJSONObject(i).get("name").toString();
                System.out.println("District : " + district);
                //ward
                JSONArray wardList = districtList.getJSONObject(i).getJSONArray("ward");
                System.out.println("Ward size : "+wardList.length());
                for(int j = 0; j < wardList.length(); j++){
                    String ward = wardList.getJSONObject(j).get("name").toString();
                    System.out.println("Ward : "+ ward);
                }
                //street
                JSONArray streetList = districtList.getJSONObject(i).getJSONArray("street");
                System.out.println("Street size : "+streetList.length());
                for(int k = 0; k < streetList.length(); k++){
                    String street = streetList.get(k).toString();
                    System.out.println("Street : "+ street);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
