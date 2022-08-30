package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class insideApp{
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    @FXML
    private Button homeButton;
    @FXML
    private Button close;

    //Close the tab with button
    public void closeOnAction() {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    //Go to the home page from other page
    public void homeOnAction() {
        new loadTab("lobby.fxml",1100,650);
        Stage stage = (Stage) homeButton.getScene().getWindow();
        stage.close();
    }

    //Method to check if it numeric
    public static boolean isNotNumber(String str){
        try{
            Double.parseDouble(str);
            return false;
        }catch(NumberFormatException e){
            return true;
        }
    }

    //Method to get the account_id (key) from log
    public int getAccount(){
        int account_id = 0; //create variable so can be change later
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            //load statement into database by looking at log database descending order of log_id
            preparedStatement = connectDB.prepareStatement("SELECT * FROM log ORDER BY log_id DESC LIMIT 1");
            resultSet = preparedStatement.executeQuery();
            //loop while there more log to look at
            while (resultSet.next()) {
                account_id = resultSet.getInt("account_id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(account_id);
        return account_id;
    }
}