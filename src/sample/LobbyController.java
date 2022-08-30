package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class LobbyController extends insideApp implements Initializable {

    @FXML
    private ImageView topLeftIconView;
    @FXML
    private ImageView botRightIconView;
    @FXML
    private ImageView botLeftIconView;

    @FXML
    private Button nutrientButton;
    @FXML
    private Button trackerButton;
    @FXML
    private Button plannerButton;

    @FXML
    private ProgressBar myProgressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private DatePicker myDatePicker;
    @FXML
    private Label dayLabel;
    double progress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //Pregnant Logo
        File topLeftFile = new File("Image/PREG.png");
        Image topLeftImage = new Image(topLeftFile.toURI().toString());
        topLeftIconView.setImage(topLeftImage);
        //Pie Chart Logo
        File botRightFile = new File("Image/PIE_CHART.png");
        Image botRightImage = new Image(botRightFile.toURI().toString());
        botRightIconView.setImage(botRightImage);
        //Calendar Logo
        File botLeftFile = new File("Image/CALENDAR.png");
        Image botLeftImage = new Image(botLeftFile.toURI().toString());
        botLeftIconView.setImage(botLeftImage);
        myProgressBar.setStyle("-fx-accent: red;"); //Colour of progress Bar
//        dailyAlert();
    }

    @FXML //Activate on Date Picker
    public void getDateNow() {
        //Load today date in "yyyy-MM-DD" format and parse it
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        //Get the value from the Date Picker and parse it
        LocalDate myDate = myDatePicker.getValue();
        String assign = myDate.toString();
        //Set the text to the different between two date
        dayLabel.setText(pregCount(assign, dateNow));
        //Calculate the difference and divide by 9 month to see how long until labor
        progress = Float.parseFloat(pregCount(assign, dateNow))/280;
        if (progress<0.9){
            //Set the progress bar percentage of progress
            myProgressBar.setProgress(progress);
            progressLabel.setText(((int)Math.round(progress * 100)) + "%");
        }
        }
    //Method to calculate difference between two given date
    private String pregCount(String past, String present){
        //Parsing it to float for calculation after picking substring from the date String
        float pastYear = Float.parseFloat(past.substring(0,4));
        float pastMonth = Float.parseFloat(past.substring(5,7));
        float pastDay = Float.parseFloat(past.substring(8,10));
        float presentYear = Float.parseFloat(present.substring(0,4));
        float presentMonth = Float.parseFloat(present.substring(5,7));
        float presentDay = Float.parseFloat(present.substring(8,10));
        float diffYear = presentYear-pastYear;
        float diffMonth = (presentMonth-pastMonth)+(diffYear*12);
        float diffDay = (presentDay-pastDay)+(diffMonth*30);
        if (diffDay>0){
            return String.valueOf((int)diffDay);
        } else {
            return "Error";
        }
    }

    public void calendarOnAction(){
        new loadTab("tracker.fxml", 1100, 650);
        Stage stage = (Stage) trackerButton.getScene().getWindow();
        stage.close();
    }
    public void nutrientOnAction(){
        new loadTab("nutrient.fxml",1100,650);
        Stage stage = (Stage) nutrientButton.getScene().getWindow();
        stage.close();
    }
    public void plannerOnAction(){
        new loadTab("planner.fxml",1100,650);
        Stage stage = (Stage) plannerButton.getScene().getWindow();
        stage.close();
    }

}