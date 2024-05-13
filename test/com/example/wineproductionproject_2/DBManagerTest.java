package com.example.wineproductionproject_2;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {

    @Test
    void getCurrentUserWithNotLoggedInUserShouldReturnNull() throws SQLException, ClassNotFoundException {
        User actualResult = DBManager.getInstance().getCurrentUser();

        assertNull(actualResult);
    }

    @Test
    void insertingUserShouldInsertNewUser() {
        try {
            String name = "newNameTest";
            String password = "newPasswordTest";
            int type = 1;

            String actualResult = DBManager.getInstance().insertUser(name, password, type);
            String expectedResult = "Username " + name + " has been registered.";

            DBManager.getInstance().deleteUser(name, password);
            assertEquals(expectedResult, actualResult);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertingUserShouldIndicateThatUserAlreadyExists() {
        try {
            String name = "newNameTest";
            String password = "newPasswordTest";
            int type = 1;
            DBManager.getInstance().insertUser(name, password, type);
            String actualResult = DBManager.getInstance().insertUser(name, password, type);
            String expectedResult = "Username " + name + " already exists.";

            DBManager.getInstance().deleteUser(name, password);
            assertEquals(expectedResult, actualResult);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deletionUserShouldDeleteUser() {
        try {
            String name = "newNameTest";
            String password = "newPasswordTest";
            int type = 1;

            DBManager.getInstance().insertUser(name, password, type);

            String actualResult = DBManager.getInstance().deleteUser(name, password);
            String expectedResult = "Successfully deleted user";

            assertEquals(expectedResult, actualResult);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deletionUserShouldIndicateThatUserNotExist() {
        try {
            String name = "newNameTest";
            String password = "newPasswordTest";

            String actualResult = DBManager.getInstance().deleteUser(name, password);
            String expectedResult = "That user does not exist";

            assertEquals(expectedResult, actualResult);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loginWithLegalUserShouldLogin() {
        String name = "newNameTest";
        String password = "newPasswordTest";
        int type = 1;

        try {
            DBManager.getInstance().insertUser(name, password, type);
            User actualResult = DBManager.getInstance().login(name, password);

            User expectedResult = new User("100", name, "1", password);

            DBManager.getInstance().deleteUser(name, password);

            assertEquals(expectedResult.getName(), actualResult.getName());
            assertEquals(expectedResult.getPassword(), actualResult.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loginWithInvalidUserShouldNotLogin() {
        String name = "newNameTest";
        String password = "newPasswordTest";
        int type = 1;

        try {
            User actualResult = DBManager.getInstance().login(name, password);

            assertNull(actualResult);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void logoutShouldLogout() {
        String name = "newNameTest";
        String password = "newPasswordTest";
        int type = 1;

        try {
            DBManager.getInstance().insertUser(name, password, type);
            DBManager.getInstance().login(name, password);
            DBManager.getInstance().logout();

            User actualResult = DBManager.getInstance().getCurrentUser();
            DBManager.getInstance().deleteUser(name, password);


            assertNull(actualResult);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertVarietyGrapeThatAlreadyExistShouldIndicate() {
        String name = "Merlot";
        Double amountLiquid = 1.11;
        Double availableQty = 1.11;
        int category = 1;

        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String actualResult = null;
        try {
            actualResult = DBManager.getInstance().insertVarietyGrape(name, amountLiquid, availableQty, category, date);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String expectedResult = "There is already registered Grape variety with that name. Try again!";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void insertVarietyGrapeShouldInsert() {
        String name = "testTest";
        Double amountLiquid = 1.11;
        Double availableQty = 1.11;
        int category = 1;

        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String actualResult = null;
        try {
            actualResult = DBManager.getInstance().insertVarietyGrape(name, amountLiquid, availableQty, category, date);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String expectedResult = "The new variety of grape named " + name + " has been registered.";
        assertEquals(expectedResult, actualResult);

        try {
            DBManager.getInstance().deleteGrapeVariety(name);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteGrapeVarietyShouldDelete() {
        String name = "testTest";
        Double amountLiquid = 1.11;
        Double availableQty = 1.11;
        int category = 1;

        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String actualResult = null;
        try {
            DBManager.getInstance().insertVarietyGrape(name, amountLiquid, availableQty, category, date);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String expectedResult = "Successfully deletion of grape variety named - '" + name + "'!";

        try {
            actualResult = DBManager.getInstance().deleteGrapeVariety(name);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCurrentAmountOfLiquid() {
        String name = "testTest";
        Double amountLiquid = 77d;
        Double availableQty = 1.11;
        int category = 1;

        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String actualResult = null;
        try {
            DBManager.getInstance().insertVarietyGrape(name, amountLiquid, availableQty, category, date);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        String expectedResult = String.valueOf(amountLiquid);

        try {
            actualResult = DBManager.getInstance().getCurrentAmountOfLiquid(name);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            DBManager.getInstance().deleteGrapeVariety(name);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getGrapeAvailableQty() throws SQLException, ClassNotFoundException {
        String name = "testTest";
        Double amountLiquid = 12d;
        Double availableQty = 700d;
        int category = 1;

        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Integer actualResult = null;
        try {
            DBManager.getInstance().insertVarietyGrape(name, amountLiquid, availableQty, category, date);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Integer expectedResult = (int) Math.round(availableQty);

        actualResult = DBManager.getInstance().getGrapeAvailableQty(name);

        try {
            DBManager.getInstance().deleteGrapeVariety(name);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void getGrapeAvailableQtyGrapeDoesNotExistShouldReturnZero() throws SQLException, ClassNotFoundException {
        String name = "testTest";
        Double amountLiquid = 12d;
        Double availableQty = 700d;
        int category = 1;
        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Integer actualResult = DBManager.getInstance().getGrapeAvailableQty(name);

        assertEquals(0, actualResult);
    }


    @Test
    void getWineTypesWithNoRecipe() throws SQLException, ClassNotFoundException {
        String name = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(name, obtainedQty);

        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithNoRecipe();

        DBManager.getInstance().deleteWineType(name);

        assertTrue(resultList.contains(name));
    }

    @Test
    void getWineTypesWithRecipeResultShouldNotIncludeWineWithNoRecipe() throws SQLException, ClassNotFoundException {
        String name = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(name, obtainedQty);

        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithRecipe();

        DBManager.getInstance().deleteWineType(name);

        assertFalse(resultList.contains(name));
    }

    @Test
    void registerWineRecipe() throws SQLException, ClassNotFoundException {
        //GrapeVariety data
        String grapeName = "testTest";
        Double amountLiquid = 2d;
        Double availableQty = 2d;
        int category = 1;
        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        DBManager.getInstance().insertVarietyGrape(grapeName, amountLiquid, availableQty, category, date);

        // wineType data
        String name = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(name, obtainedQty);

        //recipeData
        ArrayList<String> rows = new ArrayList<>();
        rows.add(grapeName + "\t\t" + 1.5);

        DBManager.getInstance().registerWineRecipe(name, rows);

        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithRecipe();

        assertTrue(resultList.contains(name));

        DBManager.getInstance().deleteWineRecipe(name);
        DBManager.getInstance().deleteWineType(name);
        DBManager.getInstance().deleteGrapeVariety(grapeName);
    }

    @Test
    void registerWineType() throws SQLException, ClassNotFoundException {
        String name = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(name, obtainedQty);
        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithNoRecipe();

        DBManager.getInstance().deleteWineType(name);

        assertTrue(resultList.contains(name));

    }

    @Test
    void deleteWineType() throws SQLException, ClassNotFoundException {
        String name = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(name, obtainedQty);
        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithNoRecipe();

        DBManager.getInstance().deleteWineType(name);

        assertTrue(resultList.contains(name));

        resultList = DBManager.getInstance().getWineTypesWithNoRecipe();

        assertFalse(resultList.contains(name));
    }

    @Test
    void deleteWineRecipe() throws SQLException, ClassNotFoundException {
        //GrapeVariety data
        String grapeName = "testTest";
        Double amountLiquid = 2d;
        Double availableQty = 2d;
        int category = 1;
        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        DBManager.getInstance().insertVarietyGrape(grapeName, amountLiquid, availableQty, category, date);

        // wineType data
        String name = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(name, obtainedQty);

        //recipeData
        ArrayList<String> rows = new ArrayList<>();
        rows.add(grapeName + "\t\t" + 1.5);

        DBManager.getInstance().registerWineRecipe(name, rows);

        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithRecipe();

        assertTrue(resultList.contains(name));

        DBManager.getInstance().deleteWineRecipe(name);
        DBManager.getInstance().deleteWineType(name);
        DBManager.getInstance().deleteGrapeVariety(grapeName);

        resultList = DBManager.getInstance().getWineTypesWithRecipe();

        assertFalse(resultList.contains(name));
    }


    @Test
    void allProcessFromIntroducingNewGrapeVarietyToBottleWineWithValidDataShouldPerformOperations() throws SQLException, ClassNotFoundException {
        //GrapeVariety data
        String grapeName = "testTest";
        Double amountLiquid = 2d;
        Double availableQty = 2000d;
        int category = 1;


        String date = null;
        LocalDateTime now = LocalDateTime.now();
        String myFormatedDate = null;

        try {
            date = now.format(DBManager.getInstance().dateFormat);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        DBManager.getInstance().insertVarietyGrape(grapeName, amountLiquid, availableQty, category, date);

        // wineType data
        String wineName = "TestWineTypeTest";
        Integer obtainedQty = 100;

        DBManager.getInstance().registerWineType(wineName, obtainedQty);

        //recipeData
        ArrayList<String> rows = new ArrayList<>();
        rows.add(grapeName + "\t\t" + 150);

        DBManager.getInstance().registerWineRecipe(wineName, rows);

        ArrayList<String> resultList = DBManager.getInstance().getWineTypesWithRecipe();

        assertTrue(resultList.contains(wineName));

        // produceWine
        DBManager.getInstance().produceWine(wineName, 5);
        ArrayList<String> listOfProducedWines = DBManager.getInstance().getProducedWineTypes();
        assertTrue(listOfProducedWines.contains(wineName));

        // Bottle wine
        DBManager.getInstance().bottleWine(wineName, 10d, "2022-01-08 11:56:40");
        SortedSet<String> bottledWineList = DBManager.getInstance().getBottledWine();
        assertTrue(bottledWineList.contains(wineName));


        // delete all
        DBManager.getInstance().deleteBottledWine(wineName);
        DBManager.getInstance().deleteProducedWine(wineName);
        DBManager.getInstance().deleteWineRecipe(wineName);
        DBManager.getInstance().deleteWineType(wineName);
        DBManager.getInstance().deleteGrapeVariety(grapeName);
    }

}