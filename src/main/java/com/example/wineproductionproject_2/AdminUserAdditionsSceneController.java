package com.example.wineproductionproject_2;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminUserAdditionsSceneController implements Initializable {

    @FXML
    private Label label_newUserAdded;

    @FXML
    private TextField tf_username, tf_password;

    @FXML
    private Button button_createUser, button_back;

    @FXML
    private RadioButton rb_operator, rb_host;


    private static User currentUser;

    public static void getCurrentUser(User user) {
        currentUser = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        rb_operator.setSelected(true);

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

        button_createUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String username = tf_username.getText();
                String password = tf_password.getText();
                int type = 2;
                if (rb_host.isSelected()) {
                    type = 3;
                }
                try {
                    String result = DBManager.getInstance().insertUser(username, password, type);
                    WineLogger.getLOGGER().info(result);
                    label_newUserAdded.setText(result);

                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
