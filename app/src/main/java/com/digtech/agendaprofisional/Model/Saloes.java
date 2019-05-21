package com.digtech.agendaprofisional.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Saloes implements Parcelable {
    private String name, address, website, phone, horarioAbertura, salomId;

    public Saloes() {
    }

    protected Saloes(Parcel in) {
        name = in.readString();
        address = in.readString();
        website = in.readString();
        phone = in.readString();
        horarioAbertura = in.readString();
        salomId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(website);
        dest.writeString(phone);
        dest.writeString(horarioAbertura);
        dest.writeString(salomId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Saloes> CREATOR = new Creator<Saloes>() {
        @Override
        public Saloes createFromParcel(Parcel in) {
            return new Saloes(in);
        }

        @Override
        public Saloes[] newArray(int size) {
            return new Saloes[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(String horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public String getSalomId() {
        return salomId;
    }

    public void setSalomId(String salomId) {
        this.salomId = salomId;
    }
}
