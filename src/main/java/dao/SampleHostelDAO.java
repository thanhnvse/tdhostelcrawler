package main.java.dao;

import main.java.entity.Facility;
import main.java.entity.Service;
import main.java.util.DBUtil;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<Facility> getAllFacilities() throws ClassNotFoundException, SQLException, NamingException {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<Facility> facilitiesIdList = null;
        try {
            con = DBUtil.getConnectDB();
            if (con != null) {
                String sql = "Select facility_id, facility_name from facility";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                facilitiesIdList = new ArrayList<>();
                while (rs.next()) {
                    Facility facility = new Facility();
                    facility.setId(rs.getInt("facility_id"));
                    facility.setName(rs.getString("facility_name"));
                    facilitiesIdList.add(facility);
                }
                return facilitiesIdList;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return facilitiesIdList;
    }
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

    public List<Service> getAllServices() throws ClassNotFoundException, SQLException, NamingException {
        Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<Service> servicesIdList = null;
        try {
            con = DBUtil.getConnectDB();
            if (con != null) {
                String sql = "Select service_id, service_name from service";
                stm = con.prepareStatement(sql);
                rs = stm.executeQuery();
                servicesIdList = new ArrayList<>();
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("service_id"));
                    service.setName(rs.getString("service_name"));
                    servicesIdList.add(service);
                }
                return servicesIdList;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (con != null) {
                con.close();
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
