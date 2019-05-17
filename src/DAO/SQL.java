package DAO;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQL {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://aaoptbt13kn02k.casuinr99rxz.eu-west-3.rds.amazonaws.com:3306/ebdb";

    private static final String USER = "xiaoroubao1996";
    private static final String PASSWORD = "jdh19960114";


    public static Connection getSQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.toString());
        }
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        }catch(SQLException e){
            System.out.println(e.toString());
            return null;
        }
    }
}
