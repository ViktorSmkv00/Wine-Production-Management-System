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

public class StorageRegisterBottleSceneController implements Initializable {
    @FXML
    private Button button_addBottleType, button_back;

    @FXML
    private Label label_result;

    @FXML
    private TextField tf_availableQuantity;

    @FXML
    private RadioButton rb_750, rb_375, rb_200, rb_187;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rb_750.setSelected(true);


        button_addBottleType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                double qty;
                try {
                    qty = Double.parseDouble(tf_availableQuantity.getText());
                } catch (NumberFormatException e) {
                    tf_availableQuantity.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }
                int capacity = 750;
                if (rb_375.isSelected()) {
                    capacity = 375;
                } else if (rb_200.isSelected()) {
                    capacity = 200;
                } else if (rb_187.isSelected()) {
                    capacity = 187;
                }
                LocalDateTime now = LocalDateTime.now();
                String myFormatedDate = null;

                try {
                    myFormatedDate = now.format(DBManager.getInstance().dateFormat);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    String result = DBManager.getInstance().insertBottleType(capacity, qty, myFormatedDate);
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
