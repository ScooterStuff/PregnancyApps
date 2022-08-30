package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private Button cancelButton;

    @FXML
    private ImageView lockImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Integer giveUserID = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void loginButtonOnAction() {
        //Check if the field is blank
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in the data!");
            alert.showAndWait();
            //if field is blank it alert the user with appropriate message
        }
    }
    @FXML
    public void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void signupButtonOnAction() {
        new loadTab("register.fxml", 520, 445);
        Stage stage = (Stage) signupButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        //Connect to Database
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        //Getting username and password from the field
        String username = usernameTextField.getText();
        String password = enterPasswordField.getText();
        //Select and find number of row from user_account that have the entered username and password
        String verifyLogin = "SELECT count(*) FROM user_account WHERE username = '" +
                username + "' AND password ='" + password + "'";
        try {
            //Load up the SQL Command
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
            //Loop through all the select result
            while (queryResult.next()) {
                //Check if the total number of row that is found is equal to one
                //Validating that username and password exist within database
                if (queryResult.getInt(1) == 1) {
                    try {
                        preparedStatement = connectDB.prepareStatement("SELECT * FROM user_account WHERE username = '" +
                                username + "'");
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            giveUserID = resultSet.getInt("account_id");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    String storeData = "INSERT INTO log(username, account_id) VALUES('"+username +"','"+ giveUserID +"')";
                    try {
                        Statement statementOne = connectDB.createStatement();
                        statementOne.executeUpdate(storeData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        e.getCause();
                    }
                    //Today Date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dateNow = sdf.format(new Date());
                    boolean FLAG = false; //Declaring FLAG

                    try {
                        //Select data from tracker table descending as we want the earilest date
                        preparedStatement = connectDB.prepareStatement("SELECT * FROM tracker WHERE user_id ="+giveUserID+" ORDER BY dateCal DESC;");
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()&& !FLAG) {
                        if((dateNow.equals(String.valueOf(resultSet.getDate("dateCal"))))){
                            FLAG = true;
                        }
                        }
                        if (!FLAG){
                            Alert alertEmpty = new Alert(Alert.AlertType.ERROR);
                            alertEmpty.setHeaderText(null);
                            alertEmpty.setContentText("Fill in tracker data for today!");
                            alertEmpty.showAndWait();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    //Go to main page
                    new loadTab("lobby.fxml", 1100, 650);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.close();
                } else { //Login Failed with alert Message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid Login please try again!");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}