package com.example.wineproductionproject_2;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserCheckStockAvailabilitySceneController implements Initializable {

    @FXML
    private Button button_back, button_check;
    @FXML
    private Label label_result;
    @FXML
    private RadioButton rb_grapeVariety, rb_bottleType, rb_bottledWine;
    @FXML
    private ChoiceBox<String> cb_grapeVariety, cb_bottleType, cb_bottledWine;
    @FXML
    private ListView<String> lv_result;
    @FXML
    private DatePicker dp_date;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareChoiceBoxGrapeVariety(cb_grapeVariety);
        prepareChoiceBoxBottleType(cb_bottleType);
        prepareChoiceBoxBottledWine(cb_bottledWine);
        setListenersToRadioButtons();
        rb_grapeVariety.setSelected(true);



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
        button_check.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                label_result.setText("");
                lv_result.getItems().clear();
                String myDate;

                try {
                    myDate = String.valueOf(dp_date.getValue());
                } catch (Exception e) {
                    label_result.setText("Pick a date and try again!");
                    return;
                }

                String name = null;
                ArrayList<BottledWine> bottledWines = new ArrayList<>();
                ArrayList<Grape> sortGrapes = new ArrayList<>();
                ArrayList<Bottle> bottles = new ArrayList<>();
                int counter = 0;

                if (rb_bottledWine.isSelected()) {
                    if (cb_bottledWine.getValue() == null) {
                        label_result.setText("Choose a type of bottled wine from the options! Please, try again!");
                        return;
                    }
                    name = cb_bottledWine.getValue();
                    try {
                        bottledWines = DBManager.getInstance().bottledWineAvailability(name, myDate);
                        if (bottledWines.isEmpty()) {
                            label_result.setText("There is no information for this wine on this date");
                            return;
                        }
                        counter = 0;
                        lv_result.getItems().add("Last value on this date:\n");
                        for (BottledWine el : bottledWines) {
                            counter++;
                            lv_result.getItems().add(counter + ". On date: " + myDate + " Wine name: " + el.getName() + "\t Bottle capacity: " + DBManager.getInstance().getBottleCapacityById(el.getId_bottle_type()) + "ml\t Bottle count: " + el.getBottles_count() + "\n");
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } else if (rb_bottleType.isSelected()) {
                    if (cb_bottleType.getValue() == null) {
                        label_result.setText("Choose a bottle type from the options! Please, try again!");
                        return;
                    }
                    name = cb_bottleType.getValue();
                    try {
                        bottles = DBManager.getInstance().bottleTypeAvailability(Integer.parseInt(name), myDate);
                        if (bottles.isEmpty()) {
                            label_result.setText("There is no information for this type bottle on this date");
                            return;
                        }
                        counter = 0;
                        lv_result.getItems().add("Last value on this date:\n");
                        for (Bottle el : bottles) {
                            counter++;
                            lv_result.getItems().add(counter + ". On date: " + myDate + " Bottle capacity: " + el.getCapacity_ml() + "\tBottle count: " + el.getAvailable_qty() + "\n");
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (cb_grapeVariety.getValue() == null) {
                        label_result.setText("Choose a grape variety from the options! Please, try again!");
                        return;
                    }
                    name = cb_grapeVariety.getValue();
                    try {
                        sortGrapes = DBManager.getInstance().grapeVarietyAvailability(name, myDate);
                        if (sortGrapes.isEmpty()) {
                            label_result.setText("There is no information for this variety grape on this date");
                            return;
                        }
                        counter = 0;
                        lv_result.getItems().add("Last value on this date:\n");
                        for (Grape el : sortGrapes) {
                            counter++;
                            lv_result.getItems().add(counter + ". On date: " + myDate + " Variety grape: " + el.getName() + "\t Quantity: " + el.getAvailableQuantity() + "kg\n");
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setListenersToRadioButtons() {
        rb_grapeVariety.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    cb_grapeVariety.setDisable(false);
                    cb_bottleType.setDisable(true);
                    cb_bottledWine.setDisable(true);
                }
            }
        });
        rb_bottleType.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    cb_bottleType.setDisable(false);
                    cb_grapeVariety.setDisable(true);
                    cb_bottledWine.setDisable(true);
                }
            }
        });
        rb_bottledWine.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    cb_bottledWine.setDisable(false);
                    cb_bottleType.setDisable(true);
                    cb_grapeVariety.setDisable(true);
                }
            }
        });
    }

    private void prepareChoiceBoxGrapeVariety(ChoiceBox<String> cb) {
        try {
            cb.getItems().clear();
            cb.getItems().addAll(DBManager.getInstance().getGrapeVarieties());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareChoiceBoxBottleType(ChoiceBox<String> cb) {
        try {
            cb.getItems().clear();
            cb.getItems().addAll(DBManager.getInstance().getBottleTypes());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareChoiceBoxBottledWine(ChoiceBox<String> cb) {
        try {
            cb.getItems().clear();
            cb.getItems().addAll(DBManager.getInstance().getBottledWine());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}