package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class tempGraphController extends insideApp implements Initializable {

    @FXML
    private LineChart<?, ?> tempGraph; //Declaring LineChart
    int account_id = getAccount();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
          generate();
    }

    //Generate Temperature Graph
    public void generate(){
        //Connect to Database
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            //Selecting data from database order by date
            preparedStatement = connectDB.prepareStatement("SELECT * FROM tracker WHERE user_id ="+account_id+" ORDER by dateCal");
            resultSet = preparedStatement.executeQuery();
            XYChart.Series temperature = new XYChart.Series();
            //Loop through result and set X axis to date and Y axis to temperature
            while(resultSet.next()) {
                temperature.getData().add(new XYChart.Data(convertDateToString(resultSet.getDate("dateCal")), resultSet.getDouble("temp")));
            }
            tempGraph.getData().addAll(temperature);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    //Method to convert Date to String
    public static String convertDateToString(Date indate) {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
        try{
            dateString = sdfr.format(indate);
        }catch (Exception ex ){
            System.out.println(ex);
        }
        return dateString;
    }
}
