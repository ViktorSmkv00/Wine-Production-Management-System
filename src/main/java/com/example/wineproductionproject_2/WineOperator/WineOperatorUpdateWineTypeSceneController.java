package com.example.wineproductionproject_2.WineOperator;

import com.example.wineproductionproject_2.DBManager;
import com.example.wineproductionproject_2.WineLogger;
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

public class WineOperatorUpdateWineTypeSceneController implements Initializable {
    @FXML
    private Button button_back, button_update;

    @FXML
    private Label label_result;

    @FXML
    private TextField tf_qty;

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
        button_update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb_wineType.getValue() == null) {
                    label_result.setText("Choose a wine type from the options! Please, try again!");
                    return;
                }

                double qty;
                try {
                    qty = Double.parseDouble(tf_qty.getText());
                } catch (NumberFormatException e) {
                    tf_qty.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }

                try {
                    String result = DBManager.getInstance().updateWineType(cb_wineType.getValue(), qty);
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
            cb.getItems().addAll(DBManager.getInstance().getWineTypesWithNoRecipe());
            cb.getItems().addAll(DBManager.getInstance().getWineTypesWithRecipe());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
