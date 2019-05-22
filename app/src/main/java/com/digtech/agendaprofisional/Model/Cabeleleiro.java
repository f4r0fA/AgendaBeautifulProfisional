package com.digtech.agendaprofisional.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Cabeleleiro implements Parcelable {
    private String name, username, password;
    private Long rating;
    private String cabeleleiroId;

    public Cabeleleiro() {
    }

    protected Cabeleleiro(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readLong();
        }
        cabeleleiroId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(cabeleleiroId);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(rating);
        }
        parcel.writeString(cabeleleiroId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cabeleleiro> CREATOR = new Creator<Cabeleleiro>() {
        @Override
        public Cabeleleiro createFromParcel(Parcel in) {
            return new Cabeleleiro(in);
        }

        @Override
        public Cabeleleiro[] newArray(int size) {
            return new Cabeleleiro[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCabeleleiroId() {
        return cabeleleiroId;
    }

    public void setCabeleleiroId(String cabeleleiroId) {
        this.cabeleleiroId = cabeleleiroId;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public static Creator<Cabeleleiro> getCREATOR() {
        return CREATOR;
    }
}
