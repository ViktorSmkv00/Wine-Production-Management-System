package com.example.wineproductionproject_2;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class BottledWine {
    private int id_recipe;
    private String name;
    private int id_bottle_type;
    private int bottles_count;
    private Timestamp date;

    public int getId_recipe() {
        return id_recipe;
    }
    public void setId_recipe(int id_recipe) {
        this.id_recipe = id_recipe;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId_bottle_type() {
        return id_bottle_type;
    }
    public void setId_bottle_type(int id_bottle_type) {
        this.id_bottle_type = id_bottle_type;
    }
    public int getBottles_count() {
        return bottles_count;
    }
    public void setBottles_count(int bottles_count) {
        this.bottles_count = bottles_count;
    }
    public Timestamp getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = DBManager.convertStringToTimestamp(date);
    }
}
