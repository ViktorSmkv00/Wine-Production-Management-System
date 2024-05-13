package com.example.wineproductionproject_2;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Grape {
    private Integer idSort;
    private Double amountLiquid;
    private Integer idCategory;

    private String name;
    private Double availableQuantity;
    private Timestamp date;

    public Timestamp getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = DBManager.convertStringToTimestamp(date);
    }

    public Grape(){};

    public Grape(Integer idSort, String name, Double amountLiquid, Double availableQuantity, Integer idCategory) {
        this.idSort = idSort;
        this.name = name;
        this.amountLiquid = amountLiquid;
        this.availableQuantity = availableQuantity;
        this.idCategory = idCategory;
    }

    public Integer getIdSort() {
        return idSort;
    }

    public void setIdSort(Integer idSort) {
        this.idSort = idSort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmountLiquid() {
        return amountLiquid;
    }

    public void setAmountLiquid(Double amountLiquid) {
        this.amountLiquid = amountLiquid;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }
}
