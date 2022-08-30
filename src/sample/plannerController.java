package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class plannerController extends insideApp implements Initializable {
    @FXML
    private TextField taskField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField addField;

    @FXML
    private ComboBox<String> timeEnter;
    @FXML
    private ComboBox<String> dayEnter;
    @FXML
    private ComboBox<String> timeDelete;
    @FXML
    private ComboBox<String> dayDelete;
    @FXML
    private Button deleteButton;
    @FXML
    private Button enterButton;
    @FXML
    private ListView<String> commonTaskList;
    @FXML
    private ListView<String> commonLocationList;

    @FXML
    private ColorPicker colorPicker;

        int account_id = getAccount();
        private int DE = 0;
        private int TE = 0;
        private int TD = 0;
        private int DD = 0;
        @FXML
        private GridPane matrix;
        private final Label[][] label = new Label[7][14]; //Declaring size of the Label Array
        private final Pane[][] pane = new Pane[7][14]; // Creating Pane with size of Array

        //Setting up value for the ComboBox time and day
        ObservableList<String> digTime = FXCollections.observableArrayList(
                "7:00", "8:00", "9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00",
                "17:00","18:00","19:00","20:00");
        ObservableList<String> daily = FXCollections.observableArrayList(
                "Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday","Sunday");

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            timeEnter.setItems(digTime);
            dayEnter.setItems(daily);
            timeDelete.setItems(digTime);
            dayDelete.setItems(daily);
            setMatrix();
            generateLocationList();
            generateTaskList();
            chooseTask();
            chooseLocation();
        }
    //Method to load the matrix grid
    private void setMatrix(){
        //Adding pane and text to the multidimensional array, to later store item
        for (int i = 0; i < label.length; i++) {
            for (int j = 0; j < label[i].length; j++) {
                label[i][j] = new Label();
                pane[i][j] = new Pane();
                matrix.add(pane[i][j], i, j);
                //Set default background color
                pane[i][j].setStyle("-fx-background-color: #F2C2C2;");
            }
        }
        try {
            //Connect Database
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            //Select data from planner table
            preparedStatement = connectDB.prepareStatement("SELECT * FROM planner WHERE user_id ="+account_id);
            resultSet = preparedStatement.executeQuery();
            //Loop through result
            while (resultSet.next()) {
                int theDay = resultSet.getInt("day"); // Position of Horizontal Array
                int theTime = resultSet.getInt("time"); // Position of Vertical Array
                //Using the position key set the text there
                label[theDay][theTime].setText(resultSet.getString("task") +"\n" +
                        resultSet.getString("location") +"\n"+
                        resultSet.getString("addition"));
                //Display on the matrix grid
                matrix.add(label[theDay][theTime], theDay, theTime);
                //Look up the color column in database and get the hex id of it then make the position that color
                String colorHex = resultSet.getString("color").substring(2,8);
                pane[theDay][theTime].setStyle("-fx-background-color: #" +colorHex+ ";");
                GridPane.setHalignment(label[theDay][theTime], HPos.CENTER);
            }
        } catch (SQLException ex) {
            Logger.getLogger(plannerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void save() {
        DatabaseConnection connectNow = new DatabaseConnection(); //Connect Database
        Connection connectDB = connectNow.getConnection();
        //Get the data from the field entered
        String task = taskField.getText();
        String location = locationField.getText();
        String addition = addField.getText();
        Color paneColor = colorPicker.getValue();

        //Check all value is entered
        if (timeEnter.getValue() == null && dayEnter.getValue() == null
                || task.isEmpty() || location.isEmpty() || addition.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill in the Data!");
            alert.showAndWait();
        } else {
            //Recall Method that convert ComboBox value to that of position array(INTEGER)
            timeToArray();
            dayToArray();
            //Initializing value to the value get from the method
            int day = DE;
            int time = TE;
            boolean FLAG = false; //Setting up flag
            try {
                //Select the table planner
                preparedStatement = connectDB.prepareStatement("SELECT * FROM planner WHERE user_id ="+account_id);
                resultSet = preparedStatement.executeQuery();
                //Loop through result for the value checking if both DAY and TIME are identical to database
                //Avoiding overlapping of data
                while (resultSet.next() && !FLAG) {
                    int theDay = resultSet.getInt("day");
                    int theTime = resultSet.getInt("time");
                    if ((DE == theDay) && (TE == theTime)) {
                        FLAG = true; //When there is identical match stop this loop
                    }
                }
                //Identical match will result in alert message
                if (FLAG) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Date and Time already used!");
                    alert.showAndWait();
                } else {
                    //SQL commands to enter this data into new row on planner table in database
                    String insertFields = "INSERT INTO planner(task, location, addition, day, time, color, user_id) VALUES ('";
                    String insertValues = task + "','" + location + "','" + addition + "','" + day + "','" + time + "','" +paneColor + "','"+ account_id + "')";
                    String insertToPlanner = insertFields + insertValues;
                    try {
                        Statement statement = connectDB.createStatement();
                        statement.executeUpdate(insertToPlanner);

                        new loadTab("planner.fxml",1100,650);//Refresh the page
                        Stage stage = (Stage) enterButton.getScene().getWindow();
                        stage.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        e.getCause();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    @FXML //On action button to delete the select array
    private void deleteArray() {
        //Check if user entered both value for position array
        if(timeDelete.getValue() != null && dayDelete.getValue() != null) {
            //Recall Method that convert ComboBox value to that of position array(INTEGER)
            dayToDelete();
            timeToDelete();
            try {
                //Connect Database
                DatabaseConnection connectNow = new DatabaseConnection();
                Connection connectDB = connectNow.getConnection();
                //Selected deleting only place with the position array input
                preparedStatement = connectDB.prepareStatement("DELETE FROM planner WHERE day = " + DD + " AND time = " + TD+ " AND user_id = " + account_id);
                preparedStatement.execute();
                new loadTab("planner.fxml", 1100, 650); //Refresh Page
                Stage stage = (Stage) deleteButton.getScene().getWindow();
                stage.close();
            } catch (SQLException ex) {
                Logger.getLogger(plannerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Switch for the value entered with ComboBox changing it value to position array
    private void timeToArray() {
        //Get value from the comboBox
        String x = timeEnter.getValue();
        switch (x) {
            case "7:00" -> TE = 0;
            case "8:00" -> TE = 1;
            case "9:00" -> TE = 2;
            case "10:00" -> TE = 3;
            case "11:00" -> TE = 4;
            case "12:00" -> TE = 5;
            case "13:00" -> TE = 6;
            case "14:00" -> TE = 7;
            case "15:00" -> TE = 8;
            case "16:00" -> TE = 9;
            case "17:00" -> TE = 10;
            case "18:00" -> TE = 11;
            case "19:00" -> TE = 12;
            case "20:00" -> TE = 13;
        }
    }
    private void dayToArray() {
        String y = dayEnter.getValue();
        switch (y) {
            case "Monday" -> DE = 0;
            case "Tuesday" -> DE = 1;
            case "Wednesday" -> DE = 2;
            case "Thursday" -> DE = 3;
            case "Friday" -> DE = 4;
            case "Saturday" -> DE = 5;
            case "Sunday" -> DE = 6;
        }
    }
    private void timeToDelete() {
        String x = timeDelete.getValue();
        switch (x) {
            case "7:00" -> TD = 0;
            case "8:00" -> TD = 1;
            case "9:00" -> TD = 2;
            case "10:00" -> TD = 3;
            case "11:00" -> TD = 4;
            case "12:00" -> TD = 5;
            case "13:00" -> TD = 6;
            case "14:00" -> TD = 7;
            case "15:00" -> TD = 8;
            case "16:00" -> TD = 9;
            case "17:00" -> TD = 10;
            case "18:00" -> TD = 11;
            case "19:00" -> TD = 12;
            case "20:00" -> TD = 13;
        }
    }
    private void dayToDelete() {
        String y = dayDelete.getValue();
        switch (y) {
            case "Monday" -> DD = 0;
            case "Tuesday" -> DD = 1;
            case "Wednesday" -> DD = 2;
            case "Thursday" -> DD = 3;
            case "Friday" -> DD = 4;
            case "Saturday" -> DD = 5;
            case "Sunday" -> DD = 6;
        }
    }
    //ArrayList to add task that are commonly use
    private void generateTaskList(){
        ArrayList<String> commonTask = new ArrayList<>();
        commonTask.add("Sleep");
        commonTask.add("Work");
        commonTask.add("Breakfast");
        commonTask.add("Lunch");
        commonTask.add("Dinner");
        commonTask.add("Exercise");
        commonTask.add("Leisure");
        commonTask.add("Shopping");
        //Display Task
        for (String useTask : commonTask){
            commonTaskList.getItems().add(useTask);
        }
    }
    private void generateLocationList(){
        ArrayList<String> commonLocation = new ArrayList<>();
        commonLocation.add("Home");
        commonLocation.add("Office");
        commonLocation.add("Central");

        for (String useLocation : commonLocation){
            commonLocationList.getItems().add(useLocation);
        }
    }
    //MouseOnClick action to choose the combobox
    private void chooseTask(){
        commonTaskList.setOnMouseClicked(event ->{
            String item = commonTaskList.getSelectionModel().getSelectedItem();
            taskField.setText(item);
        });
    }
    private void chooseLocation(){
        commonLocationList.setOnMouseClicked(event ->{
            String item = commonLocationList.getSelectionModel().getSelectedItem();
            locationField.setText(item);
        });
    }
}