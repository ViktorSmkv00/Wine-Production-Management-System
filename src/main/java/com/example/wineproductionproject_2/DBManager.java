package com.example.wineproductionproject_2;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;


public class DBManager {
    private final Connection connection;
    private User currentUser;
    public final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    protected final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public User getCurrentUser() {
        return currentUser;
    }

    protected DBManager(String dbURL, String user, String password) throws SQLException, ClassNotFoundException {
        connection = DriverManager.getConnection(dbURL, user, password);
        connection.setCatalog("wine_production");
    }

    protected void shutdown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    protected List<User> getUserList() throws SQLException {
        try (Statement stmnt = connection.createStatement(); ResultSet rs = stmnt.executeQuery("select a.user_id, a.name, a.password, b.name as type_name from user a" + " JOIN user_type b ON a.type_id = b.type_id;");) {
            List<User> personList = new ArrayList<>();

            while (rs.next()) {
                String id = rs.getString("user_id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                String type = rs.getString("type_name");
                User user = new User(id, name, type, password);
                personList.add(user);
            }
            return personList;
        }
    }

    protected String insertUser(String name, String password, int type_id) throws SQLException {

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;

        String query = "SELECT name FROM user where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);

        rs = pstmt1.executeQuery();

        if (!rs.next()) {
            pstmt2 = connection.prepareStatement("INSERT INTO `wine_production`.`user` (`type_id`,`name`,`password`)\n" + "VALUES(" + type_id + ",'" + name + "','" + password + "');");
            pstmt2.executeUpdate();
            return "Username " + name + " has been registered.";
        } else {
            return "Username " + name + " already exists.";
        }
    }

    protected String deleteUser(String name, String password) throws SQLException {

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;

        String query = "select * from user where name=? and password =?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        pstmt1.setString(2, password);
        rs = pstmt1.executeQuery();

        int id = 0;
        if (rs.next()) {
            id = rs.getInt("user_id");
        }

        if (id != 0) {
            pstmt2 = connection.prepareStatement("Delete from user where user_id=? ");
            pstmt2.setInt(1, rs.getInt("user_id"));
            pstmt2.executeUpdate();
            return "Successfully deleted user";
        } else {
            return "That user does not exist";
        }
    }

    public void logout() {
        currentUser = null;
    }

    public User login(String name, String password) throws SQLException {
        Statement stmnt = connection.createStatement();
        ResultSet rs = stmnt.executeQuery("select * from user where name = '" + name + "' and password='" + password + "';");

        if (!rs.next()) {
            return null;
        }
        currentUser = new User(String.valueOf(rs.getInt("user_id")), rs.getString("name"), String.valueOf(rs.getInt("type_id")), rs.getString("password"));
        return currentUser;
    }

    public static DBManager getInstance() throws SQLException, ClassNotFoundException {
        return new DBManager("jdbc:mysql://localhost:3306", "root", "viktor123");
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, User user) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(DBManager.class.getResource(fxmlFile));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        assert root != null;
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Grape Varieties CRUD
    public String insertVarietyGrape(String name, Double amountLiquid, Double availableQuantity, Integer category_id, String date) throws SQLException {

        PreparedStatement pstmt1 = null;
        ResultSet rs = null;


        String query = "SELECT name FROM sort_grape where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);

        rs = pstmt1.executeQuery();

        if (!rs.next()) {
            return registerGrapeVariety(name, amountLiquid, availableQuantity, category_id, date);
        }
        return "There is already registered Grape variety with that name. Try again!";
    }

    public String updateGrapeVariety(String name, Double quantity, Double amountLiquid, String date) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select * from wine_production.sort_grape where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        rs = pstmt1.executeQuery();
        rs.next();

        double in_stock = rs.getDouble("available_qty");
        if ((quantity + in_stock) < 0) {
            return "The quantity you want to reduce is not in stock. There is only " + in_stock + "kg of " + name;
        }

        query = " INSERT INTO wine_production.hist_sort_grape " +
                "VALUES (?,?,?,?,?,?,?)";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, rs.getInt("id_sort"));
        pstmt1.setInt(2, rs.getInt("id_category"));
        pstmt1.setInt(3, rs.getInt("amount_liquid"));
        pstmt1.setInt(4, rs.getInt("available_qty"));

        Timestamp timestamp = rs.getTimestamp("date");
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String strFromTimeStamp = dtf.format(timestamp);

        pstmt1.setString(5, strFromTimeStamp);
        pstmt1.setString(6, rs.getString("name"));
        pstmt1.setString(7, date);
        pstmt1.executeUpdate();

        query = """
                UPDATE wine_production.sort_grape\s
                SET\s
                    available_qty=?,
                    date=?,
                    amount_liquid=?
                WHERE
                    name=?""";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setDouble(1, (quantity + in_stock));
        pstmt1.setString(2, date);
        pstmt1.setDouble(3, amountLiquid);
        pstmt1.setString(4, name);
        pstmt1.executeUpdate();


