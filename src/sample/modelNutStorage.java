package sample;
import java.sql.Date;

public class modelNutStorage{

    private final Date dateAlone;
    private final double totalCalories;
    private final double totalMass;

    public modelNutStorage(Date dateAlone, double totalCalories, double totalMass) {
        //Setting this dateCal to the parameter, otherwise it will be confuse with same name
        this.dateAlone = dateAlone;
        this.totalCalories = totalCalories;
        this.totalMass = totalMass;
    }
    //Getter method, to get and use the value after it being stored
    public Date getDateAlone(){
        return dateAlone;
    }
    public Double getTotalCalories(){
        return totalCalories;
    }
    public Double getTotalMass(){
        return totalMass;
    }
}

