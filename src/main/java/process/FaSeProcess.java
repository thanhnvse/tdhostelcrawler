package main.java.process;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static main.java.constants.Synounymous.*;
import static main.java.constants.Synounymous.WATER;

public class FaSeProcess {
    public List<String> getFacilities(String description){
        List<String> facilities = new ArrayList<>();
        if (description.contains(AIR_CONDITIONER_V1) || description.contains(AIR_CONDITIONER_V2)) {
            facilities.add(AIR_CONDITIONER);
        }
        if (description.contains(FRIDGE_V1)) {
            facilities.add(FRIDGE);
        }
        if (description.contains(MEZZANINE_V1) || description.contains(MEZZANINE_V2)
                || description.contains(MEZZANINE_V3) || description.contains(MEZZANINE_V4)) {
            facilities.add(MEZZANINE);
        }
        if (description.contains(WASHING_MACHINE_V1)) {
            facilities.add(WASHING_MACHINE);
        }
        if (description.contains(GAS_STOVE_V1) || description.contains(GAS_STOVE_V2)) {
            facilities.add(GAS_STOVE);
        }
        if (description.contains(WC_V1) || description.contains(WC_V2) || description.contains(WC_V3)
                || description.contains(WC_V4) || description.contains(WC_V5)) {
            facilities.add(WC);
        }
        if (description.contains(BED_V1) || description.contains(BED_V2)) {
            facilities.add(BED);
        }
        if (description.contains(CABINET_V1) || description.contains(CABINET_V2) || description.contains(CABINET_V3)) {
            facilities.add(CABINET);
        }
        if (description.contains(WINDOW_V1) || description.contains(WINDOW_V2) || description.contains(WINDOW_V3)) {
            facilities.add(WINDOW);
        }
        if (description.contains(TIVI_V1) || description.contains(TIVI_V2) || description.contains(TIVI_V3) || description.contains(TIVI_V4)) {
            facilities.add(TIVI);
        }
        if (description.contains(WATER_HEATER_V1) || description.contains(WATER_HEATER_V2)) {
            facilities.add(WATER_HEATER);
        }
        if (description.contains(BALCON_V1) || description.contains(BALCON_V2)) {
            facilities.add(BALCON);
        }
        return facilities;
    }
    public List<String> getServices(String description){
        List<String> services = new ArrayList<>();
        if (description.contains(PARKING_V1) || description.contains(PARKING_V2) || description.contains(PARKING_V3)) {
            services.add(PARKING);
        }
        if (description.contains(TRASH)) {
            services.add(TRASH);
        }
        if (description.contains(ELEVATOR)) {
            services.add(ELEVATOR);
        }
        if (description.contains(CLEANING) || description.contains(CLEANING_V1)) {
            services.add(CLEANING);
        }
        if (description.contains(INTERNET) || description.contains(INTERNET_V1) || description.contains(INTERNET_V2)) {
            services.add(INTERNET);
        }
        if (description.contains(ELECTRICITY)) {
            services.add(ELECTRICITY);
        }
        if (description.contains(WATER)) {
            services.add(WATER);
        }
        return services;
    }

    public List<String> getFacilities(Elements faseList){
        List<String> facilities = new ArrayList<>();
        for (Element e : faseList) {
            String fasFinal = e.text().trim();
            if (fasFinal.equals(AIR_CONDITIONER_V3)) {
                facilities.add(AIR_CONDITIONER);
            } else if (fasFinal.equals(WATER_HEATER_V3)) {
                facilities.add(WATER_HEATER);
            } else if (fasFinal.equals(WASHING_MACHINE)) {
                facilities.add(WASHING_MACHINE);
            } else if (fasFinal.equals(TIVI)) {
                facilities.add(TIVI);
            }
        }
        return facilities;
    }

    public List<String> getServices(Elements faseList){
        List<String> services = new ArrayList<>();
        for (Element e : faseList) {
            String fasFinal = e.text().trim();
            if (fasFinal.equals(PARKING_V1)) {
                services.add(PARKING);
            } else if (fasFinal.equals(INTERNET_V3)) {
                services.add(INTERNET);
            } else if (fasFinal.equals(ELEVATOR_V1)) {
                services.add(ELEVATOR);
            }
        }
        return services;
    }
}
