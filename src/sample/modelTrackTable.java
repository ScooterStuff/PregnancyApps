package sample;
import java.sql.Date;

public class modelTrackTable {
    private final Date dateCal;
    private final Double temp;
    private final Double weight;
    private final String mens;

    public modelTrackTable(Date dateCal, Double temp, Double weight, String mens) {
        //Setting this dateCal to the parameter, otherwise it will be confuse with same name
        this.dateCal = dateCal;
        this.temp = temp;
        this.weight = weight;
        this.mens = mens;
    }

    //Getter method, to get and use the value after it being stored
    public Date getDateCal(){ return this.dateCal; }
    public Double getTemp(){ return this.temp; }
    public Double getWeight(){ return this.weight; }
    public String getMens(){ return this.mens; }
}