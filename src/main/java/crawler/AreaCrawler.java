package main.java.crawler;

import main.java.dao.AreaDAO;
import main.java.entity.District;
import main.java.entity.Street;
import main.java.entity.Ward;
import main.java.process.AreaProcess;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AreaCrawler {
    public void getSGApiInfo(boolean districtFlag, boolean wardFlag, boolean streetFlag) {
        AreaProcess areaProcess = new AreaProcess();
        District districtDTO;
        AreaDAO areaDAO = new AreaDAO();
        List<String> streetListAll = new ArrayList<>();
        JSONArray districtList = areaProcess.readJsonFile();
        int id = 0;
        for (int i = 0; i < districtList.length(); i++) {
            id++;
            districtDTO = areaProcess.setDataForDistrictFromFile(districtList, i, id);
            if (districtFlag) {
                areaDAO.insertDistrict(districtDTO);
            }
            if (wardFlag) {
                areaProcess.setDataForWardFromFile(districtList, i, id);
            }
            if (streetFlag) {
                streetListAll = areaProcess.setDataForStreetFromFile(districtList, i);
            }
        }
        //delete duplicate value
        if (streetFlag) {
            areaProcess.checkDuplicateAndInsertStreet(streetListAll);
        }
    }

    public void getSGAPIInFor() {
        AreaDAO areaDAO = new AreaDAO();
        if (areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()) {
            getSGApiInfo(true, true, true);
        } else if (areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()) {
            getSGApiInfo(true, false, true);
        } else if (areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()) {
            getSGApiInfo(true, true, false);
        } else if (areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()) {
            getSGApiInfo(true, false, false);
        } else if (!areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()) {
            getSGApiInfo(false, false, false);
        } else if (!areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()) {
            getSGApiInfo(false, true, true);
        } else if (!areaDAO.checkDistrictEmpty() && !areaDAO.checkWardEmpty() && areaDAO.checkStreetEmpty()) {
            getSGApiInfo(false, false, true);
        } else if (!areaDAO.checkDistrictEmpty() && areaDAO.checkWardEmpty() && !areaDAO.checkStreetEmpty()) {
            getSGApiInfo(false, true, false);
        }
    }
}
