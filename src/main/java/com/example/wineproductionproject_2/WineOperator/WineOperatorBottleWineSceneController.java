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
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class WineOperatorBottleWineSceneController implements Initializable {
    @FXML
    private Button button_back, button_bottle;
    @FXML
    private Label label_result;
    @FXML
    private ChoiceBox<String> cb_wine;
    @FXML
    private TextField tf_qty;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxOptions(cb_wine);
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
        button_bottle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (cb_wine.getValue() == null) {
                    label_result.setText("Choose a wine from the options! Please, try again!");
                    return;
                }

                double qty = 0;
                try {
                    if (tf_qty.getText().trim().isEmpty()) {
                        tf_qty.setText("0");
                    }
                    qty = Double.parseDouble(tf_qty.getText());

                    if (qty <= 0) {
                        throw new NumberFormatException();
                    }
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

                try {
                    String result = DBManager.getInstance().bottleWine(cb_wine.getValue(), qty, myFormatedDate);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                    prepareChoiceBoxOptions(cb_wine);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prepareChoiceBoxOptions(ChoiceBox<String> cb) {
        cb.getItems().clear();
        try {
            cb.getItems().addAll(DBManager.getInstance().getProducedWineTypes());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
