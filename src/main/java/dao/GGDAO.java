package main.java.dao;

import main.java.entity.*;
import main.java.util.DBUtil;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static main.java.constants.LocationLatLong.*;
import static main.java.constants.StaticPlaceType.*;

public class GGDAO {
    public List<UCategory> createUCategoryList() {
        List<UCategory> categoryList = new ArrayList<>();
        categoryList.add(new UCategory(1,1,"market","chợ"));
        categoryList.add(new UCategory(2,2,"school","trường học"));
        categoryList.add(new UCategory(3,3,"bus","trạm xe buýt"));
        categoryList.add(new UCategory(4,4,"atm","ngân hàng"));
        categoryList.add(new UCategory(5,5,"hospital","bệnh viện"));
        return categoryList;
    }

    public List<UType> createUTypeList() {
        List<UType> uTypeList = new ArrayList<>();
        uTypeList.add(new UType(1, CONVENIENCE_STORE_VN, 1));
        uTypeList.add(new UType(2, SUPERMARKET_VN, 1));
//        uTypeList.add(new UType(3, DEPARTMENT_STORE_VN, 1));
        uTypeList.add(new UType(3, UNIVERSITY_VN, 2));
//        uTypeList.add(new UType(4, SCHOOL_VN, 2));
        uTypeList.add(new UType(4, PRIMARY_SCHOOL_VN, 2));
        uTypeList.add(new UType(5, SECONDARY_SCHOOL_VN, 2));
        uTypeList.add(new UType(6, BUS_STATION_VN, 3));
        uTypeList.add(new UType(7, ATM_VN, 4));
        uTypeList.add(new UType(8, BANK_VN, 4));
        uTypeList.add(new UType(9,  HOSPITAL_VN, 5));
        uTypeList.add(new UType(10, DRUGSTORE_VN, 5));
//        uTypeList.add(new UType(13, PHARMACY_VN, 5));
        return uTypeList;
    }

    public List<String> createTypeListToCrawl() {
        List<String> uTypeList = new ArrayList<>();
        uTypeList.add(CONVENIENCE_STORE);
        uTypeList.add(SUPERMARKET);
//        uTypeList.add(DEPARTMENT_STORE);
        uTypeList.add(UNIVERSITY);
//        uTypeList.add(SCHOOL);
        uTypeList.add(PRIMARY_SCHOOL);
        uTypeList.add(SECONDARY_SCHOOL);
        uTypeList.add(BUS_STATION);
        uTypeList.add(ATM);
        uTypeList.add(BANK);
        uTypeList.add(HOSPITAL);
        uTypeList.add(DRUGSTORE);
//        uTypeList.add(PHARMACY);
        return uTypeList;
    }

    public void insertUCategoryList() {
        List<UCategory> categoryList = createUCategoryList();
        for(UCategory uCategory : categoryList){
            String query = "INSERT INTO u_category (u_category_id, display_order, name, code) VALUES (?, ?, ?, ?)";
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query)) {
                ps.setInt(1, uCategory.getCategoryId());
                ps.setInt(2, uCategory.getDisplay_order());
                ps.setString(3, uCategory.getName());
                ps.setString(4, uCategory.getCode());

                int affectedRows = ps.executeUpdate();
                // check the affected rows
                if (affectedRows > 0) {
                    System.out.println("INSERT SUCCESSFULLY");
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
        }else if(name.equals(UNIVERSITY)){
            typeId = 3;
        }else if(name.equals(PRIMARY_SCHOOL)){
            typeId = 4;
        }else if(name.equals(SECONDARY_SCHOOL)){
            typeId = 5;
        }else if(name.equals(BUS_STATION)){
            typeId = 6;
        }else if(name.equals(ATM)){
            typeId = 7;
        }else if(name.equals(BANK)){
            typeId = 8;
        }else if(name.equals(HOSPITAL)){
            typeId = 9;
        }else if(name.equals(DRUGSTORE)){
            typeId = 10;
        }
        return typeId;
    }

