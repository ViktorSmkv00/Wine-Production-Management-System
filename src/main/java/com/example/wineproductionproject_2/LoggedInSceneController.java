package com.example.wineproductionproject_2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoggedInSceneController implements Initializable {

    @FXML
    private Button button_logout, button_createUser,
            button_regBottle, button_regGrape,
            button_updateBottle, button_updateGrape,
            button_deleteGrape, button_deleteBottle,
            button_regWineType, button_regWineRecipe,
            button_updateWineType, button_deleteWineType, button_deleteWineRecipe,
            button_produceWine, button_bottleWine, button_checkAvailability;

    @FXML
    private Label label_welcome;


    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label_welcome.setText("Welcome, " + currentUser.getName() + "!");

        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DBManager.getInstance().logout();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                DBManager.changeScene(event, "logIn.fxml", "Login", null);

            }
        });
        button_checkAvailability.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBManager.changeScene(event, "checkStockAvailability.fxml", "Stock Availability", currentUser);
            }
        });

        if (currentUser.getType().equals("2")){
            // wine operator
            setButtonAvailable(button_regWineType);
            setButtonAvailable(button_regWineRecipe);
            setButtonAvailable(button_deleteWineRecipe);
            setButtonAvailable(button_deleteWineType);
            setButtonAvailable(button_updateWineType);
            setButtonAvailable(button_produceWine);
            setButtonAvailable(button_bottleWine);

            callAlertsForMinimumQty();

            button_updateWineType.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/updateWineType.fxml", "Update Wine Type", currentUser);

                }
            });
            button_deleteWineRecipe.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/deleteWineRecipe.fxml", "Delete Wine Recipe", currentUser);
                }
            });
            button_deleteWineType.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/deleteWineType.fxml", "Delete Wine Type", currentUser);
                }
            });
            button_regWineType.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/registerWineType.fxml", "Register Wine Type", currentUser);
                }
            });
            button_regWineRecipe.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/registerWineRecipe.fxml", "Register Wine Recipe", currentUser);
                }
            });
            button_produceWine.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/produceWine.fxml", "Produce Wine", currentUser);
                }
            });
            button_bottleWine.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "WineOperator/bottleWine.fxml", "Bottle Wine", currentUser);
                }
            });
        }
        if (currentUser.getType().equals("3")) {
            // host of the storage
            setButtonAvailable(button_updateGrape);
            setButtonAvailable(button_updateBottle);
            setButtonAvailable(button_regGrape);
            setButtonAvailable(button_deleteGrape);
            setButtonAvailable(button_deleteBottle);
            setButtonAvailable(button_regBottle);

            callAlertsForMinimumQty();

            button_updateGrape.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "StorageHost/updateGrape.fxml", "Update Grape Variety", currentUser);
                }
            });
            button_updateBottle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "StorageHost/updateBottle.fxml", "Update Bottle Type", currentUser);
                }
            });
            button_regGrape.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "StorageHost/registerGrape.fxml", "Add Sort of Grape", currentUser);
                }
            });
            button_deleteGrape.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "StorageHost/deleteGrape.fxml", "Delete Sort of Grape", currentUser);

                }
            });
            button_deleteBottle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "StorageHost/deleteBottle.fxml", "Delete Bottle Type", currentUser);
                }
            });
            button_regBottle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "StorageHost/registerBottle.fxml", "Add type of bottle", currentUser);
                }
            });
        }
        if (currentUser.getType().equals("1")) {
            // admin
            setButtonAvailable(button_createUser);
            button_createUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DBManager.changeScene(event, "Admin/UserAddition.fxml", "Add User", currentUser);
                }
            });
        }
    }

    private void callAlertsForMinimumQty() {
        try {
            ArrayList<String> grapeAlertMessages = DBManager.getInstance().grapeCriticalMinimumAlert(1000);
            for(String message : grapeAlertMessages){
                AlertBox.display("Critical minimum of Grape Variety",message);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<String> bottleTypeAlertMessages = DBManager.getInstance().bottleTypeCriticalMinimumAlert(1000);
            for(String message : bottleTypeAlertMessages){
                AlertBox.display("Critical minimum of Bottle Type Variety",message);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void setButtonAvailable(Button button) {
        button.setDisable(false);
        button.setOpacity(1);
    }
}
