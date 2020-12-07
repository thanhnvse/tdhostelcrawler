package main.java.process;

import java.util.Date;

public class AllDataProcess {
    public static Date getTomorrowMorning0AM(){
        Date date0am = new java.util.Date();
        date0am.setHours(0);
        date0am.setMinutes(0);
        return date0am;
    }
}
