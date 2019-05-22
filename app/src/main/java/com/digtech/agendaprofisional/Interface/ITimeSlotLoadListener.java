package com.digtech.agendaprofisional.Interface;

import com.digtech.agendaprofisional.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlot);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
