package com.digtech.agendaprofisional.Interface;

import com.digtech.agendaprofisional.Model.City;

import java.util.List;

public interface IOnAllStateLoadListener {
    void onAllStateLoadSuccess(List<City> cityList);
    void onAllStateLoadFailed(String message);
}
