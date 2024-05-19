package com.example.wineproductionproject_2.StorageHost;

import com.example.wineproductionproject_2.DBManager;
import com.example.wineproductionproject_2.WineLogger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class StorageUpdateBottleSceneController implements Initializable {
    @FXML
    private Label label_result;
    @FXML
    private TextField tf_qty;
    @FXML
    private ChoiceBox<String> cb_bottleType;
    @FXML
    private Button button_update, button_back;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxOptions();
        button_update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                int qty;
                try {
                    qty = Integer.parseInt(tf_qty.getText());
                } catch (NumberFormatException e) {
                    tf_qty.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }

                LocalDateTime now = LocalDateTime.now();
                String myFormatedDate = null;

                try {
                    myFormatedDate = now.format(DBManager.getInstance().dateFormat);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (cb_bottleType.getValue() == null) {
                    label_result.setText("Choose a bottle type from the options! Please, try again!");
                    return;
                }

                try {
                    String result = DBManager.getInstance().updateBottleType(Integer.parseInt(cb_bottleType.getValue()), qty, myFormatedDate);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                    prepareChoiceBoxOptions();
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


    private void prepareChoiceBoxOptions() {
        try {
            cb_bottleType.getItems().clear();
            cb_bottleType.getItems().addAll(DBManager.getInstance().getBottleTypes());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