    public void insertAUType() {
        List<UType> uTypeList = new ArrayList<>();
        uTypeList = createUTypeList();
        for(UType uType : uTypeList){
            String query = "INSERT INTO u_type (u_type_id, name, u_category_id) VALUES (?, ?, ?)";
            try(Connection c = DBUtil.getConnectDB();
                PreparedStatement ps = c.prepareStatement(query)) {
                ps.setInt(1, uType.getTypeId());
                ps.setString(2, uType.getName());
                ps.setInt(3, uType.getCategoryId());

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
    public boolean checkUCategoryEmpty(){
        String query = "SELECT * from u_category";
        try (Connection c = DBUtil.getConnectDB();
             Statement ps = c.createStatement();
             ResultSet rs = ps.executeQuery(query)){
            if(rs.next() == false){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUTypeEmpty(){
        String query = "SELECT * from u_type";
        try (Connection c = DBUtil.getConnectDB();
             Statement ps = c.createStatement();
             ResultSet rs = ps.executeQuery(query)){
            if(rs.next() == false){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkInsert(Double latitude, Double longitude, String name, int typeId) {
        if(name.contains("'")){
            String nameEqual = name;
            name = nameEqual.replace("'","''");
        }
        String query = "SELECT latitude, longitude, name, u_type_id from utility where latitude = '"+ latitude +"' and longitude = '" + longitude
                +"' and name = '" + name +"' and u_type_id = " + typeId;
        try (Connection c = DBUtil.getConnectDB();
             Statement ps = c.createStatement();
             ResultSet rs = ps.executeQuery(query)){
            return rs.isBeforeFirst();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<Location> getLocationList(){
        List<Location> locationList = new ArrayList<>();

        Location location1 = new Location();
        location1.setLatitude(QUAN1_LAT);
        location1.setLongitude(QUAN1_LONG);
        locationList.add(location1);

        Location location2 = new Location();
        location2.setLatitude(QUAN2_LAT);
        location2.setLongitude(QUAN2_LONG);
        locationList.add(location2);

        Location location3 = new Location();
        location3.setLatitude(QUAN3_LAT);
        location3.setLongitude(QUAN3_LONG);
        locationList.add(location3);

        Location location4 = new Location();
        location4.setLatitude(QUAN4_LAT);
        location4.setLongitude(QUAN4_LONG);
        locationList.add(location4);

        Location location5 = new Location();
        location5.setLatitude(QUAN5_LAT);
        location5.setLongitude(QUAN5_LONG);
        locationList.add(location5);

        Location location6 = new Location();
        location6.setLatitude(QUAN6_LAT);
        location6.setLongitude(QUAN6_LONG);
        locationList.add(location6);

        Location location7 = new Location();
        location7.setLatitude(QUAN7_LAT);
        location7.setLongitude(QUAN7_LONG);
        locationList.add(location7);

        Location location8 = new Location();
        location8.setLatitude(QUAN8_LAT);
        location8.setLongitude(QUAN8_LONG);
        locationList.add(location8);

        Location location9 = new Location();
        location9.setLatitude(QUAN9_LAT);
        location9.setLongitude(QUAN9_LONG);
        locationList.add(location9);

        Location location10 = new Location();
        location10.setLatitude(QUAN10_LAT);
        location10.setLongitude(QUAN10_LONG);
        locationList.add(location10);

        Location location11 = new Location();
        location11.setLatitude(QUAN11_LAT);
        location11.setLongitude(QUAN11_LONG);
        locationList.add(location11);

        Location location12 = new Location();
        location12.setLatitude(QUAN12_LAT);
        location12.setLongitude(QUAN12_LONG);
        locationList.add(location12);

        Location locationBinhChanh = new Location();
        locationBinhChanh.setLatitude(HUYENBINHCHANH_LAT);
        locationBinhChanh.setLongitude(HUYENBINHCHANH_LONG);
        locationList.add(locationBinhChanh);


        Location locationCanGio = new Location();
        locationCanGio.setLatitude(HUYENCANGIO_LAT);
        locationCanGio.setLongitude(HUYENCANGIO_LONG);
        locationList.add(locationCanGio);

        Location locationCuChi = new Location();
        locationCuChi.setLatitude(HUYENCUCHI_LAT);
        locationCuChi.setLongitude(HUYENCUCHI_LONG);
        locationList.add(locationCuChi);

        Location locationHocMon = new Location();
        locationHocMon.setLatitude(HUYENHOCMON_LAT);
        locationHocMon.setLongitude(HUYENHOCMON_LONG);
        locationList.add(locationHocMon);

        Location locationNhaBe = new Location();
        locationNhaBe.setLatitude(HUYENNHABE_LAT);
        locationNhaBe.setLongitude(HUYENNHABE_LONG);
        locationList.add(locationNhaBe);

        Location locationBinhThanh = new Location();
        locationBinhThanh.setLatitude(QUANBINHTHANH_LAT);
        locationBinhThanh.setLongitude(QUANBINHTHANH_LONG);
        locationList.add(locationBinhThanh);

        Location locationBinhTan = new Location();
        locationBinhTan.setLatitude(QUANBINHTAN_LAT);
        locationBinhTan.setLongitude(QUANBINHTAN_LONG);
        locationList.add(locationBinhTan);

        Location locationGoVap = new Location();
        locationGoVap.setLatitude(QUANGOVAP_LAT);
        locationGoVap.setLongitude(QUANGOVAP_LONG);
        locationList.add(locationGoVap);

        Location locationPhuNhuan = new Location();
        locationPhuNhuan.setLatitude(QUANPHUNHUAN_LAT);
        locationPhuNhuan.setLongitude(QUANPHUNHUAN_LONG);
        locationList.add(locationPhuNhuan);

        Location locationThuDuc = new Location();
        locationThuDuc.setLatitude(QUANTHUDUC_LAT);
        locationThuDuc.setLongitude(QUANTHUDUC_LONG);
        locationList.add(locationThuDuc);

        Location locationTanBinh = new Location();
        locationTanBinh.setLatitude(QUANTANBINH_LAT);
        locationTanBinh.setLongitude(QUANTANBINH_LONG);
        locationList.add(locationTanBinh);

        Location locationTanPhu = new Location();
        locationTanPhu.setLatitude(QUANTANPHU_LAT);
        locationTanPhu.setLongitude(QUANTANPHU_LONG);
        locationList.add(locationTanPhu);

        return locationList;
    }

    public boolean checkSchool(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty")){
            check = false;
        }
        if(nameLower.contains("thcs") || nameLower.contains("thpt") || nameLower.contains("trung học cơ sở") || nameLower.contains("trung học phổ thông")
        || nameLower.contains("phổ thông") || nameLower.contains("secondary")){
            check =  true;
        }
        return check;
    }

    public boolean checkSupermarket(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty") || nameLower.contains("tạp hóa")){
            check = false;;
        }
        else if(nameLower.contains("vissan") || nameLower.contains("bách hóa xanh") || nameLower.contains("mart") || nameLower.contains("vinmart")
                || nameLower.contains("co.op") || nameLower.contains("vin") || nameLower.contains("satra") || nameLower.contains("siêu thị")
                || nameLower.contains("aeon")){
            check = true;
        }
        return check;
    }
    public boolean checkConvenienceStore(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty") || nameLower.contains("tạp hóa")){
            check = false;
        }
        else if(nameLower.contains("vissan") || nameLower.contains("bách hóa xanh") || nameLower.contains("mart") || nameLower.contains("vinmart")
                || nameLower.contains("co.op") || nameLower.contains("vin") || nameLower.contains("satra") || nameLower.contains("siêu thị")
                || nameLower.contains("ministop") || nameLower.contains("circle k") || nameLower.contains("gs25") || nameLower.contains("eleven")){
            check = true;
        }
        return check;
    }
    public boolean checkPrimary(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty") || nameLower.contains("mầm non")){
            check = false;
        }
        else if(nameLower.contains("tiểu học") || nameLower.contains("quốc tế")){
            check = true;
        }
        return check;
    }
    public boolean checkSecondary(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty")){
            check = false;
        }
        else if(nameLower.contains("thpt") || nameLower.contains("trung học phổ thông") || nameLower.contains("thcs")
                || nameLower.contains("trung học cơ sở") || nameLower.contains("phổ thông") || nameLower.contains("pt")
                || nameLower.contains("quốc tế")){
            check = true;
        }
        return check;
    }
    public boolean checkUniversity(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty")){
            check = false;
        }
        else if(nameLower.contains("đại học") || nameLower.contains("học viện") || nameLower.contains("hv")
                || nameLower.contains("university") || nameLower.contains("cao đẳng") || nameLower.contains("cđ")
                || nameLower.contains("trung cấp")){
            check = true;
        }
        return check;
    }
    public boolean checkBank(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty")){
            check = false;
        }
        else {
            check = true;
        }
        return check;
    }
    public boolean checkHospital(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("bệnh viện") || nameLower.contains("nha khoa") || nameLower.contains("phòng khám")
                || nameLower.contains("trung tâm y tế") || nameLower.contains("hospital") || nameLower.contains("trạm y tế")
                || nameLower.contains("trung tâm xét nghiệm") || nameLower.contains("medical")){
            check = true;
        }
        return check;
    }

    public boolean checkDrugStore(String name){
        String nameLower = name.toLowerCase();
        boolean check = false;
        if(nameLower.contains("công ty")){
            check = false;
        }
        else if(nameLower.contains("nhà thuốc") || nameLower.contains("drug store")
                || nameLower.contains("thú y") || nameLower.contains("đông y")){
            check = true;
        }
        return check;
    }
}
