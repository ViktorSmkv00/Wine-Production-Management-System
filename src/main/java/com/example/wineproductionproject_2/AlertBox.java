package com.example.wineproductionproject_2;

import javafx.scene.control.Alert;

public class AlertBox {
    public static void display(String title, String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
