package com.nutstechnologies.orderingsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class CategoryActivity extends Fragment {
    View _view;
    public List<String> menu_item = new ArrayList<String>();
    public List<String> order_item = new ArrayList<String>();
    public List<String> picker_item = new ArrayList<String>();
    public List<String> item = new ArrayList<String>();
    String menu_tag;
    String order_tag;
    String picker_tag;
    public static Integer menuID;
    public static String menuName;
    public static String From;
    String tag;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(menuName);
        _view = view;
        ConnectToDatabase();
        setHasOptionsMenu(true);
        MainActivity.Prev_Module = "CategoryActivity";
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem _menu = menu.findItem(R.id.nav_Tmenu);
        _menu.setIcon(R.drawable.x_menu);
        _menu.setVisible(true);
        MenuItem _Table = menu.findItem(R.id.nav_TOut);
        _Table.setIcon(R.drawable.exit);
        _Table.setVisible(StaticClass.TableName != null ? true : false);
        MenuItem _billing = menu.findItem(R.id.nav_Tbilling);
        _billing.setVisible(false);
        MenuItem _Send = menu.findItem(R.id.nav_Send);
        _Send.setVisible(false);
    }
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
//        if(From != "Menu"){
//            menuID = activity.getMenuID();
//        }
//        menuName =activity.getMenuName();
        return inflater.inflate(R.layout.activity_category, container, false);
    }
    UUID _uuid = UUID.randomUUID();
    public void ConnectToDatabase() {
        ResultSet set = null;
        try {
            menu_item = new ArrayList<String>();
            order_item = new ArrayList<String>();
//
                set = connectionString.ConnectionString("EXEC SP_Android_SelectItemByCategory '" + menuID + "'");
//
            GridLayout gridLayout = new GridLayout(getContext());
            GridLayout.LayoutParams _param = new GridLayout.LayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            gridLayout.setColumnCount(3);
            gridLayout.setLayoutParams(_param);
            getData(set, gridLayout);
            set.close();
            ScrollView scroll = new ScrollView(getContext());
            scroll.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            scroll.addView(gridLayout);
            scroll.setPadding(50,0,0,0);
            ViewGroup viewGroup = (ViewGroup) _view;
            viewGroup.addView(scroll);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            Log.w("Error connection", "" + e.getMessage());
        }
    }
    public void getData(ResultSet set, GridLayout gridLayout){
        final DecimalFormat formatter = new DecimalFormat("#,###.00");
        if(From.contains("Home")){
            //region Load All CategoryData
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
                    TextView description = new TextView((getContext()));
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

                }
            } catch (Exception e) {
            }
            //endregion
        }
        else{
            //region Load Category With ID
            try {
                while (set.next()) {
                    LinearLayout albumLayout = new LinearLayout(getContext());
                    RelativeLayout albumDetailsLayout = new RelativeLayout(getContext());
                    LinearLayout buttonLayout = new LinearLayout(getContext());
                    LinearLayout desclayout = new LinearLayout(getContext());
                    LinearLayout pictureHolder = new LinearLayout(getContext());
                    LinearLayout pictureRowHolder = new LinearLayout(getContext());
                    albumLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    pictureRowHolder.setOrientation(LinearLayout.HORIZONTAL);
                    pictureRowHolder.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pictureHolder.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pictureHolder.setOrientation(LinearLayout.VERTICAL);
                    albumLayout.setOrientation(LinearLayout.VERTICAL);
                    albumDetailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                    desclayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    desclayout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView description = new TextView((getContext()));
                    RelativeLayout.LayoutParams rlName = new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    rlName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    description. setLayoutParams(rlName);
                    description.setText(set.getString("ItemCode"));
                    description.setTextSize(19);
                    description.setPadding(0, 20, 0, 10);
                    TextView price = new TextView((getContext()));
                    price.setLayoutParams(new LinearLayout.LayoutParams(170, 40));
                    price.setText( "Price: " + set.getString("RegularPrice"));
                    price.setTextSize(16);
//                    TextView order_size = new TextView((getContext()));
//                    order_size.setLayoutParams(new LinearLayout.LayoutParams(70, 40));
//                    order_size.setText( "Size:");
//                    order_size.setTextSize(16);
//                    order_size.setGravity(Gravity.RIGHT);
//
//                    Spinner select_size = new Spinner((getContext()));
//                    select_size.setLayoutParams(new LinearLayout.LayoutParams(115, 40));
//                    select_size.setGravity(Gravity.LEFT);
//                    // Initializing an ArrayAdapter
//                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
//                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    select_size.setAdapter(spinnerAdapter);
//                    spinnerAdapter.add("Regular");
//                    spinnerAdapter.add("Large");
//                    spinnerAdapter.notifyDataSetChanged();

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
                    LinearLayout.LayoutParams img_param = new LinearLayout.LayoutParams(350, 300);
                    btnImage.setLayoutParams(img_param);
                    menu_tag = "btn_menu" + set.getString("itemCount");
                    btnImage.setTag(menu_tag);
                    ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).rightMargin = 30;
                    RelativeLayout.LayoutParams _rl = new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    _rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    final Button btnOrder = new Button(getContext());
                    btnOrder.setPadding(5, 5, 5, 5);
                    btnOrder.setTop(10);
                    btnOrder.setLeft(10);
                    btnOrder.setText("Add");
                    order_tag = "btn_order" + set.getString("itemCount");
                    btnOrder.setTag(order_tag);
                    btnOrder.setLayoutParams(new LinearLayout.LayoutParams(100, 75));
                    ((ViewGroup.MarginLayoutParams) btnOrder.getLayoutParams()).leftMargin = 10;

                    final EditText btn_picker = new EditText(getContext());
                    btn_picker.setTextSize(18);
                    btn_picker.setText("1.00");
                    btn_picker.setGravity(Gravity.RIGHT);
                    picker_tag = "btn_picker" + set.getString("itemCount");
                    btn_picker.setTag(picker_tag);
                    btn_picker.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
                    ((ViewGroup.MarginLayoutParams) btn_picker.getLayoutParams()).leftMargin = 12;
                    btn_picker.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    btn_picker.setKeyListener(DigitsKeyListener.getInstance(true, true));
                    btn_picker.setFilters(new InputFilter[]{StaticClass.filter});
                    //region TODO: Events and Validations for btn_picker
                    btn_picker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_MASK_ACTION) {
                                btn_picker.setText(String.format("%.2f", Double.parseDouble(btn_picker.getText().toString())));
                            }
                            return false;
                        }
                    });
                    btn_picker.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            try {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        btn_picker.setFocusable(true);
                                        btn_picker.requestFocus();
                                        btn_picker.setSelection(0, btn_picker.getText().length());
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        v.performClick();
                                        break;
                                    default:
                                        break;
                                }
                            }catch (Exception e){
                                Toast myToast;
                                myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
                                myToast.show();
                            }
                            return true;

                        }
                    });


                    TextView _qty = new TextView(getContext());
                    _qty.setLayoutParams(new LinearLayout.LayoutParams(100,75));
                    _qty.setText("Quantity:");
                    _qty.setGravity(Gravity.RIGHT);
                    _qty.setTextSize(19);
                    ((ViewGroup.MarginLayoutParams) _qty.getLayoutParams()).leftMargin = 30;
                    ((ViewGroup.MarginLayoutParams) buttonLayout.getLayoutParams()).bottomMargin = 30;
                    menu_item.add(menu_tag);
                    order_item.add(order_tag);
                    picker_item.add(picker_tag);
                    pictureRowHolder.addView(btnImage);
                    pictureHolder.addView(pictureRowHolder);
                    albumDetailsLayout.addView(description);
                    if(StaticClass.TableName != null){
                        buttonLayout.addView(_qty);
                        buttonLayout.addView(btn_picker);
                        buttonLayout.addView(btnOrder);
                    }

                    desclayout.addView(price);
                    albumLayout.addView(albumDetailsLayout);
                    albumLayout.addView(pictureHolder);
                    albumLayout.addView(desclayout);
                    albumLayout.addView(buttonLayout);
                    gridLayout.addView(albumLayout);
                    //add onClickLister on btnImage
                    btn_picker.setOnClickListener((new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    btn_picker.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
                                }
                            })
                    );
                    btnImage.setOnClickListener((new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Toast myToast = null;
                                    try {
                                        if (menu_item.contains(btnImage.getTag())) {
                                            String text = (String) btnImage.getTag();
                                            String split = text.replace("btn_menu", "");
                                            StaticClass.showDialog(_view, Integer.parseInt(split));
                                        }
                                    } catch (Exception e) {
                                        myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                                        myToast.show();
                                    }
                                }
                            })
                    );
                    //add onClickLister on btnOrder
                    btnOrder.setOnClickListener((new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Toast myToast = null;
                                    try {
                                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                        if (order_item.contains(btnOrder.getTag())) {

                                            String text = (String) btnOrder.getTag();
                                            String split = text.replace("btn_order", "");
                                            String picker = "btn_picker" + split;
                                            String value = null;
                                            if (picker_item.contains(picker)) {
                                                value = btn_picker.getText().toString();
                                            }
                                            // Use the Builder class for convenient dialog construction
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Add Order");
                                            builder.setMessage("Are you sure you want to add "+ value + " order(s) of this menu?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // You don't have to do anything here if you just want it dismissed when clicked
                                                    Toast.makeText(getContext(), "Order is successfully added.", Toast.LENGTH_LONG).show();
                                                    OrderList x = new OrderList();
                                                    String text = (String) btnOrder.getTag();
                                                    String split = text.replace("btn_order", "");
                                                    String picker = "btn_picker" + split;
                                                    String value = null;
                                                    if (picker_item.contains(picker)) {
                                                        value = btn_picker.getText().toString();
                                                    }
                                                    x._menuID = Integer.parseInt(split);
                                                    x._quantity = Double.parseDouble(value);

                                                    final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + x._menuID + "'");
                                                    try {
                                                        while (set.next()) {
                                                            UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                                            Double Price = Double.parseDouble(set.getString("RegularPrice"));
                                                            Double Amount = x._quantity * Double.parseDouble(set.getString("RegularPrice"));
                                                            int count = 1;
                                                            ResultSet count_set = connectionString.ConnectionString("EXEC SP_Android_Select_Max_Count_From_Detail '" + SaveData.Trans_HDRID + "'");
                                                            while (count_set.next()) {
                                                                count = count_set.getInt("CountPOS") + 1;
                                                                break;
                                                            }
                                                            SaveData.OrderItemSave(SaveData.Trans_HDRID, ItemID, x._quantity, Price, Amount, count, true);
                                                        }
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // You don't have to do anything here if you just want it dismissed when clicked
                                                    Toast.makeText(getContext(), "Order is cancelled", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            builder.show();
                                        }
                                    } catch (Exception e) {
                                        myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                                        myToast.show();
                                    }
                                }
                            })
                    );
                }
            } catch (Exception e) {
            }
            //endregion
        }


    }
}
