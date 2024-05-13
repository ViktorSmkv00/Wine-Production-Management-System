package com.example.wineproductionproject_2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LogInSceneController implements Initializable {
    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_password;

    @FXML
    private Button button_login;

    @FXML
    private Label label_invalid_username_or_password;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {


                String username = tf_username.getText();
                String password = tf_password.getText();

                try {
                    User user = DBManager.getInstance().login(username, password);
                    if (user != null) {
                        WineLogger.getLOGGER().info(user.getName() + " logged into the program");

                        LoggedInSceneController.setCurrentUser(user);
                        DBManager.changeScene(event, "LoggedIn.fxml", "Your Profile", user);
                    } else {
                        WineLogger.getLOGGER().info("Tried to login with wrong password or username");
                        label_invalid_username_or_password.setText("Invalid username or password. Please, try again.");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}