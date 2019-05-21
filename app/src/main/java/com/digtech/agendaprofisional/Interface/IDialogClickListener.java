package com.digtech.agendaprofisional.Interface;

import android.content.DialogInterface;

public interface IDialogClickListener {
    void onClickPositionButton(DialogInterface dialogInterface,String userName, String password);
    void onClickNegativeButton(DialogInterface dialogInterface);
}
