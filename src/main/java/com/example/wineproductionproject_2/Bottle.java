package com.example.wineproductionproject_2;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Bottle {
    private int id_type;
    private int capacity_ml;
    private int available_qty;
    private Timestamp date;

    public int getId_type() {
        return id_type;
    }
    public void setId_type(int id_type) {
        this.id_type = id_type;
    }
    public int getCapacity_ml() {
        return capacity_ml;
    }
    public void setCapacity_ml(int capacity_ml) {
        this.capacity_ml = capacity_ml;
    }
    public int getAvailable_qty() {
        return available_qty;
    }
    public void setAvailable_qty(int available_qty) {
        this.available_qty = available_qty;
    }
    public Timestamp getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = DBManager.convertStringToTimestamp(date);
    }
}
