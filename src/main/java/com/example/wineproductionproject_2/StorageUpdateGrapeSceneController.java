package com.example.wineproductionproject_2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StorageUpdateGrapeSceneController implements Initializable {
    @FXML
    private Label label_result;
    @FXML
    private TextField tf_qty, tf_amountLiquid;
    @FXML
    private ChoiceBox<String> cb_grapeVariety;
    @FXML
    private Button button_update, button_back;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxOptions(cb_grapeVariety);
        button_update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb_grapeVariety.getValue() == null) {
                    label_result.setText("Choose a grape variety from the options! Please, try again!");
                    return;
                }

                double quantity, amountLiquid = 0;
                try {
                    if (tf_qty.getText().trim().isEmpty()) {
                        tf_qty.setText("0");
                    }
                    quantity = Double.parseDouble(tf_qty.getText());
                } catch (NumberFormatException e) {
                    tf_qty.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }
                try {
                    if (tf_amountLiquid.getText().trim().isEmpty()) {
                        tf_amountLiquid.setText(DBManager.getInstance().getCurrentAmountOfLiquid(cb_grapeVariety.getValue()));
                    }
                    amountLiquid = Double.parseDouble(tf_amountLiquid.getText());
                } catch (NumberFormatException e) {
                    tf_amountLiquid.setText("");
                    label_result.setText("Invalid number for amount of liquid! Please, try again!");
                    return;
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                LocalDateTime now = LocalDateTime.now();
                String myFormatedDate = null;

                try {
                    myFormatedDate = now.format(DBManager.getInstance().dateFormat);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    String result = DBManager.getInstance().updateGrapeVariety(cb_grapeVariety.getValue(), quantity, amountLiquid, myFormatedDate);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                    prepareChoiceBoxOptions(cb_grapeVariety);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

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
    }

    private void prepareChoiceBoxOptions(ChoiceBox<String> cb) {
        cb.getItems().clear();
        try {
            cb.getItems().addAll(DBManager.getInstance().getGrapeVarieties());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
