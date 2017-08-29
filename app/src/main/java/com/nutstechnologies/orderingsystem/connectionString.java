package com.nutstechnologies.orderingsystem;

import android.graphics.Color;
import android.os.StrictMode;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import net.sourceforge.jtds.jdbc.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by Adrian on 5/26/2017.
 */

public class connectionString {
   public static String con_u_name;
    public static String con_p_word;
    public static String con_db_name;
    public static String con_server_name;
    public static ResultSet ConnectionString(String command){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ResultSet set = null;
        Log.i("Android", " MS SQL Connect Example.");
        try {
            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://" + con_server_name + "/" + con_db_name + ";encrypt=false;user=" + con_u_name + ";password=" + con_p_word + ";instance=SQLEXPRESS;";
//            String connString = "jdbc:jtds:sqlserver://192.168.1.16:1433/dbRestaurant;encrypt=false;user=SA;password=CALIFORNIA@1153;instance=MSSQLSERVER2014;";
            Connection conn = null;
            conn = DriverManager.getConnection(connString);
            Log.w("Connection", "open");
            Statement stmt = conn.createStatement();
            set = stmt.executeQuery(command);

           return set;
        } catch (Exception e) {
            Log.w("Error connection", "" + e.getMessage());
        }
        return set;
    }


}
