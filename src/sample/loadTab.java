package sample;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Method to open new tab with minimal line of code and input
public class loadTab {
    public loadTab(String fxmlFile, int width, int height){
        try{
            //Getting fxml from the code
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage Stage = new Stage();
            Stage.initStyle(StageStyle.UNDECORATED);
            //Setting it size following the set width and height
            Stage.setScene(new Scene(root, width, height));
            Stage.show();
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }
}
