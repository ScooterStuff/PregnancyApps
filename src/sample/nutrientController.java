package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nutrientController extends insideApp implements Initializable {

    @FXML
    private TextField foodField;
    @FXML
    private TextField calField;
    @FXML
    private TextField carbField;
    @FXML
    private TextField proteinField;
    @FXML
    private TextField fibreField;
    @FXML
    private TextField fatField;
    @FXML
    private Label indicator;


    @FXML
    private TableColumn<modelNutStorage, String> colTotalCalories;
    @FXML
    private TableColumn<modelNutStorage, String> colTotalMass;
    @FXML
    private TableColumn<modelNutStorage, String> colDateAlone;
    @FXML
    private TableView<modelNutStorage> totalTable;

    @FXML
    private TableView<modelNutTable> table;
    @FXML
    private TableColumn<modelNutTable, String> colDate;
    @FXML
    private TableColumn<modelNutTable, String> colFood;
    @FXML
    private TableColumn<modelNutTable, String> colCalories;
    @FXML
    private TableColumn<modelNutTable, String> colMass;
    @FXML
    private TableColumn<modelNutTable, String> colCarb;
    @FXML
    private TableColumn<modelNutTable, String> colProtein;
    @FXML
    private TableColumn<modelNutTable, String> colFibre;
    @FXML
    private TableColumn<modelNutTable, String> colFat;
    @FXML
    private PieChart pieChart;
    modelNutTable someNut = null;
    modelNutStorage someNutStorage = null;
    int account_id = getAccount();
    ObservableList<modelNutTable> nutList = FXCollections.observableArrayList();
    ObservableList<modelNutStorage> totalNutList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load();
        loadStorage();
    }

    @FXML //Getting data from database and adding it to table
    private void refreshTable() {
        try {
            nutList.clear();
            //Connect to Database
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            //Select data from nutrient table showing only today date
            preparedStatement = connectDB.prepareStatement("SELECT * FROM nutrient WHERE date = '"+today+"' AND user_id = "+account_id);
            resultSet = preparedStatement.executeQuery();
            //Loop through the result from table while getting data from each row
            while (resultSet.next()){
                nutList.add(new modelNutTable(
                        resultSet.getDate("date"),
                        resultSet.getString("food"),
                        resultSet.getDouble("calories"),
                        resultSet.getDouble("mass"),
                        resultSet.getDouble("carb"),
                        resultSet.getDouble("protein"),
                        resultSet.getDouble("fibre"),
                        resultSet.getDouble("fat")));
                table.setItems(nutList);
            }
        } catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Method to load the table and display each data from database to column
    private void load(){
        //Connect to Database
        DatabaseConnection connectNow = new DatabaseConnection();
        connectNow.getConnection();
        refreshTable();
        //Setting the value of each column to the respected data from method above
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colFood.setCellValueFactory(new PropertyValueFactory<>("food"));
        colCalories.setCellValueFactory(new PropertyValueFactory<>("calories"));
        colMass.setCellValueFactory(new PropertyValueFactory<>("mass"));
        colCarb.setCellValueFactory(new PropertyValueFactory<>("carb"));
        colProtein.setCellValueFactory(new PropertyValueFactory<>("protein"));
        colFibre.setCellValueFactory(new PropertyValueFactory<>("fibre"));
        colFat.setCellValueFactory(new PropertyValueFactory<>("fat"));
        table.setItems(nutList);//Display the data
    }

    //Getting data from database and adding it to table
    private void loadStorage(){
        //Connect to Database
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        totalNutList.clear();
        try{
            //Select data from nutrientSummary table
            preparedStatement = connectDB.prepareStatement("SELECT * FROM nutrientSummary WHERE user_id ="+account_id);
            resultSet = preparedStatement.executeQuery();

            //Loop through the result from table while getting data from each row
            while (resultSet.next()) {
                totalNutList.add(new modelNutStorage(
                        resultSet.getDate("dateAlone"),
                        resultSet.getDouble("totalCalories"),
                        resultSet.getDouble("totalMass")));
                totalTable.setItems(totalNutList);
            }
        }catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Setting the value of each column to the respected data
        colDateAlone.setCellValueFactory(new PropertyValueFactory<>("dateAlone"));
        colTotalCalories.setCellValueFactory(new PropertyValueFactory<>("totalCalories"));
        colTotalMass.setCellValueFactory(new PropertyValueFactory<>("totalMass"));
        totalTable.setItems(totalNutList); //Display the Data onto Table
    }
    @FXML
    private void save() {
        pieChart.getData().clear();//Reset Database
        //Connect Database
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datenow = sdf.format(new Date());
        //Getting Data from Field
        String food = foodField.getText();
        String calories = calField.getText();
        String carb = carbField.getText();
        String protein = proteinField.getText();
        String fibre = fibreField.getText();
        String fat = fatField.getText();

        //Check if field is empty
        if (food.isEmpty() || calories.isEmpty() || carb.isEmpty()|| protein.isEmpty()|| fibre.isEmpty()|| fat.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();
        } else if(isNotNumber(calories) || isNotNumber(carb) || isNotNumber(protein) || isNotNumber(fibre)|| isNotNumber(fat)){
            Alert alertEmpty = new Alert(Alert.AlertType.ERROR);
            alertEmpty.setHeaderText(null);
            alertEmpty.setContentText("Data is not numeric!");
            alertEmpty.showAndWait();
            clean();
        }

        else {
            clean();
            //Parsing to Integer
            int tempCarb = Integer.parseInt(carb);
            int tempProtein = Integer.parseInt(protein);
            int tempFibre = Integer.parseInt(fibre);
            int tempFat = Integer.parseInt(fat);
            int temporaryMass = tempCarb + tempProtein + tempFibre + tempFat;//Calculating total Mass
            //Calculate mass percentage respected to the total mass
            int carbPercent = tempCarb/temporaryMass;
            int proteinPercent = tempProtein/temporaryMass;
            int fatPercent = tempFat/temporaryMass;
            String mass = String.valueOf(temporaryMass);

            //SQL commands to enter this data into new row on nutrient table in database
            String insertFields = "INSERT INTO nutrient(date, food, calories, mass, carb, protein, fibre, fat, user_id) VALUES ('";
            String insertValues = datenow +"','"+ food +"','"+ calories +"','"+ mass +"','"+carb+"','"+protein+"','"+
                    fibre+"','"+fat+"','"+account_id+"')";
            String insertToNutrient = insertFields + insertValues;

            //Checking if meal is healthy and displaying message using condition from health website
            if (((0.45<carbPercent)||(carbPercent<0.65))&&((0.2<proteinPercent)||(proteinPercent<0.35))&&
                    (tempFibre>30)&&((0.2<fatPercent)||(fatPercent<0.35))){
                indicator.setText("This meal is balanced \n and healthy");
                indicator.setTextFill(Color.web("#008450",0.8));
            }else{
                indicator.setText("This meal is unbalanced");
                indicator.setTextFill(Color.web("#B81D13",0.8));
            }
            //Setting the piechart with given data
            PieChart.Data slice1 = new PieChart.Data("Carbohydrate", tempCarb);
            PieChart.Data slice2 = new PieChart.Data("Protein", tempProtein);
            PieChart.Data slice3 = new PieChart.Data("Fibre", tempFibre);
            PieChart.Data slice4 = new PieChart.Data("Fat", tempFat);
            pieChart.getData().add(slice1);
            pieChart.getData().add(slice2);
            pieChart.getData().add(slice3);
            pieChart.getData().add(slice4);

            try{
                Statement statement = connectDB.createStatement();
                statement.executeUpdate(insertToNutrient);
                refreshTable();
            }catch (Exception e){
                e.printStackTrace();
                e.getCause();
            }
            clean();
        }
    }
    @FXML
    private void deleteNutCell() {
        try {
            someNut = table.getSelectionModel().getSelectedItem();//Get value of the selected row
            DatabaseConnection connectNow = new DatabaseConnection();//Connect Database
            Connection connectDB = connectNow.getConnection();
            //SQL commands to delete this data into new row on nutrient table in database
            preparedStatement = connectDB.prepareStatement("DELETE FROM nutrient WHERE fat = "+someNut.getFat()+ " AND calories = "+someNut.getCalories()+ " AND user_id = " + account_id);
            preparedStatement.execute();
            refreshTable();
        } catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void deleteNutStorage() {
        try {
            someNutStorage = totalTable.getSelectionModel().getSelectedItem();//Get value of the selected row
            DatabaseConnection connectNow = new DatabaseConnection();//Connect Database
            Connection connectDB = connectNow.getConnection();
            //SQL commands to delete this data into new row on nutrientSummary table in database
            preparedStatement = connectDB.prepareStatement("DELETE FROM nutrientSummary WHERE totalCalories = "+someNutStorage.getTotalCalories()+
                    " AND totalMass = "+someNutStorage.getTotalMass()+ " AND user_id = " + account_id);
            preparedStatement.execute();
            loadStorage();
        } catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML //Generate Table for the the total intake that day
    public void generateDailyTable(){
        //Generating today date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datenow = sdf.format(new Date());
        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            //Selecting data from nutrient table from today only
            preparedStatement = connectDB.prepareStatement("SELECT * FROM nutrient WHERE date='"+datenow+"' AND user_id = "+account_id);
            resultSet = preparedStatement.executeQuery();

            double tempTotalCalories = 0;
            double tempTotalMass = 0;
            //Loop through result of today nutrient and sum them all up
            while(resultSet.next()){
                tempTotalCalories = tempTotalCalories+resultSet.getDouble("calories");
                tempTotalMass = tempTotalMass+resultSet.getDouble("mass");
            }
            //SQL commands to enter this data into new row on nutrientSummary table in database
            String insertFields = "INSERT INTO nutrientSummary(dateAlone, totalCalories, totalMass, user_id) VALUES ('";
            String insertValues = datenow +"','"+ tempTotalCalories +"','"+ tempTotalMass+"','"+ account_id+"')";
            String insertToTotalNutrient = insertFields + insertValues;
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToTotalNutrient);

        } catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadStorage();
    }
    @FXML //Method to remove text in the textField
    private void clean() {
        foodField.setText(null);
        calField.setText(null);
        carbField.setText(null);
        proteinField.setText(null);
        fibreField.setText(null);
        fatField.setText(null);
    }
}