package com.example.wineproductionproject_2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WineOperatorRegisterWineRecipeSceneController implements Initializable {
    @FXML
    private Button button_back, button_register, button_add;

    @FXML
    private Label label_result;

    @FXML
    private TextField tf_qty;

    @FXML
    private ChoiceBox<String> cb_wine, cb_sort;

    @FXML
    private ListView<String> lv_recipe;

    private ArrayList<String> listViewComponents = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxWineOptions(cb_wine);
        prepareChoiceBoxSortOptions(cb_sort);
        button_add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb_sort.getValue() == null) {
                    label_result.setText("Choose a grape variety from the options! Please, try again!");
                    return;
                }

                double quantity;
                try {
                    quantity = Double.parseDouble(tf_qty.getText());
                } catch (NumberFormatException e) {
                    tf_qty.setText("");
                    label_result.setText("Invalid number for quantity! Please, try again!");
                    return;
                }

                lv_recipe.getItems().add(cb_sort.getValue() + "\t\t" + quantity);
                listViewComponents.add(cb_sort.getValue() + "\t\t" + quantity);
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
        button_register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb_wine.getValue() == null) {
                    label_result.setText("Choose a wine from the options! Please, try again!");
                    return;
                }
                try {
                    String result = DBManager.getInstance().registerWineRecipe(cb_wine.getValue(), listViewComponents);
                    WineLogger.getLOGGER().info(result);
                    label_result.setText(result);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prepareChoiceBoxWineOptions(ChoiceBox<String> cb_wine) {
        try {
            cb_wine.getItems().clear();
            cb_wine.getItems().addAll(DBManager.getInstance().getWineTypesWithNoRecipe());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareChoiceBoxSortOptions(ChoiceBox<String> cb_sort) {
        try {
            cb_sort.getItems().clear();
            cb_sort.getItems().addAll(DBManager.getInstance().getGrapeVarieties());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
