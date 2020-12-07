package main.java.process;

import main.java.entity.Facility;
import main.java.entity.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MogiProcess {
    public long getMillisecondFromPostAt(String time) throws ParseException {
        long millis = 0;
        if (time.toLowerCase().contains("hôm nay")) {
            millis = getMilisNow();
        } else if (time.toLowerCase().contains("hôm qua")) {
            millis = getMilisYesterday();
        } else {
            millis = getMilisOthers(time);
        }
        return millis;
    }

    public long getMilisNow() {
        long millis = 0;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String myDate = dtf.format(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse(myDate);
            System.out.println("Date :" + date);
            millis = date.getTime();
        } catch (Exception e) {
            Logger.getLogger(MogiProcess.class.getName()).log(Level.SEVERE, "Now time", e);
        }
        return millis;
    }

    public long getMilisYesterday() {
        long millis = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar c1 = Calendar.getInstance();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String myDate = dtf.format(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse(myDate);
            c1.setTime(date);
            c1.roll(Calendar.DATE, -1);
            date = sdf.parse(dateFormat.format(c1.getTime()));
            System.out.println("Date :" + date);
            millis = date.getTime();
        } catch (Exception e) {
            Logger.getLogger(MogiProcess.class.getName()).log(Level.SEVERE, "Yesterday time", e);
        }
        return millis;
    }

    public long getMilisOthers(String time) {
        long millis = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar c1 = Calendar.getInstance();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            c1.setTime(date);
            date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            Logger.getLogger(MogiProcess.class.getName()).log(Level.SEVERE, "Others time", e);
        }
        return millis;
    }
}
