package com.example.wineproductionproject_2.StorageHost;

import com.example.wineproductionproject_2.DBManager;
import com.example.wineproductionproject_2.WineLogger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StorageDeleteGrapeSceneController implements Initializable {

    @FXML
    private Label label_result;

    @FXML
    private ChoiceBox<String> cb_grapeVariety;

    @FXML
    private Button button_back, button_delete;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxOptions();
        button_back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DBManager.changeScene(event, "LoggedIn.fxml", "Your Profile", DBManager.getInstance().getCurrentUser());
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        button_delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb_grapeVariety.getValue() == null) {
                    label_result.setText("Choose a grape variety from the options! Please, try again!");
                    return;
                }
                try {
                    String result = DBManager.getInstance().deleteGrapeVariety(cb_grapeVariety.getValue());
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                    prepareChoiceBoxOptions();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prepareChoiceBoxOptions() {
        try {
            cb_grapeVariety.getItems().clear();
            cb_grapeVariety.getItems().addAll(DBManager.getInstance().getGrapeVarieties());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
