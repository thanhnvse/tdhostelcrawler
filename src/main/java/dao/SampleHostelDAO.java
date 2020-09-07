package main.java.dao;

import main.java.entity.Facility;
import main.java.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SampleHostelDAO {

    public static List<Integer> getAllFacilities (List<String> facilitiesName){
        List<Integer> facilitiesIdList = new ArrayList<>();
        String query = "Select facility_id from facility where facility_name = ?";
        for (int i = 0 ;  i < facilitiesName.size(); i++){
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query)) {
                ps.setString(1, facilitiesName.get(i));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    facilitiesIdList.add(rs.getInt("facility_id"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return facilitiesIdList;
    }
//    public static List<Facility> getAllFacilities (){
//        List<Facility> facilitiesIdList = new ArrayList<>();
//        Facility facility = new Facility();
//        String query = "Select facility_id, facility_name from facility";
//        try(Connection c = DBUtil.getConnectDB();
//            PreparedStatement ps = c.prepareStatement(query)) {
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                facility.setId(rs.getInt("facility_id"));
//                facility.setName(rs.getString("facility_name"));
//                facilitiesIdList.add(facility);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return facilitiesIdList;
//    }
    public static List<Integer> getAllServices (List<String> servicesName){
        List<Integer> servicesIdList = new ArrayList<>();
        String query = "Select service_id from service where service_name = ?";
        for (int i = 0 ;  i < servicesName.size(); i++){
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query)) {
                ps.setString(1, servicesName.get(i));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    servicesIdList.add(rs.getInt("service_id"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return servicesIdList;
    }

    public static int getDistrictId (String districtName){
        List<Integer> servicesIdList = new ArrayList<>();
        String query = "Select district_id, district_name from district";
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    servicesIdList.add(rs.getInt("service_id"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        return 1;
    }



}
