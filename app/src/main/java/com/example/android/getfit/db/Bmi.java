package com.example.android.getfit.db;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.android.getfit.utils.Constants;
import com.example.android.getfit.utils.DateConverter;

import java.util.Date;
// Entity class model of room database
@Entity
public class Bmi {
    // room database entity primary key
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String bmiValue;
    private String bmiStatusDescription;
    @TypeConverters(DateConverter.class)
    private Date createdAt;

    public Bmi(Context context, Double weight, Double height, Date createdAt) {

        this.bmiValue = Constants.getBmiValue(weight,height) ;
        this.bmiStatusDescription = Constants.getBmiStatus(context,weight,height);
        this.createdAt = createdAt;
    }
    public Bmi(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBmiValue() {
        return bmiValue;
    }

    public void setBmiValue(String bmiValue) {
        this.bmiValue = bmiValue;
    }

    public String getBmiStatusDescription() {
        return bmiStatusDescription;
    }

    public void setBmiStatusDescription(String bmiStatusDescription) {
        this.bmiStatusDescription = bmiStatusDescription;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
