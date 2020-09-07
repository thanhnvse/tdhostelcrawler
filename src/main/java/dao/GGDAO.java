package main.java.dao;

import main.java.entity.UCategory;
import main.java.entity.UType;
import main.java.entity.Utility;
import main.java.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.java.constants.StaticPlaceType.*;

public class GGDAO {
    public List<UCategory> createUCategoryList() {
        List<UCategory> categoryList = new ArrayList<>();
        categoryList.add(new UCategory(1,"market","chợ"));
        categoryList.add(new UCategory(2,"school","trường học"));
        categoryList.add(new UCategory(3,"bus","trạm xe buýt"));
        categoryList.add(new UCategory(4,"atm","ngân hàng"));
        categoryList.add(new UCategory(5,"hospital","bệnh viện"));
        return categoryList;
    }

    public List<UType> createUTypeList() {
        List<UType> uTypeList = new ArrayList<>();
        uTypeList.add(new UType(1, 1, CONVENIENCE_STORE_VN, 1));
        uTypeList.add(new UType(2, 1, SUPERMARKET_VN, 1));
        uTypeList.add(new UType(3, 1, DEPARTMENT_STORE_VN, 1));
        uTypeList.add(new UType(4, 2, UNIVERSITY_VN, 2));
        uTypeList.add(new UType(5, 2, SCHOOL_VN, 2));
        uTypeList.add(new UType(6, 2, PRIMARY_SCHOOL_VN, 2));
        uTypeList.add(new UType(7, 2, SECONDARY_SCHOOL_VN, 2));
        uTypeList.add(new UType(8, 3, BUS_STATION_VN, 3));
        uTypeList.add(new UType(9, 4, ATM_VN, 4));
        uTypeList.add(new UType(10, 4, BANK_VN, 4));
        uTypeList.add(new UType(11, 5, HOSPITAL_VN, 5));
        uTypeList.add(new UType(12, 5, DRUGSTORE_VN, 5));
        uTypeList.add(new UType(13, 5, PHARMACY_VN, 5));
        return uTypeList;
    }

    public void insertUCategoryList() {
        List<UCategory> categoryList = createUCategoryList();
        for(UCategory uCategory : categoryList){
            String query = "INSERT INTO u_category (name, code) VALUES (?, ?)";
            long id = 0;
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, uCategory.getName());
                ps.setString(2, uCategory.getCode());

                int affectedRows = ps.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    // get the ID back
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            id = rs.getLong(1);
                            System.out.println("INSERT SUCCESSFULLY");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getUtilityIdFromAUType(String name){
        int typeId=0;
        if(name.equals(CONVENIENCE_STORE)){
            typeId = 1;
        }else if(name.equals(SUPERMARKET)){
            typeId = 2;
        }else if(name.equals(DEPARTMENT_STORE)){
            typeId = 3;
        }else if(name.equals(UNIVERSITY)){
            typeId = 4;
        }else if(name.equals(SCHOOL)){
            typeId = 5;
        }else if(name.equals(PRIMARY_SCHOOL)){
            typeId = 6;
        }else if(name.equals(SECONDARY_SCHOOL)){
            typeId = 7;
        }else if(name.equals(BUS_STATION)){
            typeId = 8;
        }else if(name.equals(ATM)){
            typeId = 9;
        }else if(name.equals(BANK)){
            typeId = 10;
        }else if(name.equals(HOSPITAL)){
            typeId = 11;
        }else if(name.equals(DRUGSTORE)){
            typeId = 12;
        }else if(name.equals(PHARMACY)){
            typeId = 13;
        }
        return typeId;
    }

    public void insertAUType() {
        List<UType> uTypeList = new ArrayList<>();
        uTypeList = createUTypeList();
        for(UType uType : uTypeList){
            String query = "INSERT INTO u_type (u_type_id, display_order, name, u_category_id) VALUES (?, ?, ?, ?)";
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, uType.getTypeId());
                ps.setInt(2, uType.getDisplayOrder());
                ps.setString(3, uType.getName());
                ps.setInt(4, uType.getCategoryId());

                int result = ps.executeUpdate();
                // check the affected rows
                if (result > 0) {
                    System.out.println("INSERT TYPE SUCCESSFULLY");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void insertAUtility(Utility utility) {
        String query = "INSERT INTO utility (latitude, longitude, name, u_type_id) VALUES ( ?, ?, ?, ?)";
        long id = 0;
        try(Connection c = DBUtil.getConnectDB();
            PreparedStatement ps = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, utility.getLatitude());
            ps.setDouble(2, utility.getLongitude());
            ps.setString(3, utility.getName());
            ps.setInt(4, utility.getTypeId());

            int result = ps.executeUpdate();
            // check the affected rows
            if (result > 0) {
                // get the ID back
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                        System.out.println("INSERT UTILITY SUCCESSFULLY");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
