package com.example.wineproductionproject_2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class WineOperatorProduceWineSceneController implements Initializable {

    @FXML
    private Button button_back, button_produce;

    @FXML
    private Label label_result;

    @FXML
    private ChoiceBox<String> cb_recipe;

    @FXML
    private TextField tf_doses;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxWineOptions(cb_recipe);
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
        button_produce.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                label_result.setText("");
                int doses;
                try {
                    doses = Integer.parseInt(tf_doses.getText());
                } catch (NumberFormatException e) {
                    tf_doses.setText("");
                    label_result.setText("Invalid number for doses count! Please, try again!");
                    return;
                }
                if (cb_recipe.getValue() == null) {
                    label_result.setText("Choose a recipe from the options! Please, try again!");
                    return;
                }

                try {
                    String result = DBManager.getInstance().produceWine(cb_recipe.getValue(), doses);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
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
