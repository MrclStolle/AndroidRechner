package de.stolle.myapps.ui;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class OracleDBConnection {
    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@192.168.0.91:1521:XE";
    private static final String DEFAULT_USERNAME = "TESTSPACE";
    private static final String DEFAULT_PASSWORD = "Zreeder90";

    private static Connection connection;
    static ArrayList<String[]> result = new ArrayList<>();

    public static ArrayList<String[]> OracleQuery(String command){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        try {
            connection = createConnection();

            Statement statement = connection.createStatement();
            //StringBuffer stringBuffer = new StringBuffer();
            ResultSet resultSet = statement.executeQuery(command);
            int spalten = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
            String[] str = new  String[spalten];
            for(int k = 1;k<=spalten;k++){
                str[k-1]=resultSet.getString(k);
            }
            result.add(str);
                //stringBuffer.append(resultSet.getString(1) + "\n");
            }
            connection.close();
            //return stringBuffer.toString();
            return  result;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }

    private static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    private static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(DEFAULT_DRIVER, DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}

