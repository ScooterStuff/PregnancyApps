package sample;

import java.sql.*;


public class DatabaseConnection {
    public Connection databaseLink;

//    public Connection getConnection() { //Use to connect to the database when called, only work if host locally
//        String databaseUser = "root";
//        String databasePassword = "skyhunter1921";
//        String url = "jdbc:mysql://localhost/demo_db";
    public Connection getConnection() { //Use to connect to the database when called, using hosting service to held database
        String databaseUser = "sql6481240";
        String databasePassword = "1bBqnEaEWv";
        String url = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6481240";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return databaseLink;
    }

    public static boolean CheckUsernameExists(String username) //Command to check if the username that is register already exist
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        boolean usernameExists = false;
        try
        {
            //loop through the database sort by username descending
            //individually check  with the newly created
            PreparedStatement st = connectDB.prepareStatement("select * from user_account order by username desc");
            ResultSet r1=st.executeQuery();
            String usernameCounter;
            if(r1.next())
            {
                usernameCounter =  r1.getString("username");
                if(usernameCounter.equalsIgnoreCase(username))
                {
                    System.out.println("It already exists"); // warning
                    usernameExists = true;
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("SQL Exception: "+ e);
        }
        return usernameExists;
    }
}
