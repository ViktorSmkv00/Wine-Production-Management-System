package com.example.wineproductionproject_2.WineOperator;

import com.example.wineproductionproject_2.DBManager;
import com.example.wineproductionproject_2.WineLogger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class WineOperatorRegisterWineTypeSceneController implements Initializable {
    @FXML
    private Button button_back, button_register;

    @FXML
    private Label label_result;

    @FXML
    private TextField tf_name, tf_qty;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        button_register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (tf_name.getText().trim().isEmpty()) {
                    label_result.setText("The field for name is empty! Please, try again!");
                    return;
                }
                String name = tf_name.getText();

                int quantity;
                try {
                    quantity = Integer.parseInt(tf_qty.getText());
                } catch (NumberFormatException e) {
                    tf_qty.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }

                try {
                    String result = DBManager.getInstance().registerWineType(name, quantity);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
