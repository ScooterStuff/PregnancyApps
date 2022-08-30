package sample;

import javafx.event.ActionEvent;
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
import java.sql.Connection;
import java.sql.Statement;
import java.util.ResourceBundle;


public class RegisterController extends DatabaseConnection implements Initializable {

    //This is to indicate the @FXML that I'm using
    @FXML
    private ImageView shieldImageView;
    @FXML
    private Button closeButton;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //Loading Image
        File shieldFile = new File("OMO/logo.png");
        Image shieldImage = new Image(shieldFile.toURI().toString());
        shieldImageView.setImage(shieldImage);
    }
    @FXML
    public void close(ActionEvent event){
        new loadTab("login.fxml",520,440);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void registerButtonOnAction(ActionEvent event) {
        //Check if any field is empty
        if (firstnameTextField.getText().isBlank() && lastnameTextField.getText().isBlank() &&
                confirmPasswordField.getText().isBlank() && setPasswordField.getText().isBlank() &&
                usernameTextField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill in the data!");
            alert.showAndWait();
        //Check if password is equal to confirm password
        }else if(!(setPasswordField.getText().equals(confirmPasswordField.getText()))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Password not match!");
            alert.showAndWait();
        //Check if username is already used, recalling method from DatabaseConnection
        }else if((CheckUsernameExists(usernameTextField.getText()))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Username already exist!");
            alert.showAndWait();
        }else{
            registerUser();
        }
    }
    //Method to put registry data that entered from field into database
    public void registerUser(){
        //Connect Datbase
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        //Getting Data from TextField
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String username = usernameTextField.getText();
        String password = setPasswordField.getText();
        //SQL commands to enter this data into new row on user_account table in database
        String insertFields = "INSERT INTO user_account(lastname, firstname, username, password) VALUES ('";
        String insertValues = firstname +"','"+ lastname +"','"+ username +"','"+ password +"')";
        String insertToRegister = insertFields + insertValues;
        try{
            //Execute Command
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
            //Give Alert to show completion
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("User enter data successfully");
            alert.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }
}
