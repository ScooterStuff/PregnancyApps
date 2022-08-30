package sample;
import java.sql.Date;

public class modelNutTable {

    private Date date;
    private final String food;
    private final Double calories;
    private final Double mass;
    private final Double carb;
    private final Double protein;
    private final Double fibre;
    private final Double fat;

    public modelNutTable(Date date, String food, Double calories, Double mass, Double carb, Double protein, Double fibre, Double fat) {
        //Setting this to the parameter, otherwise it will be confuse with same name
        this.date = date;
        this.food = food;
        this.calories = calories;
        this.mass = mass;
        this.carb = carb;
        this.protein = protein;
        this.fibre = fibre;
        this.fat = fat;
    }
    //Getter method, to get and use the value after it being stored
    public Date getDate(){
        return date;
    }
    public void setDate(Date date){
        this.date = date;
    }
    public String getFood(){
        return food;
    }
    public Double getCalories(){
        return calories;
    }
    public Double getCarb(){
        return carb;
    }
    public Double getProtein(){
        return protein;
    }
    public Double getFibre(){
        return fibre;
    }
    public Double getFat(){
        return fat;
    }
    public Double getMass(){
        return mass;
    }
}