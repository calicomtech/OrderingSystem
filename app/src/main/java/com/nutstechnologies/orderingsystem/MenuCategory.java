package com.nutstechnologies.orderingsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuCategory extends Fragment {
    public List<String> item = new ArrayList<String>();
    String tag;
    Integer menuID;
    String menuName;
    public static String From;
    ScrollView scroll;

    class  last_menu {
        int menu_id;
        String  menu_from;
    }

    static List<last_menu> prev_menu = new ArrayList<last_menu>();
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(MainActivity.Title);
//        DisplayMenu();
        MainActivity.Prev_Module = "MenuCategory";
        MainActivity.Order_Prev = "my_order";
        Display_ItemGroup(view);
        if (!StaticClass.return_data.contains("MenuCategory")) {
            StaticClass.return_data.add("MenuCategory");
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        menuID = activity.getMenuID();
        menuName = activity.getMenuName();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_cart, container, false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (StaticClass.TableName == null){
            MenuItem _return = menu.findItem(R.id.nav_return);
            _return.setVisible(false);
        }
        MenuItem _menu = menu.findItem(R.id.nav_Tmenu);
        _menu.setIcon(R.drawable.x_menu);
        _menu.setVisible(false);
        MenuItem _Table = menu.findItem(R.id.nav_TOut);
        _Table.setIcon(R.drawable.exit);
        _Table.setVisible(false);
        MenuItem _refresh = menu.findItem(R.id.nav_Refresh);
        _refresh.setVisible(false);
        MenuItem _billing = menu.findItem(R.id.nav_Tbilling);
        _billing.setVisible(false);
        MenuItem _Send = menu.findItem(R.id.nav_Send);
        _Send.setVisible(false);
    }

    public void Display_ItemGroup(View view) {
        final LinearLayout ll_main = new LinearLayout(getContext());
        ll_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll_main.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ll_holder = new LinearLayout(getContext());
        ll_holder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ll_holder.setOrientation(LinearLayout.HORIZONTAL);

        final LinearLayout ll_detail = new LinearLayout(getContext());
        ll_detail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ll_detail.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams ll_param = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);

        //region TODO: Load last category when user leaves this form
        if (prev_menu.size() > 0){
            for (int i =0; i < prev_menu.size(); i++){
                last_menu x = prev_menu.get(i);
                if (x.menu_from == "category"){
                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectCategory_ByGroup '" + x.menu_id + "'");
                    load_DataByItemGroup(set, ll_detail);
                } else if (x.menu_from == "promo"){
                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectAllPromo 'HEADER' ");
                    load_DataByItemGroup(set, ll_detail);
                } else if (x.menu_from == "chef"){
                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_ChefRec 'ALL', NULL ");
                    load_DataByItemGroup(set, ll_detail);
                }

            }
        }//endregion

        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_GroupMenu");
        try {
            while (set.next()) {
                //region TODO: Btn menu Properties
                final Button btn_menu = new Button(getContext());
                btn_menu.setBackgroundColor(Color.WHITE);
                btn_menu.setTextColor(Color.GRAY);
                btn_menu.setLayoutParams(ll_param);
                btn_menu.setTextSize(18);
                btn_menu.setTransformationMethod(null);
                btn_menu.setText(set.getString("GroupDescription"));
                btn_menu.setTag(set.getString("GroupCount"));

                ((ViewGroup.MarginLayoutParams) btn_menu.getLayoutParams()).rightMargin = 10;
                ((ViewGroup.MarginLayoutParams) btn_menu.getLayoutParams()).topMargin = 7;
                ((ViewGroup.MarginLayoutParams) btn_menu.getLayoutParams()).bottomMargin = 7;
                ll_holder.addView(btn_menu);
                //endregion
                //region TODO: Menu Events
                btn_menu.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        try {
                            PerCategoryActivity.header_btn_desc = btn_menu.getText().toString();
                            final String voidTag = (String) btn_menu.getTag();
                            prev_menu = new ArrayList<last_menu>();
                            last_menu x = new last_menu();
                            x.menu_from = "category";
                            x.menu_id = Integer.parseInt(voidTag);
                            prev_menu.add(x);
                            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectCategory_ByGroup '" + voidTag + "'");
                            load_DataByItemGroup(set, ll_detail);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }));
                //endregion
            }
            set.close();

            //region TODO: Btn Promo Properties
            final Button btn_promo = new Button(getContext());
            btn_promo.setBackgroundColor(Color.WHITE);
            btn_promo.setTextColor(Color.GRAY);
            btn_promo.setLayoutParams(ll_param);
            btn_promo.setTextSize(18);
            btn_promo.setTransformationMethod(null);
            btn_promo.setText("Promo");
            btn_promo.setTag("Promo");

            ((ViewGroup.MarginLayoutParams) btn_promo.getLayoutParams()).rightMargin = 10;
            ((ViewGroup.MarginLayoutParams) btn_promo.getLayoutParams()).topMargin = 7;
            ((ViewGroup.MarginLayoutParams) btn_promo.getLayoutParams()).bottomMargin = 7;
            //endregion
            //region TODO: Promo Events
            btn_promo.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        PerCategoryActivity.header_btn_desc = btn_promo.getText().toString();
                        prev_menu = new ArrayList<last_menu>();
                        last_menu x = new last_menu();
                        x.menu_from = "promo";
                        x.menu_id = 0;
                        prev_menu.add(x);
                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectAllPromo 'HEADER' ");
                        load_DataByItemGroup(set, ll_detail);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }));
            //endregion
            //region TODO: Btn Chef Properties
            final Button btn_chef = new Button(getContext());
            btn_chef.setBackgroundColor(Color.WHITE);
            btn_chef.setTextColor(Color.GRAY);
            btn_chef.setLayoutParams(ll_param);
            btn_chef.setTransformationMethod(null);
            btn_chef.setTextSize(18);
            btn_chef.setText("Chef Reco.");
            btn_promo.setTag("Chef");

            ((ViewGroup.MarginLayoutParams) btn_chef.getLayoutParams()).rightMargin = 10;
            ((ViewGroup.MarginLayoutParams) btn_chef.getLayoutParams()).topMargin = 7;
            ((ViewGroup.MarginLayoutParams) btn_chef.getLayoutParams()).bottomMargin = 7;
            //endregion
            //region TODO: Chef Events
            btn_chef.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        prev_menu = new ArrayList<last_menu>();
                        last_menu x = new last_menu();
                        x.menu_from = "chef";
                        x.menu_id = 0;
                        prev_menu.add(x);
                        PerCategoryActivity.header_btn_desc = btn_chef.getText().toString();
                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_ChefRec 'ALL', NULL ");
                        load_DataByItemGroup(set, ll_detail);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }));
            //endregion

            ((ViewGroup.MarginLayoutParams) btn_chef.getLayoutParams()).rightMargin = 10;
            ((ViewGroup.MarginLayoutParams) btn_chef.getLayoutParams()).topMargin = 7;
            ((ViewGroup.MarginLayoutParams) btn_chef.getLayoutParams()).bottomMargin = 7;
            //endregion
            HorizontalScrollView scroll = new HorizontalScrollView(getContext());
            scroll.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ((ViewGroup.MarginLayoutParams) ll_holder.getLayoutParams()).bottomMargin = 10;
            ll_holder.addView(btn_promo);
            ll_holder.addView(btn_chef);
            scroll.addView(ll_holder);
            ll_main.addView(scroll);
            ll_main.addView(ll_detail);
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.addView(ll_main);
        }catch (Exception ex){
            Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            Log.w("Error connection", "" + ex.getMessage());
        }
    }
    void load_DataByItemGroup(ResultSet set, LinearLayout ll_detail){
        try {

            GridLayout gridLayout = new GridLayout(getContext());
            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            gridLayout.setColumnCount(6);

            LinearLayout.LayoutParams ll_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            while (set.next()) {
                LinearLayout ll_detail_holder = new LinearLayout(getContext());
                ll_detail_holder.setLayoutParams(new LinearLayout.LayoutParams(200, 150));
                ll_detail_holder.setOrientation(LinearLayout.VERTICAL);

                //region TODO: Btn menu Properties
                final Button btn_menu = new Button(getContext());
                btn_menu.setBackgroundColor(Color.WHITE);
                btn_menu.setTextColor(Color.GRAY);
                btn_menu.setLayoutParams(ll_param);
                btn_menu.setTransformationMethod(null);
                btn_menu.setText(set.getString("desc"));
                btn_menu.setTag(set.getString("id"));

                ((ViewGroup.MarginLayoutParams) btn_menu.getLayoutParams()).rightMargin = 10;
                ((ViewGroup.MarginLayoutParams) btn_menu.getLayoutParams()).topMargin = 7;
                ((ViewGroup.MarginLayoutParams) btn_menu.getLayoutParams()).bottomMargin = 7;

                ll_detail_holder.addView(btn_menu);
                gridLayout.addView(ll_detail_holder);
                //endregion
                //region TODO: Menu Events
                btn_menu.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        try {
                            Fragment fragment = new PerCategoryActivity();
                            PerCategoryActivity.From = "Menu";
                            PerCategoryActivity.btn_desc = btn_menu.getText().toString();
                            PerCategoryActivity.categoryID = (Integer.parseInt((String)btn_menu.getTag()));
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_main, fragment);
                            ft.commit();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }));
                //endregion
            }
            set.close();
            ScrollView scroll = new ScrollView(getContext());
            scroll.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            scroll.addView(gridLayout);
            ll_detail.removeAllViews();
            ll_detail.addView(scroll);
        }catch (Exception ex){
        Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
        Log.w("Error connection", "" + ex.getMessage());
    }
    }
    UUID _uuid = UUID.randomUUID();
    public void DisplayMenu(View view) {
        ResultSet set = null;
        try {
            item = new ArrayList<String>();
            if(From.contains("Home")){
                 set = connectionString.ConnectionString("EXEC SP_Android_SelectCategory 'ALL', NULL");
            }
            else{
                 set = connectionString.ConnectionString("EXEC SP_Android_SelectCategory 'ALL', NULL");
            }

            //Print the data to the console
            GridLayout gridLayout = new GridLayout(getContext());
            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);

            gridLayout.setColumnCount(3);
            getData(set, gridLayout);
            set.close();
            scroll = new ScrollView(getContext());
            scroll.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            scroll.addView(gridLayout);
            scroll.setPadding(50,0,0,0);
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.addView(scroll);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            Log.w("Error connection", "" + e.getMessage());
        }
    }
    public void getData(ResultSet set, GridLayout gridLayout){
            try {
                while (set.next()) {
                    LinearLayout albumLayout = new LinearLayout(getContext());
                    RelativeLayout albumDetailsLayout = new RelativeLayout(getContext());
                    LinearLayout pictureHolder = new LinearLayout(getContext());
                    LinearLayout pictureRowHolder = new LinearLayout(getContext());
                    albumLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pictureRowHolder.setOrientation(LinearLayout.HORIZONTAL);
                    pictureRowHolder.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pictureHolder.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pictureHolder.setOrientation(LinearLayout.VERTICAL);
                    albumLayout.setOrientation(LinearLayout.VERTICAL);
                    albumDetailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    final TextView description = new TextView((getContext()));
                    RelativeLayout.LayoutParams rlName = new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    rlName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    description.setLayoutParams(rlName);
                    description.setText(set.getString("CategoryDescription"));
                    description.setTextSize(15);
                    description.setPadding(0, 5, 0, 5);
                    final ImageButton btnImage = new ImageButton(getContext());
                    btnImage.setPadding(5, 5, 5, 5);
                    btnImage.setTop(10);
                    btnImage.setLeft(10);
                    btnImage.setBackgroundColor(Color.WHITE);
                    if (set.getString("ItemImage") != null) {
                        btnImage.setImageBitmap(StaticClass.bmp(set.getString("ItemImage")));
                        btnImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    else {
                        btnImage.setImageResource(R.drawable.no_image);
                        btnImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    LinearLayout.LayoutParams img_param = new LinearLayout.LayoutParams(350,300);
                    btnImage.setLayoutParams(img_param);
                    btnImage.setTag("btn_menu" + set.getString("CategoryCount"));
                    ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).bottomMargin = 30;
                    ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).rightMargin = 30;
                    pictureRowHolder.addView(btnImage);
                    pictureHolder.addView(pictureRowHolder);
                    albumDetailsLayout.addView(description);
                    albumLayout.addView(albumDetailsLayout);
                    albumLayout.addView(pictureHolder);
                    gridLayout.addView(albumLayout);
                    tag = "btn_menu" + set.getString("CategoryCount");
                    item.add(tag);
                    btnImage.setOnClickListener((new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Toast myToast = null;
                                    try {
                                        if (item.contains(btnImage.getTag())){
                                            String text = (String)btnImage.getTag();
                                            String id = text.replace("btn_menu", "");
                                            try {
                                                Fragment fragment = new CategoryActivity();
                                                CategoryActivity.From = "Menu";
                                                if(StaticClass.TableName != null){
                                                    CategoryActivity.menuName = StaticClass.TableName + " > Menu > " + (String) description.getText();
                                                }
                                                else{
                                                    CategoryActivity.menuName = " Menu > " + (String) description.getText();
                                                }

                                                CategoryActivity.menuID = (Integer.parseInt(id));
                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                ft.replace(R.id.content_main, fragment);
                                                ft.commit();
                                                DrawerLayout drawer = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
                                                drawer.closeDrawer(GravityCompat.START);
                                            }catch (Exception e){
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            })
                    );
                }
            } catch (Exception e) {
            }
    }
}
