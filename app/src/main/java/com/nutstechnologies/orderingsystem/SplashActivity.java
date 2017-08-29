package com.nutstechnologies.orderingsystem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Context mContext = this;
        if (isOnline()) {
            boolean _valid = false;
            SharedPreferences pref = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            final DatabaseHelper db = new DatabaseHelper(mContext);
            ContentValues c_val = db.getData();
            String cs_u_name = c_val.getAsString("user_name");
            String cs_p_word = c_val.getAsString("p_word");
            String cs_db_name = c_val.getAsString("db_name");
            String cs_server_name = c_val.getAsString("server_name");
            if (TextUtils.isEmpty(cs_server_name)) {
                StaticClass.conSettings_Dialog(this, "splash_form");
            } else {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                ResultSet set = null;
                Log.i("Android", " MS SQL Connect Example.");
                try {
                    String driver = "net.sourceforge.jtds.jdbc.Driver";
                    Class.forName(driver).newInstance();
                    String connString = "jdbc:jtds:sqlserver://" + cs_server_name + "/" + cs_db_name + ";encrypt=false;user=" + cs_u_name + ";password=" + cs_p_word + ";instance=SQLEXPRESS;";
                    Connection conn = null;
                    conn = DriverManager.getConnection(connString);
                    Log.w("Connection", "open");
                    Statement stmt = conn.createStatement();
                    set = stmt.executeQuery("SP_Android_Select_Companyinfo");
                    _valid = true;
                } catch (Exception e) {
                    Toast.makeText(this, "Connection is invalid", Toast.LENGTH_LONG);
                    StaticClass.conSettings_Dialog(this, "splash_form");
                    Log.w("Error connection", "" + e.getMessage());
                }
            }

            if (_valid) {

                connectionString.con_u_name = cs_u_name;
                connectionString.con_p_word = cs_p_word;
                connectionString.con_db_name = cs_db_name;
                connectionString.con_server_name = cs_server_name;

                final SharedPreferences.Editor editor = pref.edit();
                String _id = pref.getString("trans_id", null);
                if (_id != null && _id != "") {
                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + UUID.fromString(_id) + "'");
                    editor.remove("trans_id"); // will delete key key_name4
                    editor.commit();
                    editor.clear();
                    editor.commit();
                }

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Intent newDocumentIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        newDocumentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        startActivity(newDocumentIntent);
                        finishAndRemoveTask();
                    }
                }, 3000);
            }
        } else {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connection Error");
            builder.setMessage("No network connection available. System will exit.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog1, int id) {
                    finish();
                    System.exit(0);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    /*
 * isOnline - Check if there is a NetworkConnection
 * @return boolean
 */
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
