package main.java.dao;

import main.java.entity.*;
import main.java.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {
    public void insertDistrict(District district) {
        String query = "INSERT INTO district (district_id, district_name, province_id) VALUES (?, ?, ?)";
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, district.getDistrictId());
            ps.setString(2, district.getDistrictName());
            ps.setInt(3, district.getProvinceId());

            int result = ps.executeUpdate();
            // check the affected rows
            if (result > 0) {
                System.out.println("INSERT DISTRICT SUCCESSFULLY");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void insertWard(Ward ward) {
        String query = "INSERT INTO ward (ward_id, ward_name, district_id) VALUES (?, ?, ?)";
        long id = 0;
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, ward.getWardId());
            ps.setString(2, ward.getWardName());
            ps.setInt(3, ward.getDistrictId());

            int result = ps.executeUpdate();
            // check the affected rows
            if (result > 0) {
                System.out.println("INSERT WARD SUCCESSFULLY");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertStreet(Street street) {
        String query = "INSERT INTO street (street_id, street_name) VALUES (?, ?)";
        long id = 0;
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, street.getStreetId());
            ps.setString(2, street.getStreetName());

            int result = ps.executeUpdate();
            // check the affected rows
            if (result > 0) {
                System.out.println("INSERT STREET SUCCESSFULLY");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
