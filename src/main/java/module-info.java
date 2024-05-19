module com.example.wineproductionproject_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires log4j;


    opens com.example.wineproductionproject_2 to javafx.fxml;
    exports com.example.wineproductionproject_2;
    exports com.example.wineproductionproject_2.StorageHost;
    opens com.example.wineproductionproject_2.StorageHost to javafx.fxml;
    opens com.example.wineproductionproject_2.Login to javafx.fxml;
    exports com.example.wineproductionproject_2.WineOperator;
    opens com.example.wineproductionproject_2.WineOperator to javafx.fxml;
}