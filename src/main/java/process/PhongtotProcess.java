package main.java.process;

import main.java.entity.Facility;
import main.java.entity.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class PhongtotProcess {
    public long getMillisecondFromPostAtPhongTot(String time) {
        String[] timeList = time.split(" ");
        String postAt = timeList[1].toLowerCase();
        long millis = 0;
        try {
            if (postAt.toLowerCase().contains("phút") || postAt.toLowerCase().contains("giờ")) {
                millis = getMilisNow();
            } else {
                millis = getMilisOthers(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMilisOthers(String time) {
        String[] timeList = time.split(" ");
        int number = Integer.parseInt(timeList[0]);
        String postAt = timeList[1].toLowerCase();
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
            if ((postAt.toLowerCase().contains("ngày"))) {
                if (now.getDayOfMonth() - number > 0) {
                    millis = getMillisDay(c1,number,sdf,dateFormat);
                } else {
                    millis = getMillisDayTheOther(c1,number,sdf,dateFormat);
                }
            } else if ((postAt.toLowerCase().contains("tuần"))) {
                if (now.getDayOfMonth() - (7 * number) > 0) {
                    millis = getMillisWeek(c1,number,sdf,dateFormat);
                } else {
                    millis = getMillisWeekTheOther(c1,number,sdf,dateFormat);
                }
            } else if ((postAt.toLowerCase().contains("tháng"))) {
                if (now.getMonthValue() - number > 0) {
                    millis = getMillisMonth(c1,number,sdf,dateFormat);
                } else {
                    millis = getMillisMonthTheOther(c1,number,sdf,dateFormat);
                }
            } else if ((postAt.toLowerCase().contains("năm"))) {
                millis = getMillisYear(c1,number,sdf,dateFormat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisDay(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.DATE, -number);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisDayTheOther(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.DATE, -number);
            c1.roll(Calendar.MONTH, -1);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisWeek(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.DATE, -7 * number);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisWeekTheOther(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.DATE, -7 * number);
            c1.roll(Calendar.MONTH, -1);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisMonth(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.MONTH, -number);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisMonthTheOther(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.MONTH, -number);
            c1.roll(Calendar.YEAR, -1);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long getMillisYear(Calendar c1, int number, SimpleDateFormat sdf, SimpleDateFormat dateFormat) {
        long millis = 0;
        try {
            c1.roll(Calendar.YEAR, -number);
            Date date = sdf.parse(dateFormat.format(c1.getTime()));
            millis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }
}
