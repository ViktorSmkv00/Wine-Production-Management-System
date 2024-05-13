module com.example.wineproductionproject_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires log4j;


    opens com.example.wineproductionproject_2 to javafx.fxml;
    exports com.example.wineproductionproject_2;
}