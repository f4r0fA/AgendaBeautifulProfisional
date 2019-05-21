package com.digtech.agendaprofisional.Interface;

import com.digtech.agendaprofisional.Model.Saloes;

import java.util.List;

public interface ISaloesLoadListener {
    void onSaloesLoadSucess(List<Saloes> saloesList);
    void onSaloesLoadFailed(String message);
}
