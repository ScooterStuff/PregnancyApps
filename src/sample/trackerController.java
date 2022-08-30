package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class trackerController extends insideApp implements Initializable {
    @FXML
    private DatePicker myDatePicker;
    @FXML
    private CheckBox menstruation;
    @FXML
    private TextField tempField;
    @FXML
    private TextField weightField;

    @FXML
    private TableView<modelTrackTable> table;
    @FXML
    private TableColumn<modelTrackTable, String> colDate;
    @FXML
    private TableColumn<modelTrackTable, String> colTemp;
    @FXML
    private TableColumn<modelTrackTable, String> colWeight;
    @FXML
    private TableColumn<modelTrackTable, String> colMens;

    modelTrackTable trackTable = null;
    int account_id = getAccount();
    ObservableList<modelTrackTable> trackerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load();
    }

    public void tempOnAction(){
        new loadTab("temp.fxml",1000,550);
    }

    //Method to load the table and display each data from database to column
    private void load(){
        //Connect to Database
        DatabaseConnection connectNow = new DatabaseConnection();
        connectNow.getConnection();
        refreshTable();
        //Setting the value of each column to the respected data from method above
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCal"));
        colTemp.setCellValueFactory(new PropertyValueFactory<>("temp"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colMens.setCellValueFactory(new PropertyValueFactory<>("mens"));
        table.setItems(trackerList);//Display the data
    }

    @FXML //Getting data from database and adding it to table
    private void refreshTable() {
        try {
            trackerList.clear();
            //Connect to Database
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            //Select data from tracker table sort by date
            preparedStatement = connectDB.prepareStatement("SELECT * FROM tracker WHERE user_id ="+account_id+" ORDER by dateCal DESC" );
            resultSet = preparedStatement.executeQuery();
            //Loop through the result from table while getting data from each row
            while (resultSet.next()){
                trackerList.add(new modelTrackTable(
                        resultSet.getDate("dateCal"),
                        resultSet.getDouble("temp"),
                        resultSet.getDouble("weight"),
                        resultSet.getString("mens")));
                table.setItems(trackerList);
            }
        } catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void save() {
        //Connect Database
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String mens = "No"; //Declaring Variable for menstruation
        if(menstruation.isSelected()){ mens = "Yes"; } //Check if box is tick
        boolean FLAG = false; //Declaring FLAG

        //Check if value is insert in field
        if(myDatePicker.getValue() == null || weightField.getText() == null || tempField.getText() == null){
            Alert alertEmpty = new Alert(Alert.AlertType.ERROR);
            alertEmpty.setHeaderText(null);
            alertEmpty.setContentText("Please fill in the data");
            alertEmpty.showAndWait();
        //Making sure that the enter value is number and not integer
        } else if(isNotNumber(weightField.getText()) || isNotNumber(tempField.getText())){
            Alert alertEmpty = new Alert(Alert.AlertType.ERROR);
            alertEmpty.setHeaderText(null);
            alertEmpty.setContentText("Data is not numeric!");
            alertEmpty.showAndWait();
            clean();
        }
        else{
            Double tempStore= Double.parseDouble(tempField.getText()); //Getting from field
            tempStore = Math.round(tempStore*10.0)/10.0; //Rounding it to 2 sf
            String temp = String.valueOf(tempStore); //Parsing back to String to be insert

            Double weightStore= Double.parseDouble(weightField.getText()); //Getting from field
            weightStore = Math.round(weightStore*10.0)/10.0; //Rounding it to 2 sf
            String weight = String.valueOf(weightStore); //Parsing back to String to be insert

            String dateCal = String.valueOf(myDatePicker.getValue()); //Parsing back to String to be insert
            try {
                //Select data from tracker table sort by date and user_id via SQL command
                preparedStatement = connectDB.prepareStatement("SELECT * FROM tracker WHERE user_id ="+account_id+" ORDER by user_id, datecal DESC");
                resultSet = preparedStatement.executeQuery();
                //Loop through result to check if Date already exist
                while(resultSet.next() && !FLAG) {
                    Date date = resultSet.getDate("dateCal");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String calDay = dateFormat.format(date);
                    if (calDay.equals(dateCal)) {
                        FLAG = true;
                    }
                //If date already exist
                }if(FLAG){
                    Alert alertEmpty = new Alert(Alert.AlertType.ERROR);
                    alertEmpty.setHeaderText(null);
                    alertEmpty.setContentText("Date Already been used");
                    alertEmpty.showAndWait();
                }
                //If date doesn't already exist
                else {
                    clean();
                    //SQL commands to enter this data into new row on tracker table in database
                    String insertFields = "INSERT INTO tracker(dateCal, temp, weight, mens, user_id) VALUES ('";
                    String insertValues = dateCal +"','"+ temp +"','"+ weight +"','"+ mens+"','"+ account_id +"')";
                    String insertToCalendar = insertFields + insertValues;
                    try{
                        Statement statement = connectDB.createStatement();
                        statement.executeUpdate(insertToCalendar);
                        refreshTable();
                    }catch (Exception e){
                        e.printStackTrace();
                        e.getCause();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    @FXML
    private void deleteCell() {
        try {
            trackTable = table.getSelectionModel().getSelectedItem(); //Get value of the selected row
            DatabaseConnection connectNow = new DatabaseConnection(); //Connect Database
            Connection connectDB = connectNow.getConnection();
            //SQL commands to delete this data into new row on tracker table in database
            preparedStatement = connectDB.prepareStatement("DELETE FROM tracker WHERE temp ="+trackTable.getTemp()+
                    " AND weight ="+trackTable.getWeight()+ " AND user_id = " + account_id);
            preparedStatement.execute();
            //Load table again with new value
            refreshTable();
        } catch (SQLException ex) {
            Logger.getLogger(trackerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML //Method to remove text in the textField
    private void clean() {
        tempField.setText(null);
        weightField.setText(null);
    }
    public void getDateNow(){
        LocalDate myDate = myDatePicker.getValue();
    }

}