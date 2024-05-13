package com.example.wineproductionproject_2;

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

public class WineOperatorDeleteWineRecipeSceneController implements Initializable {
    @FXML
    private Button button_back, button_delete;

    @FXML
    private Label label_result;

    @FXML
    private ChoiceBox<String> cb_wineType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxWineOptions(cb_wineType);
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
                if (cb_wineType.getValue() == null) {
                    label_result.setText("Choose a wine from the options! Please, try again!");
                    return;
                }
                try {
                    String result = DBManager.getInstance().deleteWineRecipe(cb_wineType.getValue());
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                    prepareChoiceBoxWineOptions(cb_wineType);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void prepareChoiceBoxWineOptions(ChoiceBox<String> cb) {
        try {
            cb.getItems().clear();
            cb.getItems().addAll(DBManager.getInstance().getWineTypesWithRecipe());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
