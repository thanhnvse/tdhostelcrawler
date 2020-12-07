package main.java.dao;

import main.java.entity.*;
import main.java.util.DBUtil;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return facilitiesIdList;
    }
    public List<Facility> getAllFacilities() throws SQLException{
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
                Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
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

    public List<Street> getAllStreet (){
        List<Street> streetList = new ArrayList<>();
        String query = "select street_id, street_name from street";
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Street street = new Street();
                    street.setStreetId(rs.getInt("street_id"));
                    street.setStreetName(rs.getString("street_name"));
                    streetList.add(street);
                }
            }catch (Exception e){
                Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
            }
        return streetList;
    }

    public List<Ward> getAllWard (){
        List<Ward> wardList = new ArrayList<>();
        String query = "select ward_id, ward_name, district_id from ward";
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ward ward = new Ward();
                ward.setWardId(rs.getInt("ward_id"));
                ward.setWardName(rs.getString("ward_name"));
                ward.setDistrictId(rs.getInt("district_id"));
                wardList.add(ward);
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return wardList;
    }
    public List<District> getAllDistrict (){
        List<District> districtList = new ArrayList<>();
        String query = "select district_id, district_name from district";
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                District district = new District();
                district.setDistrictId(rs.getInt("district_id"));
                district.setDistrictName(rs.getString("district_name"));
                districtList.add(district);
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return districtList;
    }

    public void insertStreetWard(StreetWard streetWard) {
        String query = "INSERT INTO street_ward (ward_id, street_id) VALUES (?, ?)";
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {

            ps.setInt(1,streetWard.getWardId());
            ps.setInt(2,streetWard.getStreetId());

            int result = ps.executeUpdate();
            // check the affected rows
            if (result > 0) {
                System.out.println("INSERT STREET WARD SUCCESSFULLY");
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void insertSample(Sample sample) {
        String query = "INSERT INTO sample (facility_ids, latitude, longitude,post_at, price, service_ids, superficiality, street_ward_id, category_id) VALUES ( ?, ?, ?,?, ?, ?, ?, ?, ?)";
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            List<Integer> facilityInteger = sample.getFacilities();
            List<Integer> serviceInteger = sample.getServices();

            ps.setArray(1, c.createArrayOf("integer", facilityInteger.toArray()));
            ps.setDouble(2, sample.getLatitude());
            ps.setDouble(3, sample.getLongitude());
            ps.setLong(4, sample.getPostAt());
            ps.setDouble(5, sample.getPrice());
            ps.setArray(6, c.createArrayOf("integer", serviceInteger.toArray()));
            ps.setDouble(7,sample.getSuperficiality());
            ps.setInt(8,sample.getStreetId());
            ps.setInt(9,sample.getCategoryId());

            int result = ps.executeUpdate();
            // check the affected rows
            if (result > 0) {
                System.out.println("INSERT SAMPLE SUCCESSFULLY");
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean checkInsert(int wardId, int streetId) {
        String query = "SELECT ward_id, street_id from street_ward where ward_id = "+ wardId +" and street_id = " + streetId;
        try (Connection c = DBUtil.getConnectDB();
            Statement ps = c.createStatement();
            ResultSet rs = ps.executeQuery(query)){
            return rs.isBeforeFirst();
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Integer getStreetWardId (int wardId, int streetId){
        String query = "select street_ward_id from street_ward where ward_id = "+ wardId + "and street_id = "+ streetId;
        int streetWardId = 0;
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                streetWardId = rs.getInt("street_ward_id");
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return streetWardId;
    }

    public boolean checkInsertSample(double price, double superficiality, int streetWardId) {
        String query = "SELECT price, superficiality, street_ward_id from sample where price = '"+ price +"'and superficiality = '" + superficiality
                + "'and street_ward_id = " + streetWardId;
        try (Connection c = DBUtil.getConnectDB();
             Statement ps = c.createStatement();
             ResultSet rs = ps.executeQuery(query)){
            return rs.isBeforeFirst();
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public List<Ward> getAllWardByDistrictId(int districtId){
        List<Ward> wardNameList = new ArrayList<>();
        String query = "SELECT ward_id, ward_name from ward where district_id = " + districtId;
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int wardId = rs.getInt("ward_id");
                String wardName = rs.getString("ward_name");
                Ward ward = new Ward();
                ward.setWardId(wardId);
                ward.setWardName(wardName);
                ward.setDistrictId(districtId);
                wardNameList.add(ward);
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return wardNameList;
    }

    public long getThelastPostAt (){
        String query = "select max(post_at) from sample";
        long postAt = 0;
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                postAt = rs.getLong("max");
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return postAt;
    }

    public boolean checkSampleEmpty(){
        String query = "SELECT * from sample";
        try (Connection c = DBUtil.getConnectDB();
             Statement ps = c.createStatement();
             ResultSet rs = ps.executeQuery(query)){
            if(rs.next() == false){
                return true;
            }
        }catch (Exception e){
            Logger.getLogger(SampleHostelDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
}
