package com.nutstechnologies.orderingsystem;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public List<Integer> _menuitem = new ArrayList<Integer>();
    int _id;
    String _menu_name;
    public static String Prev_Module;
    public static String Order_Prev;
    public Menu _Menu;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
//        handler = new Handler();
//        r = new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                startHandler();
//            }
//        };
//        startHandler();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DisplaySelectedScreen(R.id.nav_mainPage);
    }
//    Handler handler;
//    Runnable r;
//    @Override
//    public void onUserInteraction() {
//        // TODO Auto-generated method stub
//        super.onUserInteraction();
//        stopHandler();//stop first and then start
//        handler.postDelayed(r, 5000); //for 5 minutes
//        startHandler();
//    }
//    public void stopHandler() {
//        handler.removeCallbacks(r);
//    }
//    public void startHandler() {
//        if(StaticClass.Current_Fragment == "table summary") {
//            Fragment fragment = new TableActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.content_main, fragment);
//            ft.commit();
//
//        }
//
//    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
//            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(StaticClass.return_data.size() > 0) {
            Fragment fragment = null;
            String tag = null;
            for (int i = StaticClass.return_data.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    tag = "table summary";
                    if (SaveData.Trans_HDRID != null) {
                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + SaveData.Trans_HDRID + "'");
                    }
                    HideNavigation();
                    StaticClass.TableName = null;
                    StaticClass.Current_Fragment = "table summary";
                    fragment = new TableActivity();
                    Title = "Table Summary";
                    break;
                } else {
                    String value = StaticClass.return_data.get(i - 1).toString();
                    if (value.equals("MyOrderActivity")) {
                        tag = "My Order";
                        StaticClass.Current_Fragment = "My Order";
                        fragment = new MyOrderActivity();
                        Title = "My Order(s)";
                        StaticClass.return_data.remove(i);
                        break;
                    }
                    if (value.equals("PerCategoryActivity")) {
                        tag = "PerCategoryActivity";
                        StaticClass.Current_Fragment = "PerCategoryActivity";
                        fragment = new PerCategoryActivity();
                        StaticClass.return_data.remove(i);
                        break;
                    }
                    if (value.equals("MenuCategory")) {
                        tag = "Menu";
                        StaticClass.Current_Fragment = "Menu";
                        fragment = new MenuCategory();
                        Title = "Menu";
                        StaticClass.return_data.remove(i);
                        break;
                    }
                }
            }
            if (StaticClass.TableName != null) {
                Title = StaticClass.TableName + " > " + Title;
            }

            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, fragment, tag);
                ft.commit();
            }
            drawer.closeDrawer(GravityCompat.START);
        } else{
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        DisplaySelectedScreen(id);
        return super.onOptionsItemSelected(item);
    }
    public void HideNavigation() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_sign_out).setVisible(true);
        nav_Menu.findItem(R.id.nav_menu).setVisible(true);

        View hView =  navigationView.getHeaderView(0);
        ImageView company_image = (ImageView) hView.findViewById(R.id.nav_header_imageView);
        TextView company_name = (TextView) hView.findViewById(R.id.nav_header_Company);
        TextView company_email = (TextView) hView.findViewById(R.id.nav_header_Email);

        try {
            ResultSet set = connectionString.ConnectionString("SP_Android_Select_Companyinfo");
            while (set.next()) {
                company_name.setText(set.getString("CompanyName"));
                company_email.setText(set.getString("CompanyEmail"));
                if (set.getString("CompanyLogoAndroid") != null) {
                    company_image.setImageBitmap(StaticClass.bmp(set.getString("CompanyLogoAndroid")));
                }
                break;
            }
        } catch (Exception ex) {
            Log.v(ex.getCause().toString(), ex.getMessage());
        }
    }
    public Integer getMenuID() {
        return _id;
    }
    public String getMenuName() {
        return _menu_name;
    }
    public static String Title;
    private void DisplaySelectedScreen(int id){
        Fragment fragment = null;
        ResultSet set;
        String tag = null;
        boolean isVisible = false;
        if (_menuitem.contains(id)) {
            tag = "CategoryActivity";
            StaticClass.Current_Fragment = "CategoryActivity";
            fragment = new CategoryActivity();
            CategoryActivity.menuID = id;
            //Toast.makeText(getApplicationContext(), "The id of this menu is " + id, Toast.LENGTH_LONG).show();
        }
        switch(id) {
            case R.id.nav_mainPage:
                    tag = "table summary";
                    if (tag != StaticClass.Current_Fragment) {
                        set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + SaveData.Trans_HDRID + "'");
                        HideNavigation();
                        StaticClass.TableName = null;
                        StaticClass.Current_Fragment = "table summary";
                        fragment = new TableActivity();
                        Title = "Table Summary";
                    }
                break;
            case R.id.nav_menu:
                tag = "Menu";
                if (tag != StaticClass.Current_Fragment){
                    MenuCategory.From = "Home";
                    StaticClass.Current_Fragment = "Menu";
                    fragment = new MenuCategory();
                    Title = "Menu";
                }
                break;
            case R.id.nav_settings:
//                Prev_Module = "Login";
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.activity_settings);
                final RadioButton radioEnglish = (RadioButton) dialog.findViewById(R.id.radioEnglish);
                final RadioButton radioSimp = (RadioButton) dialog.findViewById(R.id.radioSimp);
                final RadioButton radioTrad = (RadioButton) dialog.findViewById(R.id.radioTrad);
                final Button btnSave = (Button) dialog.findViewById(R.id.BtnSave);
                btnSave.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String Language = null;
                                if(radioEnglish.isChecked() || radioSimp.isChecked() || radioTrad.isChecked()){
                                    if(radioEnglish.isChecked()){
                                        Language = "English";
                                    }
                                    if(radioSimp.isChecked()){
                                        Language = "Chinese Simplified";
                                    }
                                    if(radioTrad.isChecked()){
                                        Language = "Chinese Traditional";
                                    }
                                    SaveData.SetLanguage(Language);
                                }
                                else{
//                                    Toast T = Toast.makeText(mContext,  " Successfully Closed", Toast.LENGTH_LONG);
//                                    T.show();
                                }

                            }
                        })
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.nav_sign_out:
                Prev_Module = "Login";
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                fragment = new TableActivity();
                break;
            case R.id.nav_return:
                for (int i = StaticClass.return_data.size() - 1; i >= 0; i--){
                    if (i == 0){
                        break;
                    } else {
                        String value = StaticClass.return_data.get(i - 1).toString();
                        if(value.equals("MyOrderActivity")) {
                            tag = "My Order";
                            StaticClass.Current_Fragment = "My Order";
                            fragment = new MyOrderActivity();
                                Title = "My Order(s)";
                                StaticClass.return_data.remove(i);
                            break;
                        }
                        if(value.equals("PerCategoryActivity")) {
                            tag = "PerCategoryActivity";
                            StaticClass.Current_Fragment = "PerCategoryActivity";
                            fragment = new PerCategoryActivity();
                            StaticClass.return_data.remove(i);
                            break;
                        }
                        if(value.equals("MenuCategory")) {
                            tag = "Menu";
                            StaticClass.Current_Fragment = "Menu";
                            fragment = new MenuCategory();
                            Title = "Menu";
                            StaticClass.return_data.remove(i);
                            break;
                        }
                    }
                }
                break;
            case R.id.nav_Tmenu:
                tag = "Menu";
                if (tag != StaticClass.Current_Fragment){
                    MenuCategory.From = "Home";
                    Title = "Menu";
                    StaticClass.Current_Fragment = "Menu";
                    fragment = new MenuCategory();
                }
                break;
            case R.id.nav_TOut: {
                tag = "table summary";
                if (tag != StaticClass.Current_Fragment){
                    set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + SaveData.Trans_HDRID + "'");
                    HideNavigation();
                    StaticClass.TableName = null;
                    StaticClass.Current_Fragment = "table summary";
                    fragment = new TableActivity();
                    Title = "Table Summary";
                }
                break;
            }
            case R.id.nav_Refresh: {
                tag = "table summary";
                    HideNavigation();
                    StaticClass.TableName = null;
                    fragment = new TableActivity();
                    Title = "Table Summary";
                break;
            }
        }
        if(StaticClass.TableName != null){
            Title = StaticClass.TableName + " > " + Title;
        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment, tag);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        _menu_name = item.getTitle().toString();
        DisplaySelectedScreen(id);
        return true;
    }
}
