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

public class StorageRegisterGrapeSceneController implements Initializable {
    @FXML
    private Button button_addSortGrape, button_back;

    @FXML
    private Label label_result;

    @FXML
    private TextField tf_availableQuantity, tf_amountLiquid, tf_sortName;

    @FXML
    private RadioButton rb_white, rb_black;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rb_white.setSelected(true);

        button_addSortGrape.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (tf_sortName.getText().trim().isEmpty()) {
                    label_result.setText("The field for name is empty! Please, try again!");
                    return;
                }
                String name = tf_sortName.getText();

                double quantity, amountLiquid;
                try {
                    quantity = Double.parseDouble(tf_availableQuantity.getText());
                } catch (NumberFormatException e) {
                    tf_availableQuantity.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }
                try {
                    amountLiquid = Double.parseDouble(tf_amountLiquid.getText());
                } catch (NumberFormatException e) {
                    tf_amountLiquid.setText("");
                    label_result.setText("Invalid number for amount of liquid! Please, try again!");
                    return;
                }

                int category_id = 2;
                if (rb_white.isSelected()) {
                    category_id = 1;
                }
                LocalDateTime now = LocalDateTime.now();
                String myFormatedDate = null;

                try {
                    myFormatedDate = now.format(DBManager.getInstance().dateFormat);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    String result = DBManager.getInstance().insertVarietyGrape(name, amountLiquid, quantity, category_id, myFormatedDate);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
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
}
