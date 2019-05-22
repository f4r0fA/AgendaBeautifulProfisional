package com.digtech.agendaprofisional.Common;

import com.digtech.agendaprofisional.Model.Cabeleleiro;
import com.digtech.agendaprofisional.Model.Saloes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {
    public static final Object DISABLE_TAG = "DISABLE";
    public static final int TIME_SLOT_TOTAL = 19 ;
    public static final String LOGGED_KEY = "LOGGED";
    public static final String STATE_KEY = "STATE";
    public static final String SALON_KEY = "SALON";
    public static final String CABELELEIRO_KEY = "CABELELEIRO";
    public static String state_name="";
    public static Saloes selected_salon;
    public static Cabeleleiro currentCabeleleiro;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static Calendar bookingDate = Calendar.getInstance();
    public static Saloes currentSalom;

    public static String convertTimeSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "9:00 - 9:30";
            case 1:
                return "9:30 - 10:00";
            case 2:
                return "10:00 - 10:30";
            case 3:
                return "10:30 - 11:00";
            case 4:
                return "11:00 - 11:30";
            case 5:
                return "13:00 - 13:30";
            case 6:
                return "13:30 - 14:00";
            case 7:
                return "14:00 - 14:30";
            case 8:
                return "14:30 - 15:00";
            case 9:
                return "15:00 - 15:30";
            case 10:
                return "15:30 - 16:00";
            case 11:
                return "16:00 - 16:30";
            case 12:
                return "16:30 - 17:00";
            case 13:
                return "17:00 - 17:30";
            case 14:
                return "17:30 - 18:00";
            case 15:
                return "18:00 - 18:30";
            case 16:
                return "18:30 - 19:00";
            case 17:
                return "19:00 - 19:30";
            case 18:
                return "19:30 - 20:00";
            default:
                return "INDISPONÍVEL";
        }

    }
}