        return "Updated existing variety grape, named " + name;
    }

    protected String registerGrapeVariety(String name, Double amountLiquid, Double availableQuantity, Integer category_id, String date) throws SQLException {
        PreparedStatement pstmt2;
        pstmt2 = connection.prepareStatement("INSERT INTO wine_production.sort_grape (name, amount_liquid, available_qty, id_category, date) VALUES('" + name + "'," + amountLiquid + ", " + availableQuantity + "," + category_id + ", '" + date + "');");
        pstmt2.executeUpdate();
        return "The new variety of grape named " + name + " has been registered.";
    }

    public String deleteGrapeVariety(String name) throws SQLException {
        PreparedStatement pstmt1 = null;

        try {
            String query = "DELETE FROM sort_grape WHERE name=?";
            pstmt1 = connection.prepareStatement(query);
            pstmt1.setString(1, name);
            pstmt1.executeUpdate();

            query = "DELETE FROM hist_sort_grape WHERE name=?";
            pstmt1 = connection.prepareStatement(query);
            pstmt1.setString(1, name);
            pstmt1.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            return "You can not delete grape variety named - '" + name + "', because you have recipes including that grape variety!";
        }
        return "Successfully deletion of grape variety named - '" + name + "'!";
    }

    public ArrayList<String> getGrapeVarieties() throws SQLException {
        try (Statement stmnt = connection.createStatement(); ResultSet rs = stmnt.executeQuery("select name from sort_grape");) {
            ArrayList<String> grapeVarieties = new ArrayList<>();
            while (rs.next()) {
                grapeVarieties.add(rs.getString("name"));
            }
            return grapeVarieties;
        }
    }

    public String getCurrentAmountOfLiquid(String name) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select amount_liquid from wine_production.sort_grape where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        rs = pstmt1.executeQuery();
        rs.next();
        return String.valueOf(rs.getDouble("amount_liquid"));
    }

    protected Integer getSortIdByName(String name) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select id_sort from wine_production.sort_grape where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        rs = pstmt1.executeQuery();
        rs.next();
        return rs.getInt("id_sort");
    }

    protected String getSortNameById(Integer id) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select name from wine_production.sort_grape where id_sort=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, id);
        rs = pstmt1.executeQuery();
        rs.next();
        return rs.getString("name");
    }

    protected Integer getGrapeAvailableQty(String name) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select available_qty from wine_production.sort_grape where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        rs = pstmt1.executeQuery();
        if (rs.next()) {
            return rs.getInt("available_qty");
        }
        return 0;
    }

    // Bottle Types CRUD
    protected Integer getBottleTypeAvailability(Integer capacity) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select available_qty from wine_production.bottle where capacity_ml=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, capacity);
        rs = pstmt1.executeQuery();
        if (rs.next()) {
            return rs.getInt("available_qty");
        }
        return 0;
    }

    protected Integer getBottleCapacityById(Integer bottle_id) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select capacity_ml from wine_production.bottle where id_type=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, bottle_id);
        rs = pstmt1.executeQuery();
        rs.next();
        return rs.getInt("capacity_ml");
    }

    public String insertBottleType(Integer capacity, Double availableQuantity, String date) throws SQLException {

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;


        String query = "SELECT capacity_ml FROM bottle where capacity_ml=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, capacity);

        rs = pstmt1.executeQuery();

        if (!rs.next()) {
            return registerBottleType(capacity, availableQuantity, date);
        }
        return "There is already registered Bottle Type with that capacity. Try again!";
    }

    public String updateBottleType(Integer capacity, Integer quantity, String date) throws SQLException {
        String query;
        PreparedStatement pstmt1;
        ResultSet rs;
        query = "Select * from wine_production.bottle where capacity_ml=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, capacity);
        rs = pstmt1.executeQuery();
        rs.next();

        double in_stock = rs.getDouble("available_qty");
        if ((quantity + in_stock) < 0) {
            return "The quantity you want to reduce is not in stock. There are only " + in_stock + " bottles with capacity of" + capacity;
        }

        query = " INSERT INTO wine_production.hist_bottle " +
                "VALUES (?,?,?,?,?)";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, rs.getInt("id_type"));
        pstmt1.setInt(2, rs.getInt("capacity_ml"));
        pstmt1.setInt(3, rs.getInt("available_qty"));

        Timestamp timestamp = rs.getTimestamp("date");
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String strFromTimeStamp = dtf.format(timestamp);

        pstmt1.setString(4, strFromTimeStamp);
        pstmt1.setString(5, date);
        pstmt1.executeUpdate();

        query = """
                UPDATE wine_production.bottle\s
                SET\s
                    available_qty = ?,
                    date = ?
                WHERE
                    capacity_ml = ?;""";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setDouble(1, quantity + in_stock);
        pstmt1.setString(2, date);
        pstmt1.setInt(3, capacity);
        pstmt1.executeUpdate();
        return "Updated bottle type with capacity of " + capacity;
    }

    protected String registerBottleType(Integer capacity, Double availableQuantity, String date) throws SQLException {
        PreparedStatement pstmt2;
        pstmt2 = connection.prepareStatement("INSERT INTO wine_production.bottle (capacity_ml, available_qty, date) VALUES(" + capacity + "," + availableQuantity + ", '" + date + "');");
        pstmt2.executeUpdate();
        return "The new bottle type with capacity of " + capacity + "ml has been registered.";
    }

    public ArrayList<String> getBottleTypes() throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select capacity_ml from bottle");
        ) {
            ArrayList<String> bottleTypes = new ArrayList<>();
            while (rs.next()) {
                bottleTypes.add(String.valueOf(rs.getInt("capacity_ml")));
            }
            return bottleTypes;
        }
    }

    public String deleteBottleType(String capacity_ml) throws SQLException {
        PreparedStatement pstmt1 = null;
        try {
            String query = "DELETE FROM bottle WHERE capacity_ml=?";
            pstmt1 = connection.prepareStatement(query);
            int capacity_ml_int = Integer.parseInt(capacity_ml);
            pstmt1.setInt(1, capacity_ml_int);
            pstmt1.executeUpdate();

            query = "DELETE FROM hist_bottle WHERE capacity_ml=?";
            pstmt1 = connection.prepareStatement(query);
            pstmt1.setInt(1, capacity_ml_int);
            pstmt1.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            return "You can not delete bottle type with capacity of - '" + capacity_ml + "'ml, because you have bottled wine including this type of bottle!";
        }
        return "Successfully deletion of bottle type with capacity of - '" + capacity_ml + "'!";
    }

    protected Integer getBottleIdTypeByCapacity(Integer capacity_ml) throws SQLException {
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;

        String query = "Select * FROM bottle WHERE capacity_ml=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, capacity_ml);
        rs = pstmt1.executeQuery();
        rs.next();

        return rs.getInt("id_type");
    }

    // Wine TYPE RECIPE CRUD
    private HashMap<String, Integer> getRecipeIngredients(String wineName) throws SQLException {
        HashMap<String, Integer> result = new HashMap<>();
        Integer recipe_id = this.getRecipeIdByName(wineName);

        ResultSet rs1;
        String query1;
        PreparedStatement pstmt1;


        query1 = "Select * from wine_production.wine_recipe where id_recipe=?";
        pstmt1 = connection.prepareStatement(query1);
        pstmt1.setInt(1, recipe_id);
        rs1 = pstmt1.executeQuery();
        while (rs1.next()) {
            result.put(this.getSortNameById(rs1.getInt("id_sort")), rs1.getInt("qty_of_sort"));
        }
        return result;
    }

    private Integer getWineTypeObtainedQty(String wineName) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select obtained_quantity from wine_production.wine_type where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, wineName);
        rs = pstmt1.executeQuery();
        rs.next();
        return rs.getInt("obtained_quantity");
    }

    protected Integer getRecipeIdByName(String name) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select id_recipe from wine_production.wine_type where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        rs = pstmt1.executeQuery();
        rs.next();
        return rs.getInt("id_recipe");
    }

    protected String getRecipeNameById(Integer recipe_id) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select name from wine_production.wine_type where id_recipe=?";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, recipe_id);
        rs = pstmt1.executeQuery();
        rs.next();
        return rs.getString("name");
    }

    public ArrayList<String> getWineTypesWithNoRecipe() throws SQLException {
        ResultSet rs1, rs2;
        String query1, query2;
        PreparedStatement pstmt1;

        ArrayList<String> resultList = new ArrayList<>();

        Set<Integer> winesWithRecipe = new HashSet<>();
        query2 = "Select id_recipe from wine_production.wine_recipe";
        pstmt1 = connection.prepareStatement(query2);
        rs2 = pstmt1.executeQuery();
        while (rs2.next()) {
            winesWithRecipe.add(rs2.getInt("id_recipe"));
        }

        query1 = "Select id_recipe, name from wine_production.wine_type";
        pstmt1 = connection.prepareStatement(query1);
        rs1 = pstmt1.executeQuery();
        while (rs1.next()) {
            if (!winesWithRecipe.contains(rs1.getInt("id_recipe"))) {
                resultList.add(rs1.getString("name"));
            }
        }
        return resultList;
    }

    public ArrayList<String> getWineTypesWithRecipe() throws SQLException {
        ResultSet rs1, rs2;
        String query1, query2;
        PreparedStatement pstmt1;

        ArrayList<String> resultList = new ArrayList<>();

        Set<Integer> winesWithRecipe = new HashSet<>();
        query2 = "Select id_recipe from wine_production.wine_recipe";
        pstmt1 = connection.prepareStatement(query2);
        rs2 = pstmt1.executeQuery();
        while (rs2.next()) {
            winesWithRecipe.add(rs2.getInt("id_recipe"));
        }

        query1 = "Select id_recipe, name from wine_production.wine_type";
        pstmt1 = connection.prepareStatement(query1);
        rs1 = pstmt1.executeQuery();
        while (rs1.next()) {
            if (winesWithRecipe.contains(rs1.getInt("id_recipe"))) {
                resultList.add(rs1.getString("name"));
            }
        }
        return resultList;
    }

    public String registerWineRecipe(String wineName, ArrayList<String> rows) throws SQLException {
        PreparedStatement pstmt2;
        for (String row : rows) {
            String[] arrOfString = row.split("\t\t");

            String query = "INSERT INTO wine_production.wine_recipe (id_recipe, id_sort, qty_of_sort) VALUES(" + this.getRecipeIdByName(wineName) + "," + this.getSortIdByName(arrOfString[0]) + "," + Double.parseDouble(arrOfString[1]) + ");";
            pstmt2 = connection.prepareStatement(query);
            pstmt2.executeUpdate();
        }

        return "The new recipe for wine named" + wineName + " has been registered.";
    }

    public String registerWineType(String name, Integer quantity) throws SQLException {
        PreparedStatement pstmt2;
        pstmt2 = connection.prepareStatement("INSERT INTO wine_production.wine_type (name,obtained_quantity) VALUES('" + name + "'," + quantity + ");");
        pstmt2.executeUpdate();
        return "The new wine type named " + name + " has been registered.";
    }

    public String deleteWineType(String name) throws SQLException {
        PreparedStatement pstmt1 = null;
        String query = "";

        try {
            ArrayList<String> wineTypeWithRecipe = this.getWineTypesWithRecipe();
            for (String el : wineTypeWithRecipe) {
                if (el.equals(name)) {
                    query = "DELETE FROM wine_recipe WHERE id_recipe=?";
                    pstmt1 = connection.prepareStatement(query);
                    pstmt1.setInt(1, this.getRecipeIdByName(name));
                    pstmt1.executeUpdate();

                    query = "DELETE FROM wine_type WHERE name=?";
                    pstmt1 = connection.prepareStatement(query);
                    pstmt1.setString(1, name);
                    pstmt1.executeUpdate();

                    return "The wine type named " + name + " and its recipe have been deleted successfully.";
                }
            }

            ArrayList<String> wineTypeWithNoRecipe = this.getWineTypesWithNoRecipe();
            for (String el : wineTypeWithNoRecipe) {
                if (el.equals(name)) {
                    query = "DELETE FROM wine_type WHERE name=?";
                    pstmt1 = connection.prepareStatement(query);
                    pstmt1.setString(1, name);
                    pstmt1.executeUpdate();

                    return "The wine type named " + name + " has been deleted successfully.";
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return "You can not delete the type of wine named '" + name + "', because you have bottled wine of that type!";
        }
        return "Something went wrong with the deletion";
    }

    public String deleteWineRecipe(String name) throws SQLException {
        PreparedStatement pstmt1 = null;
        String query = "";

        query = "DELETE FROM wine_recipe WHERE id_recipe=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, this.getRecipeIdByName(name));
        pstmt1.executeUpdate();


        return "The recipe for " + name + " has been successfully deleted.";
    }

    public String updateWineType(String name, Double new_qty) throws SQLException {
        String query;
        PreparedStatement pstmt1;
        ResultSet rs;

        query = " UPDATE wine_production.wine_type SET obtained_quantity =? WHERE id_recipe =?;";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setDouble(1, new_qty);
        pstmt1.setInt(2, this.getRecipeIdByName(name));
        pstmt1.executeUpdate();
        return "Updated wine type named " + name;
    }

    // BottledWine CRUD
    protected SortedSet<String> getBottledWine() throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select id_recipe from bottled_wine");
        ) {
            SortedSet<String> bottledWine = new TreeSet<>();
            while (rs.next()) {
                bottledWine.add(this.getRecipeNameById(rs.getInt("id_recipe")));
            }
            return bottledWine;
        }
    }

    private String insertProducedWine(Integer id_recipe, String wineName, Double available_qty) throws SQLException {
        PreparedStatement pstmt2;
        pstmt2 = connection.prepareStatement("INSERT INTO wine_production.produced_wine" +
                " (id_recipe, name, available_qty) " +
                "VALUES(" + id_recipe + ",'" + wineName + "', " + available_qty + ");");
        pstmt2.executeUpdate();

        return "Successfully produced " + wineName;
    }

    private String updateProducedWine(Integer id_recipe, String wineName, Double available_qty) throws SQLException {
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;


        String query = "SELECT * FROM produced_wine where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, wineName);
        rs = pstmt1.executeQuery();

        if (!rs.next()) {
            return insertProducedWine(id_recipe, wineName, available_qty);
        }

        query = " UPDATE wine_production.produced_wine SET available_qty=? WHERE id_recipe=?;";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setDouble(1, available_qty + rs.getDouble("available_qty"));
        pstmt1.setInt(2, id_recipe);
        pstmt1.executeUpdate();
        return "Successfully produced " + wineName;
    }

    protected void deleteProducedWine(String wineName) throws SQLException {
        PreparedStatement pstmt1 = null;
        String query = "";

        try {
            query = "DELETE FROM produced_wine WHERE id_recipe=?";
            pstmt1 = connection.prepareStatement(query);
            pstmt1.setInt(1, this.getRecipeIdByName(wineName));
            pstmt1.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    protected void deleteBottledWine(String wineName)throws SQLException{
        PreparedStatement pstmt1 = null;
        String query = "";

        try {
            query = "DELETE FROM bottled_wine WHERE id_recipe=?";
            pstmt1 = connection.prepareStatement(query);
            pstmt1.setInt(1, this.getRecipeIdByName(wineName));
            pstmt1.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    public String produceWine(String wineName, int doses) throws SQLException {
        // get the recipe with all ingredients
        HashMap<String, Integer> ingredients = this.getRecipeIngredients(wineName);
        HashMap<String, Integer> ingredientsNewQuantities = new HashMap<>();

        // check the availability of each * doses
        for (Map.Entry<String, Integer> set : ingredients.entrySet()) {
            int available_qty = this.getGrapeAvailableQty(set.getKey());
            if (available_qty < doses * set.getValue()) {
                return doses * set.getValue() - available_qty + "kg of Grape :" + set.getKey() + " are not enough";
            } else {
                ingredientsNewQuantities.put(set.getKey(), available_qty - (doses * set.getValue()));
            }
        }

        // produce and update the quantities of Grape Varieties
        String result = this.updateProducedWine(
                this.getRecipeIdByName(wineName),
                wineName,
                (double) this.getWineTypeObtainedQty(wineName) * doses);

        for (Map.Entry<String, Integer> set : ingredientsNewQuantities.entrySet()) {
            this.updateGrapeVariety(
                    set.getKey(),
                    Double.valueOf(set.getValue()) - this.getGrapeAvailableQty(set.getKey()),
                    Double.parseDouble(getCurrentAmountOfLiquid(set.getKey())),
                    LocalDateTime.now().format(this.dateFormat));
        }
        return result;
    }

    public ArrayList<String> getProducedWineTypes() throws SQLException {
        try (Statement stmnt = connection.createStatement();
             ResultSet rs = stmnt.executeQuery("select name from produced_wine;");) {
            ArrayList<String> grapeVarieties = new ArrayList<>();
            while (rs.next()) {
                grapeVarieties.add(rs.getString("name"));
            }
            return grapeVarieties;
        }
    }

    public String bottleWine(String wineName, Double qty, String date) throws SQLException {
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;

        double starting_qty = qty;

        String query = "SELECT * FROM produced_wine where name=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, wineName);
        rs = pstmt1.executeQuery();
        rs.next();

        Double available_qty = rs.getDouble("available_qty");

        if (available_qty < qty) {
            return "There are only " + rs.getDouble("available_qty") + "l of " + wineName + " wine!";
        }

        int cap750 = getBottleTypeAvailability(750);
        int cap375 = getBottleTypeAvailability(375);
        int cap200 = getBottleTypeAvailability(200);
        int cap187 = getBottleTypeAvailability(187);

        HashMap<Integer, Integer> capacityKeyQtyVal = new HashMap<>();
        capacityKeyQtyVal.put(750, cap750);
        capacityKeyQtyVal.put(375, cap375);
        capacityKeyQtyVal.put(200, cap200);
        capacityKeyQtyVal.put(187, cap187);

        int filled750 = 0, filled375 = 0, filled200 = 0, filled187 = 0;

        while (qty > 0.187) {
            if (capacityKeyQtyVal.get(750) > 0 && qty >= 0.750) {
                filled750++;
                capacityKeyQtyVal.replace(750, capacityKeyQtyVal.get(750), capacityKeyQtyVal.get(750) - 1);
                qty -= 0.750;
            }

            if (capacityKeyQtyVal.get(375) > 0 && qty >= 0.375) {
                filled375++;
                capacityKeyQtyVal.replace(375, capacityKeyQtyVal.get(375), capacityKeyQtyVal.get(375) - 1);
                qty -= 0.375;
            }

            if (capacityKeyQtyVal.get(200) > 0 && qty >= 0.200) {
                filled200++;
                capacityKeyQtyVal.replace(200, capacityKeyQtyVal.get(200), capacityKeyQtyVal.get(200) - 1);
                qty -= 0.200;
            }

            if (capacityKeyQtyVal.get(187) > 0 && qty >= 0.187) {
                filled187++;
                capacityKeyQtyVal.replace(187, capacityKeyQtyVal.get(187), capacityKeyQtyVal.get(187) - 1);
                qty -= 0.187;
            }

            if (capacityKeyQtyVal.get(750) == 0 && capacityKeyQtyVal.get(375) == 0 &&
                    capacityKeyQtyVal.get(200) == 0 && capacityKeyQtyVal.get(187) == 0) {
                return "There are not enough bottles to fill this quantity";
            }
        }

        // update storage bottle availability
        // update table with bottledWine
        if (filled750 > 0) {
            updateBottleType(750, -filled750, date);
            fillBottle(750, filled750, wineName, date);
        }
        if (filled375 > 0) {
            updateBottleType(375, -filled375, date);
            fillBottle(375, filled375, wineName, date);
        }
        if (filled200 > 0) {
            updateBottleType(200, -filled200, date);
            fillBottle(200, filled200, wineName, date);
        }
        if (filled187 > 0) {
            updateBottleType(187, -filled187, date);
            fillBottle(187, filled187, wineName, date);
        }

        // update table with produced wine that are not bottled
        updateProducedWine(getRecipeIdByName(wineName), wineName, qty - starting_qty);

        return "Successfully bottled " + (starting_qty - qty) + "l of " + wineName + "!";
    }

    private void fillBottle(Integer capacity, Integer qty, String wineName, String date) throws SQLException {
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;


        String query = "SELECT * FROM bottled_wine where id_type=? and id_recipe=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, getBottleIdTypeByCapacity(capacity));
        pstmt1.setInt(2, getRecipeIdByName(wineName));

        rs = pstmt1.executeQuery();

        if (!rs.next()) {
            insertBottledWine(capacity, qty, wineName, date);
            return;
        }

        updateBottledWine(capacity, qty, wineName, date);
    }

    private void updateBottledWine(Integer capacity, Integer qty, String wineName, String date) throws SQLException {
        String query;
        PreparedStatement pstmt1;
        ResultSet rs;

        query = "Select * from wine_production.bottled_wine where id_type=? and id_recipe=?";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, getBottleIdTypeByCapacity(capacity));
        pstmt1.setInt(2, getRecipeIdByName(wineName));
        rs = pstmt1.executeQuery();

        rs.next();

        query = " INSERT INTO wine_production.hist_bottled_wine " +
                "VALUES (?,?,?,?,?)";
        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, rs.getInt("id_type"));
        pstmt1.setInt(2, rs.getInt("id_recipe"));
        pstmt1.setInt(3, rs.getInt("bottles_count"));

        Timestamp timestamp = rs.getTimestamp("date");
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String strFromTimeStamp = dtf.format(timestamp);

        pstmt1.setString(4, strFromTimeStamp);
        pstmt1.setString(5, date);
        pstmt1.executeUpdate();

        query = """
                UPDATE wine_production.bottled_wine\s
                SET\s
                    bottles_count = ?,
                    date = ?
                WHERE
                    id_type=? and id_recipe=?;""";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, qty);
        pstmt1.setString(2, date);
        pstmt1.setInt(3, getBottleIdTypeByCapacity(capacity));
        pstmt1.setInt(4, getRecipeIdByName(wineName));
        pstmt1.executeUpdate();
    }

    private void insertBottledWine(Integer capacity, Integer qty, String wineName, String date) throws SQLException {
        PreparedStatement pstmt2;
        pstmt2 = connection.prepareStatement("INSERT INTO wine_production.bottled_wine" +
                " (id_type, id_recipe, bottles_count, date) " +
                "VALUES(" + getBottleIdTypeByCapacity(capacity) + ", " + getRecipeIdByName(wineName) + ", " + qty + ", '" + date + "');");
        pstmt2.executeUpdate();
    }


    // Date Availability
    protected ArrayList<BottledWine> bottledWineAvailability(String wineName, String lowerBoundSTR) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        String formattedLowerBound = this.simpleDateFormat.format(convertStringToTimestamp(lowerBoundSTR));

        String upperBoundSTR = this.addOneDay(lowerBoundSTR);
        String formattedUpperBound = this.simpleDateFormat.format(convertStringToTimestamp(upperBoundSTR));

        query = "Select * from wine_production.bottled_wine where id_recipe=? and (date between ? and ? OR date <= ?)";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, this.getRecipeIdByName(wineName));
        pstmt1.setString(2, formattedLowerBound);
        pstmt1.setString(3, formattedUpperBound);
        pstmt1.setString(4, formattedLowerBound);
        rs = pstmt1.executeQuery();
        if (!rs.next()) {
            return getHistoricalWineAvailability(wineName, lowerBoundSTR);
        }

        ArrayList<BottledWine> resultList = new ArrayList<>();
        resultList.add(loadBottledWineToObject(rs, lowerBoundSTR));
        while (rs.next()) {
            resultList.add(loadBottledWineToObject(rs, lowerBoundSTR));
        }
        resultList.addAll(getHistoricalWineAvailability(wineName, lowerBoundSTR));
        return resultList;
    }

    private ArrayList<BottledWine> getHistoricalWineAvailability(String wineName, String lowerBoundSTR) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        String formattedLowerBound = this.simpleDateFormat.format(convertStringToTimestamp(lowerBoundSTR));

        String upperBoundSTR = this.addOneDay(lowerBoundSTR);
        String formattedUpperBound = this.simpleDateFormat.format(convertStringToTimestamp(upperBoundSTR));


        query = "Select * from wine_production.hist_bottled_wine where id_recipe=? and (date>=? and date<? OR ? between date and end_date)";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, this.getRecipeIdByName(wineName));
        pstmt1.setString(2, formattedLowerBound);
        pstmt1.setString(3, formattedUpperBound);
        pstmt1.setString(4, formattedLowerBound);
        rs = pstmt1.executeQuery();
        if (!rs.next()) {
            return new ArrayList<>();
        }

        ArrayList<BottledWine> resultList = new ArrayList<>();
        resultList.add(loadBottledWineToObject(rs, lowerBoundSTR));
        while (rs.next()) {
            resultList.add(loadBottledWineToObject(rs, lowerBoundSTR));
        }
        return resultList;
    }

    private BottledWine loadBottledWineToObject(ResultSet rs, String lowerDateBound) throws SQLException {
        BottledWine bottledWine = new BottledWine();
        bottledWine.setId_recipe(rs.getInt("id_recipe"));
        bottledWine.setId_bottle_type(rs.getInt("id_type"));
        bottledWine.setBottles_count(rs.getInt("bottles_count"));
        bottledWine.setName(this.getRecipeNameById(rs.getInt("id_recipe")));
        bottledWine.setDate(lowerDateBound);
        return bottledWine;
    }

    protected ArrayList<Grape> grapeVarietyAvailability(String name, String lowerBoundSTR) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        String formattedLowerBound = this.simpleDateFormat.format(convertStringToTimestamp(lowerBoundSTR));

        String upperBoundSTR = this.addOneDay(lowerBoundSTR);
        String formattedUpperBound = this.simpleDateFormat.format(convertStringToTimestamp(upperBoundSTR));

        query = "Select * from wine_production.sort_grape where name=? and (date between ? and ? OR date <= ?)";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        pstmt1.setString(2, formattedLowerBound);
        pstmt1.setString(3, formattedUpperBound);
        pstmt1.setString(4, formattedLowerBound);
        rs = pstmt1.executeQuery();
        if (!rs.next()) {
            return getHistoricalGrapeAvailability(name, lowerBoundSTR);
        }

        ArrayList<Grape> resultList = new ArrayList<>();

        resultList.add(loadGrapeToObject(rs, lowerBoundSTR));
        while (rs.next()) {
            resultList.add(loadGrapeToObject(rs, lowerBoundSTR));
        }
        resultList.addAll(getHistoricalGrapeAvailability(name, lowerBoundSTR));
        return resultList;
    }

    private ArrayList<Grape> getHistoricalGrapeAvailability(String name, String lowerBoundSTR) throws SQLException {
        String formattedLowerBound = this.simpleDateFormat.format(convertStringToTimestamp(lowerBoundSTR));

        String upperBoundSTR = this.addOneDay(lowerBoundSTR);
        String formattedUpperBound = this.simpleDateFormat.format(convertStringToTimestamp(upperBoundSTR));

        ResultSet rs;
        String query;
        PreparedStatement pstmt1;
        query = "Select * from wine_production.hist_sort_grape where name=? and (date>=? and date<? OR ? between date and end_date)";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setString(1, name);
        pstmt1.setString(2, formattedLowerBound);
        pstmt1.setString(3, formattedUpperBound);
        pstmt1.setString(4, formattedLowerBound);
        rs = pstmt1.executeQuery();
        if (!rs.next()) {
            return new ArrayList<>();
        }

        ArrayList<Grape> resultList = new ArrayList<>();

        resultList.add(loadGrapeToObject(rs, lowerBoundSTR));
        while (rs.next()) {
            resultList.add(loadGrapeToObject(rs, lowerBoundSTR));
        }
        return resultList;
    }

    private Grape loadGrapeToObject(ResultSet rs, String lowerDateBound) throws SQLException {
        Grape grape = new Grape();
        grape.setIdSort(rs.getInt("id_sort"));
        grape.setIdCategory(rs.getInt("id_category"));
        grape.setAmountLiquid(Double.parseDouble(String.valueOf(rs.getInt("amount_liquid"))));
        grape.setName(rs.getString("name"));
        grape.setDate(lowerDateBound);
        grape.setAvailableQuantity(Double.parseDouble(String.valueOf(rs.getInt("available_qty"))));
        return grape;
    }

    protected ArrayList<Bottle> bottleTypeAvailability(Integer capacity, String lowerBoundSTR) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        String formattedLowerBound = this.simpleDateFormat.format(convertStringToTimestamp(lowerBoundSTR));

        String upperBoundSTR = this.addOneDay(lowerBoundSTR);
        String formattedUpperBound = this.simpleDateFormat.format(convertStringToTimestamp(upperBoundSTR));

        query = "Select * from wine_production.bottle where capacity_ml=? and (date between ? and ? OR date <= ?)";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, capacity);
        pstmt1.setString(2, formattedLowerBound);
        pstmt1.setString(3, formattedUpperBound);
        pstmt1.setString(4, formattedLowerBound);
        rs = pstmt1.executeQuery();
        if (!rs.next()) {
            return getHistoricalBottleAvailability(capacity, lowerBoundSTR);
        }

        ArrayList<Bottle> resultList = new ArrayList<>();
        resultList.add(loadBottleToObject(rs, lowerBoundSTR));

        while (rs.next()) {
            resultList.add(loadBottleToObject(rs, lowerBoundSTR));
        }
        resultList.addAll(getHistoricalBottleAvailability(capacity, lowerBoundSTR));
        return resultList;
    }

    private ArrayList<Bottle> getHistoricalBottleAvailability(Integer capacity, String lowerBoundSTR) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        String formattedLowerBound = this.simpleDateFormat.format(convertStringToTimestamp(lowerBoundSTR));

        String upperBoundSTR = this.addOneDay(lowerBoundSTR);
        String formattedUpperBound = this.simpleDateFormat.format(convertStringToTimestamp(upperBoundSTR));

        query = "Select * from wine_production.hist_bottle where capacity_ml=? and (date>=? and date<? OR ? between date and end_date)";


        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, capacity);
        pstmt1.setString(2, formattedLowerBound);
        pstmt1.setString(3, formattedUpperBound);
        pstmt1.setString(4, formattedLowerBound);
        rs = pstmt1.executeQuery();

        if (!rs.next()) {
            return new ArrayList<>();
        }

        ArrayList<Bottle> resultList = new ArrayList<>();

        resultList.add(loadBottleToObject(rs, lowerBoundSTR));

        while (rs.next()) {
            resultList.add(loadBottleToObject(rs, lowerBoundSTR));
        }
        return resultList;
    }

    private Bottle loadBottleToObject(ResultSet rs, String lowerDateBound) throws SQLException {
        Bottle bottle = new Bottle();
        bottle.setId_type(rs.getInt("id_type"));
        bottle.setCapacity_ml(rs.getInt("capacity_ml"));
        bottle.setAvailable_qty(rs.getInt("available_qty"));
        bottle.setDate(lowerDateBound);
        return bottle;
    }

    private String addOneDay(String date) {
        LocalDate val = LocalDate.parse(date);
        val = val.plusDays(1);
        return val.toString();
    }

    protected static Timestamp convertStringToTimestamp(String strDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Date date = formatter.parse(strDate);
            return new Timestamp(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Check for critical minimum alerts
    public ArrayList<String> grapeCriticalMinimumAlert(Integer critical_minimum) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        query = "Select * from wine_production.sort_grape where available_qty<=?";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, critical_minimum);
        rs = pstmt1.executeQuery();

        ArrayList<String> resultList = new ArrayList<>();

        while (rs.next()) {
            resultList.add("Grape type called " + rs.getString("name") + " is with critical quantity of " + rs.getInt("available_qty") + ".\n");
        }

        return resultList;
    }

    public ArrayList<String> bottleTypeCriticalMinimumAlert(Integer critical_minimum) throws SQLException {
        ResultSet rs;
        String query;
        PreparedStatement pstmt1;

        query = "Select * from wine_production.bottle where available_qty<=?";

        pstmt1 = connection.prepareStatement(query);
        pstmt1.setInt(1, critical_minimum);
        rs = pstmt1.executeQuery();

        ArrayList<String> resultList = new ArrayList<>();

        while (rs.next()) {
            resultList.add("Bottle type with capacity of " + rs.getInt("capacity_ml") + " is with critical minimum of " + rs.getInt("available_qty") + ".\n");
        }

        return resultList;
    }

}

