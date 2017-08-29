package com.nutstechnologies.orderingsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.nutstechnologies.orderingsystem.MyOrderActivity.Qty;

public class PerCategoryActivity extends Fragment {
    View _view;
    public List<String> menu_item = new ArrayList<String>();
    public List<String> order_item = new ArrayList<String>();
    public List<String> picker_item = new ArrayList<String>();
    public List<String> item = new ArrayList<String>();
    String menu_tag;
    String order_tag;
    String picker_tag;
    public static Integer categoryID;
    public static String menuName;
    public static String btn_desc;
    public static String header_btn_desc;
    public static String From;
    String tag;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (StaticClass.TableName != null) {
            menuName = StaticClass.TableName + " > " + header_btn_desc + " > " + btn_desc;
        } else {
            menuName = header_btn_desc + " > " + btn_desc;
        }
        getActivity().setTitle(menuName);
        _view = view;
        ConnectToDatabase();
        setHasOptionsMenu(true);
        MainActivity.Prev_Module = "CategoryActivity";
        if (!StaticClass.return_data.contains("PerCategoryActivity")) {
            StaticClass.return_data.add("PerCategoryActivity");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem _menu = menu.findItem(R.id.nav_Tmenu);
        _menu.setIcon(R.drawable.x_menu);
        _menu.setVisible(false);
        MenuItem _returnTable = menu.findItem(R.id.nav_TOut);
        _returnTable.setIcon(R.drawable.exit);
        _returnTable.setVisible(false);
        MenuItem _billing = menu.findItem(R.id.nav_Tbilling);
        _billing.setVisible(false);
        MenuItem _refresh = menu.findItem(R.id.nav_Refresh);
        _refresh.setVisible(false);
        MenuItem _Send = menu.findItem(R.id.nav_Send);
        _Send.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_category, container, false);
    }

    UUID _uuid = UUID.randomUUID();

    public void ConnectToDatabase() {
        ResultSet set = null;
        try {
            menu_item = new ArrayList<String>();
            order_item = new ArrayList<String>();
//
            set = connectionString.ConnectionString("EXEC SP_Android_SelectItemByCategory '" + categoryID + "'");
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
            scroll.setPadding(50, 0, 0, 0);
            ViewGroup viewGroup = (ViewGroup) _view;
            viewGroup.addView(scroll);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            Log.w("Error connection", "" + e.getMessage());
        }
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static void setTransfer(final Context mContext, final LinearLayout insideLinear2, final TextView desc, final TextView desc2, final Float qty, final UUID SetItemID, final Button btn_menu, final String action) {
        LinearLayout l1_holder = new LinearLayout(mContext);
        l1_holder.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout l_qty = new LinearLayout(mContext);
        l_qty.setLayoutParams(new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT));
        l_qty.setOrientation(LinearLayout.VERTICAL);
        LinearLayout l_desc = new LinearLayout(mContext);
        l_desc.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        l_desc.setOrientation(LinearLayout.VERTICAL);

        String Category = (String) btn_menu.getText();
//        if (SetMenuCategory.containsKey(SetItemID)) {
            for (UUID k : SetMenuCategory.keySet()) {
                LinearLayout ll_object = SetMenuCategory.get(k);
                String tag = ll_object.getTag().toString();
                if (tag.equals(Category)) {
                    SetMenuItems.remove(k);
                    SetMenuCategory.remove(k);
                    desc.setPaintFlags(desc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    desc2.setPaintFlags(desc2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    ll_object.setVisibility(View.GONE);
                    break;
                }
            }

//        _SetMenuItemID.add();
        final TextView _Item_qty = new TextView(mContext);
        _Item_qty.setText(String.valueOf(qty));
        _Item_qty.setTextSize(16);
        if (!action.equals("put_one")) {
            _Item_qty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    insideLinear2.removeAllViews();
                    SetMenuItems.remove(SetItemID);
                    SetMenuCategory.remove(SetItemID);
                    btn_menu.performClick();
                }
            });
        }
        final TextView _Item_desc = new TextView(mContext);
        _Item_desc.setText(desc.getText().toString());
        _Item_desc.setTextSize(16);
        if (!action.equals("put_one")) {
            _Item_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    desc.setPaintFlags(desc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    desc2.setPaintFlags(desc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    insideLinear2.removeAllViews();
                    SetMenuItems.remove(SetItemID);
                    SetMenuCategory.remove(SetItemID);
                    btn_menu.performClick();
                }
            });
        }
        ((ViewGroup.MarginLayoutParams) l_qty.getLayoutParams()).leftMargin = 20;
        l_qty.addView(_Item_qty);
        l_desc.addView(_Item_desc);
        l1_holder.setTag(Category);
        l1_holder.addView(l_qty);
        l1_holder.addView(l_desc);

        if (action.equals("put") || action.equals("put_one")) {
            SetMenuItems.put(SetItemID, qty);
            SetMenuCategory.put(SetItemID, l1_holder);
        }
        insideLinear2.addView(l1_holder);
        if (!action.equals("put_one")) {
            btn_menu.performClick();
        }
    }

    public static Map<UUID, Float> SetMenuItems = new HashMap<UUID, Float>();
    public static Map<UUID, LinearLayout> SetMenuCategory = new HashMap<UUID, LinearLayout>();
    public static int SetMenuCategory_Size;
    public static int SetOne_Size;
    public static ArrayList<UUID> _SetMenuItemID = new ArrayList<UUID>();
    public Dialog DGenerate;
    public static UUID _SetMenuID;
    public UUID SetMenuHeaderID, SetMenuItemID, _ItemID;

    public void getData(ResultSet set, GridLayout gridLayout) {
        SetMenuCategory.clear();
        SetMenuItems.clear();
        SetMenuCategory_Size = 0;
        final DecimalFormat formatter = new DecimalFormat("#,###.00");
        //region Load Category With ID
        try {
            while (set.next()) {
                LinearLayout albumLayout = new LinearLayout(getContext());
                RelativeLayout albumDetailsLayout = new RelativeLayout(getContext());
                LinearLayout buttonLayout = new LinearLayout(getContext());
                final LinearLayout desclayout = new LinearLayout(getContext());
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

                RelativeLayout.LayoutParams rlName = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rlName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                final TextView description = new TextView((getContext()));
                description.setLayoutParams(rlName);
//                    description.setText(set.getString("ItemCode"));
                description.setText(set.getString("Description"));
                description.setTextSize(16);
                description.setPadding(0, 20, 0, 10);
                TextView price = new TextView((getContext()));
                TextView Oldprice = new TextView((getContext()));

                price.setLayoutParams(new LinearLayout.LayoutParams(250, 40));
                Boolean oldprice = false;

                if (set.getDouble("OldPrice") != set.getDouble("RegularPrice")) {
                    oldprice = true;
                    Oldprice.setText(set.getString("OldPrice"));
                    Oldprice.setPaintFlags(Oldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    Oldprice.setTextSize(16);
                }
                price.setText("Price: " + set.getString("RegularPrice"));
                price.setTextSize(16);
                final ImageButton btnImage = new ImageButton(getContext());
                final String TypeDescription = set.getString("TypeDescription");
                final UUID itemID = UUID.fromString(set.getString("ItemID"));
                final UUID HeaderID = UUID.fromString(set.getString("HEADERID"));
                final Double _Price = set.getDouble("RegularPrice");
                btnImage.setPadding(5, 5, 5, 5);
                btnImage.setTop(10);
                btnImage.setLeft(10);
                btnImage.setBackgroundColor(Color.WHITE);
                if (set.getString("ItemImage") != null) {
                    if (oldprice == true) {
                        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                                R.drawable.promo);
                        float ratio = Math.min(
                                (float) 120 / icon.getWidth(),
                                (float) 100 / icon.getHeight());
                        int width = Math.round((float) ratio * icon.getWidth());
                        int height = Math.round((float) ratio * icon.getHeight());
                        Bitmap newBitmap = Bitmap.createScaledBitmap(icon, width,
                                height, false);
                        Bitmap itemBitmap = StaticClass.bmp(set.getString("ItemImage"));
                        Bitmap bmOverlay = Bitmap.createBitmap(300, 350, itemBitmap.getConfig());
                        Canvas canvas = new Canvas(bmOverlay);
                        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(itemBitmap, 300, 350, true);
                        canvas.drawBitmap(scaledBitmap, 0, 0, null);
                        canvas.drawBitmap(newBitmap, 0, 0, null);
                        btnImage.setImageBitmap(bmOverlay);
                        btnImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    } else {
                        btnImage.setImageBitmap(StaticClass.bmp(set.getString("ItemImage")));
                        btnImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    }

                } else {
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
                //region BtnPicker OnEditorActionListener
                btn_picker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_MASK_ACTION) {
                            if (!TextUtils.isEmpty(btn_picker.getText())) {
                                Integer _qty = Integer.parseInt(String.valueOf(btn_picker.getText()));
                                if (!_qty.equals(0)) {
                                    btn_picker.setText(String.format("%.2f", Double.parseDouble(btn_picker.getText().toString())));
                                } else {
                                    StaticClass.showDialog(getContext(), "Add Order(s)", "Quantity cannot be equal to zero");
                                }
                            } else {
                                StaticClass.showDialog(getContext(), "Add Order(s)", "Quantity cannot be empty");
                            }
                        }
                        return false;
                    }
                });
                //endregion
                //region BtnPicker OnTouchListener
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
                        } catch (Exception e) {
                            Toast myToast;
                            myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
                            myToast.show();
                        }
                        return true;

                    }
                });
                //endregion

                TextView _qty = new TextView(getContext());
                _qty.setLayoutParams(new LinearLayout.LayoutParams(100, 75));
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
                if (StaticClass.TableName != null) {
                    buttonLayout.addView(_qty);
                    buttonLayout.addView(btn_picker);
                    buttonLayout.addView(btnOrder);
                }

                desclayout.addView(price);
                if (oldprice == true) {
                    desclayout.addView(Oldprice);
                }
                albumLayout.addView(albumDetailsLayout);
                albumLayout.addView(pictureHolder);
                albumLayout.addView(desclayout);
                albumLayout.addView(buttonLayout);
                gridLayout.addView(albumLayout);
                //region btn_picker OnOnClickListener
                //add onClickLister on btnImage
                btn_picker.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                    btn_picker.setInputType(InputType.TYPE_CLASS_NUMBER);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                            }
                        })
                );
                //endregion
                //region btnImage OnOnClickListener
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
                //endregion
                //region BtnOrder OnOnClickListener
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
                                        if (!TextUtils.isEmpty(btn_picker.getText())) {
                                            double _qty = Double.parseDouble(String.valueOf(btn_picker.getText()));
                                            if (_qty != 0) {
                                                // Use the Builder class for convenient dialog construction
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Add " + description.getText().toString());
                                                builder.setMessage("Are you sure you want to add " + value + " order(s) of " + description.getText().toString() + "?");
                                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        boolean isFlexible = SaveData.IsSetMenuFlexible(itemID);
                                                        if (TypeDescription.contains("Set Menu")) {
                                                            if (isFlexible) {
                                                                SetMenuCategory.clear();
                                                                SetMenuItems.clear();
                                                                SetMenuCategory_Size = 0;
                                                                SetOne_Size = 0;
                                                                //region Set Menu for flexible item
                                                                try {
                                                                    DGenerate = new Dialog(getContext());
                                                                    LinearLayout ll_main = new LinearLayout(getContext());
                                                                    ll_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                                                    ll_main.setOrientation(LinearLayout.VERTICAL);
                                                                    LinearLayout ll_header = new LinearLayout(getContext());
                                                                    ll_header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                                                                    ll_header.setOrientation(LinearLayout.HORIZONTAL);
                                                                    ll_header.setBackgroundColor(Color.parseColor("#34495e"));
                                                                    LinearLayout ll_detail = new LinearLayout(getContext());
                                                                    ll_detail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                                                    ll_detail.setOrientation(LinearLayout.HORIZONTAL);
                                                                    LinearLayout.LayoutParams ll_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                                                    ll_param.weight = 1;
                                                                    LinearLayout ll_from = new LinearLayout(getContext());
                                                                    ll_from.setOrientation(LinearLayout.VERTICAL);
                                                                    ll_from.setLayoutParams(ll_param);
                                                                    ll_from.setBackgroundColor(Color.WHITE);

                                                                    final LinearLayout ll_from_detail = new LinearLayout(getContext());
                                                                    ll_from_detail.setOrientation(LinearLayout.VERTICAL);
                                                                    ll_from_detail.setLayoutParams(ll_param);
                                                                    ll_from_detail.setBackgroundColor(Color.WHITE);

                                                                    TextView txt_table_from = new TextView(getContext());
                                                                    txt_table_from.setText("Item(s): ");
                                                                    txt_table_from.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60));
                                                                    txt_table_from.setGravity(Gravity.LEFT);
                                                                    txt_table_from.setTextSize(20);
                                                                    txt_table_from.setPadding(30, 0, 0, 0);
                                                                    txt_table_from.setTextColor(Color.parseColor("#34495e"));

                                                                    TextView txt_table_category = new TextView(getContext());
                                                                    txt_table_category.setText("Category: ");
                                                                    txt_table_category.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
                                                                    txt_table_category.setGravity(Gravity.LEFT);
                                                                    txt_table_category.setTextSize(20);
                                                                    txt_table_category.setPadding(30, 0, 0, 0);
                                                                    txt_table_category.setTextColor(Color.parseColor("#34495e"));

                                                                    final LinearLayout ll_to = new LinearLayout(getContext());
                                                                    ll_to.setOrientation(LinearLayout.VERTICAL);
                                                                    ll_to.setLayoutParams(ll_param);
                                                                    ll_to.setBackgroundColor(Color.parseColor("#F5F5F5"));
                                                                    TextView txt_table_to = new TextView(getContext());
                                                                    txt_table_to.setText("Selected Item(s)");
                                                                    txt_table_to.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60));
                                                                    txt_table_to.setGravity(Gravity.LEFT);
                                                                    txt_table_to.setTextSize(20);
                                                                    txt_table_to.setPadding(30, 0, 0, 0);
                                                                    txt_table_to.setTextColor(Color.parseColor("#34495e"));
                                                                    ll_to.addView(txt_table_to);

                                                                    GridLayout gridLayout = new GridLayout(getContext());
                                                                    gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
                                                                    gridLayout.setColumnCount(3);

                                                                    ResultSet set_one = connectionString.ConnectionString("EXEC SP_Android_GetCategory_FromSetMenuWithOneItem '" + itemID + "'");
                                                                    while (set_one.next()) {
                                                                        //region TODO: Btn menu Properties
                                                                        try {
                                                                            SetOne_Size++;
                                                                            final Button btn_menu = new Button(getContext());
                                                                            btn_menu.setLayoutParams(new LinearLayout.LayoutParams(185, 80));
                                                                            btn_menu.setTextSize(12);
                                                                            btn_menu.setText(set_one.getString("CategoryDescription"));
                                                                            btn_menu.setTransformationMethod(null);
                                                                            btn_menu.setTag(set_one.getString("CategoryID"));

                                                                            UUID cat_id = UUID.fromString((String) btn_menu.getTag());
                                                                            ResultSet SetMenuset = connectionString.ConnectionString("EXEC SP_Android_Select_SetMenubyCatID '" + itemID + "','" + cat_id + "'");
                                                                            while (SetMenuset.next()) {
                                                                                final LinearLayout insideLinear2 = new LinearLayout(getContext());
                                                                                insideLinear2.setOrientation(LinearLayout.VERTICAL);
                                                                                final TextView desc = new TextView(getContext());
                                                                                final TextView desc2 = new TextView(getContext());
                                                                                _SetMenuID = UUID.fromString(SetMenuset.getString("SetMenuID"));
                                                                                final UUID SetItemID = UUID.fromString(SetMenuset.getString("itemID")); //ID OF ITEM IN DETAIL
                                                                                final float RegularPrice = SetMenuset.getFloat("RegularPrice");
                                                                                final float Amount = SetMenuset.getFloat("Amount");
                                                                                final float qty = SetMenuset.getFloat("quantity");
                                                                                desc.setPadding(0, 6, 0, 0);
                                                                                desc.setText(SetMenuset.getString("Description"));
                                                                                desc.setTextSize(16);
                                                                                desc2.setPadding(0, 6, 0, 0);
                                                                                desc2.setText(String.valueOf(qty));
                                                                                desc2.setTextSize(16);
                                                                                ll_to.addView(insideLinear2);
                                                                                setTransfer(getContext(), insideLinear2, desc, desc2, qty, SetItemID, btn_menu, "put_one");
                                                                            }
                                                                        } catch (Exception e) {
                                                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                      //endregion

                                                                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_GetCategory_FromSetMenu '" + itemID + "'");
                                                                    while (set.next()) {
                                                                        //region TODO: Btn menu Properties
                                                                        SetMenuCategory_Size += 1;
                                                                        final Button btn_menu = new Button(getContext());
                                                                        btn_menu.setLayoutParams(new LinearLayout.LayoutParams(185, 80));
                                                                        btn_menu.setTextSize(12);
                                                                        btn_menu.setText(set.getString("CategoryDescription"));
                                                                        btn_menu.setTransformationMethod(null);
                                                                        btn_menu.setTag(set.getString("CategoryID"));
                                                                        gridLayout.addView(btn_menu);
                                                                        //endregion
                                                                        //region TODO: Menu Events
                                                                        btn_menu.setOnClickListener((new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                // TODO Auto-generated method stub
                                                                                try {
                                                                                    int ctr = 0;
                                                                                    UUID cat_id = UUID.fromString((String) btn_menu.getTag());
                                                                                    ll_from_detail.removeAllViews();
                                                                                    ResultSet SetMenuset = connectionString.ConnectionString("EXEC SP_Android_Select_SetMenubyCatID '" + itemID + "','" + cat_id + "'");
                                                                                    while (SetMenuset.next()) {
                                                                                        ctr++;
                                                                                        LinearLayout insideLinear1 = new LinearLayout(getContext());
                                                                                        insideLinear1.setOrientation(LinearLayout.VERTICAL);
                                                                                        LinearLayout l1_holder = new LinearLayout(getContext());
                                                                                        l1_holder.setOrientation(LinearLayout.HORIZONTAL);
                                                                                        LinearLayout l_qty = new LinearLayout(getContext());
                                                                                        l_qty.setLayoutParams(new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                                        l_qty.setOrientation(LinearLayout.VERTICAL);
                                                                                        LinearLayout l_desc = new LinearLayout(getContext());
                                                                                        l_desc.setLayoutParams(new LinearLayout.LayoutParams(390, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                                        l_desc.setOrientation(LinearLayout.VERTICAL);
                                                                                        LinearLayout l_select = new LinearLayout(getContext());
                                                                                        l_select.setLayoutParams(new LinearLayout.LayoutParams(90, 50));
                                                                                        l_select.setOrientation(LinearLayout.VERTICAL);
                                                                                        final LinearLayout insideLinear2 = new LinearLayout(getContext());
                                                                                        insideLinear2.setOrientation(LinearLayout.VERTICAL);
                                                                                        final TextView desc = new TextView(getContext());
                                                                                        final Button btn_select = new Button(getContext());
                                                                                        final TextView desc2 = new TextView(getContext());
                                                                                        _SetMenuID = UUID.fromString(SetMenuset.getString("SetMenuID"));
                                                                                        final UUID SetItemID = UUID.fromString(SetMenuset.getString("itemID")); //ID OF ITEM IN DETAIL
                                                                                        final float RegularPrice = SetMenuset.getFloat("RegularPrice");
                                                                                        final float Amount = SetMenuset.getFloat("Amount");
                                                                                        final float qty = SetMenuset.getFloat("quantity");
                                                                                        btn_select.setText("Add");
                                                                                        btn_select.setTextSize(12);
                                                                                        btn_select.setTransformationMethod(null);
                                                                                        desc.setPadding(0, 6, 0, 0);
                                                                                        desc.setText(SetMenuset.getString("Description"));
                                                                                        desc.setTextSize(16);
                                                                                        desc2.setPadding(0, 6, 0, 0);
                                                                                        desc2.setText(String.valueOf(qty));
                                                                                        desc2.setTextSize(16);
                                                                                        desc.setOnClickListener((new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                // TODO Auto-generated method stub
                                                                                                if ((desc.getPaintFlags() | ~Paint.STRIKE_THRU_TEXT_FLAG) == ~Paint.STRIKE_THRU_TEXT_FLAG) {
                                                                                                    setTransfer(getContext(), insideLinear2, desc, desc2, qty, SetItemID, btn_menu, "put");
                                                                                                }
                                                                                            }
                                                                                        }));

                                                                                        btn_select.setOnClickListener((new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                // TODO Auto-generated method stub
                                                                                                if ((desc.getPaintFlags() | ~Paint.STRIKE_THRU_TEXT_FLAG) == ~Paint.STRIKE_THRU_TEXT_FLAG) {
                                                                                                    setTransfer(getContext(), insideLinear2, desc, desc2, qty, SetItemID, btn_menu, "put");
                                                                                                }
                                                                                            }
                                                                                        }));

                                                                                        desc2.setOnClickListener((new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                // TODO Auto-generated method stub
                                                                                                if ((desc2.getPaintFlags() | ~Paint.STRIKE_THRU_TEXT_FLAG) == ~Paint.STRIKE_THRU_TEXT_FLAG) {
                                                                                                    setTransfer(getContext(), insideLinear2, desc, desc2, qty, SetItemID, btn_menu, "put");
                                                                                                }
                                                                                            }
                                                                                        }));
                                                                                        if (SetMenuItems.containsKey(SetItemID)) {
                                                                                            desc.setPaintFlags(desc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                                                            desc2.setPaintFlags(desc2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                                                        } else {
                                                                                            desc.setPaintFlags(desc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                                                                            desc2.setPaintFlags(desc2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                                                                        }
                                                                                        ((ViewGroup.MarginLayoutParams) l_qty.getLayoutParams()).leftMargin = 20;
                                                                                        l_qty.addView(desc2);
                                                                                        l_desc.addView(desc);
                                                                                        l_select.addView(btn_select);
                                                                                        l1_holder.addView(l_qty);
                                                                                        l1_holder.addView(l_desc);
                                                                                        l1_holder.addView(l_select);
                                                                                        insideLinear1.addView(l1_holder);
                                                                                        ll_from_detail.addView(insideLinear1);
                                                                                        ll_to.addView(insideLinear2);
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                                                }
                                                                            }
                                                                        }));
                                                                        //endregion
                                                                    }

                                                                    ll_from.addView(txt_table_category);
                                                                    ScrollView category_scroll = new ScrollView(getContext());
                                                                    category_scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 165));
                                                                    category_scroll.addView(gridLayout);
                                                                    ((ViewGroup.MarginLayoutParams) category_scroll.getLayoutParams()).rightMargin = 5;
                                                                    ((ViewGroup.MarginLayoutParams) category_scroll.getLayoutParams()).leftMargin = 15;
                                                                    ll_from.addView(category_scroll);
                                                                    ll_from.addView(txt_table_from);

                                                                    ScrollView from_scroll = new ScrollView(getContext());
                                                                    from_scroll.setLayoutParams(ll_param);
                                                                    from_scroll.addView(ll_from_detail);
                                                                    ll_from.addView(from_scroll);
                                                                    ll_detail.addView(ll_from);
                                                                    ll_detail.addView(ll_to);
                                                                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                    _view = inflater.inflate(R.layout.activity_blank, null);

                                                                    Button btnAll = new Button(getContext());
                                                                    Button btnFinish = new Button(getContext());
                                                                    btnAll.setText("All");
                                                                    btnFinish.setText("Finish");
//                                                                  final UUID finalTransID = TransID;
                                                                    btnFinish.setOnClickListener((new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            // TODO Auto-generated method stub
                                                                            //eto ang ginagawa ko

                                                                            UUID _newTransID = UUID.randomUUID();
                                                                            int i = 0;
                                                                            UUID sub_id = UUID.randomUUID();
                                                                            if (SetMenuItems.entrySet().size() > 0) {
                                                                                int tot_size = SetOne_Size + SetMenuCategory_Size;
                                                                                if (SetMenuItems.size() == tot_size) {
                                                                                    final ResultSet setx = connectionString.ConnectionString("EXEC SP_Android_SelectItembyMasterFileDTL_Header '" + _SetMenuID + "'");
                                                                                    try {
                                                                                        while (setx.next()) {
                                                                                            UUID ItemID = UUID.fromString(setx.getString("ItemID"));
                                                                                            Double Price = Double.parseDouble(setx.getString("RegularPrice"));
                                                                                            Double Amount = Double.parseDouble(btn_picker.getText().toString()) * Double.parseDouble(setx.getString("RegularPrice"));
                                                                                            int count = 1;
                                                                                            ResultSet count_set = connectionString.ConnectionString("EXEC SP_Android_Select_Max_Count_From_Detail '" + SaveData.Trans_HDRID + "'");
                                                                                            while (count_set.next()) {
                                                                                                count = count_set.getInt("CountPOS") + 1;
                                                                                                break;
                                                                                            }
                                                                                            SaveData.OrderItemSaveForSetMenu(SaveData.Trans_HDRID, ItemID, sub_id, Double.parseDouble(btn_picker.getText().toString()), Price, Amount, count, false);
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                                                    }

                                                                                    for (Map.Entry<UUID, Float> Item : SetMenuItems.entrySet()) {
                                                                                        _ItemID = Item.getKey();
                                                                                        Float Qty = Item.getValue();
                                                                                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_GetItem_ByItemID '" + _SetMenuID + "','" + _ItemID + "' ");
                                                                                        try {
                                                                                            while (set.next()) {
                                                                                                Double Price = set.getDouble("RegularPrice");
                                                                                                Double Amount = set.getDouble("Amount");
//                                                                                SaveData.OrderItemSaveFromSetMenu(SaveData.Trans_HDRID, _ItemID, Qty, Price, Amount, StaticClass.TableName);
                                                                                                int count = 1;
                                                                                                ResultSet count_set = connectionString.ConnectionString("EXEC SP_Android_Select_Max_Count_From_Detail '" + SaveData.Trans_HDRID + "'");
                                                                                                while (count_set.next()) {
                                                                                                    count = count_set.getInt("CountPOS") + 1;
                                                                                                    break;
                                                                                                }
                                                                                                double final_qty = Double.parseDouble(String.valueOf(Qty)) * Double.parseDouble(btn_picker.getText().toString());
                                                                                                double final_amt = final_qty * Price;
                                                                                                SaveData.OrderItemSaveForSetMenu(SaveData.Trans_HDRID, _ItemID, sub_id, final_qty, Price, final_amt, count, true);
                                                                                            }
                                                                                        } catch (Exception e) {
                                                                                            Log.v(e.getCause().toString(), e.getMessage());
                                                                                        }
                                                                                    }
                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                                    builder.setMessage(description.getText().toString() + " is successfully added.");
                                                                                    builder.setPositiveButton("OK", null);
                                                                                    AlertDialog dialog2 = builder.show();
                                                                                    dialog2.show();
                                                                                    DGenerate.hide();
                                                                                } else {
                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                                    builder.setTitle(description.getText().toString());
                                                                                    builder.setMessage("Selected menu is less than the sized of category selection.");
                                                                                    builder.setPositiveButton("OK", null);
                                                                                    AlertDialog dialog2 = builder.show();
                                                                                    dialog2.show();
                                                                                }
                                                                            } else {
                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                                builder.setMessage("Please Select Item(s) to be Transfer");
                                                                                builder.setPositiveButton("OK", null);
                                                                                AlertDialog dialog2 = builder.show();
                                                                                dialog2.show();
                                                                            }
//                dialog.hide();
                                                                        }
                                                                    }));
                                                                    btnFinish.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT));
//            ll_from.addView(btnAll);
                                                                    TextView txt_Title = new TextView(getContext());
                                                                    txt_Title.setText(description.getText());
                                                                    txt_Title.setLayoutParams(new LinearLayout.LayoutParams(940, LinearLayout.LayoutParams.MATCH_PARENT));
                                                                    txt_Title.setTextSize(40);
                                                                    txt_Title.setTextColor(Color.WHITE);
                                                                    ((ViewGroup.MarginLayoutParams) txt_Title.getLayoutParams()).leftMargin = 10;
                                                                    ((ViewGroup.MarginLayoutParams) txt_Title.getLayoutParams()).topMargin = 15;
                                                                    ll_header.addView(txt_Title);
                                                                    ll_header.addView(btnFinish);
                                                                    ll_main.addView(ll_header);
                                                                    ll_main.addView(ll_detail);
                                                                    ((ViewGroup.MarginLayoutParams) ll_main.getLayoutParams()).bottomMargin = 20;
                                                                    DGenerate.setContentView(_view);

                                                                    ViewGroup viewGroup = (ViewGroup) _view;
                                                                    viewGroup.addView(ll_main);
                                                                    _view = inflater.inflate(R.layout.activity_blank, viewGroup);
                                                                    final Dialog finalDialogOptions = DGenerate;
                                                                    ImageButton close = (ImageButton) DGenerate.findViewById(R.id.btnClose);
                                                                    close.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            finalDialogOptions.dismiss();
                                                                        }
                                                                    });
                                                                    DGenerate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                    DGenerate.getWindow()
                                                                            .setLayout((int) (getScreenWidth(getActivity()) * .9), ViewGroup.LayoutParams.MATCH_PARENT);
                                                                    DGenerate.show();
                                                                } catch (Exception e) {
                                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT);
                                                                }
                                                                DGenerate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                DGenerate.show();
                                                                //endregion
                                                            } else {
                                                                ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SetMenu_GetDetails '" + itemID + "'");
                                                                try {
                                                                    int count = 1;
                                                                    ResultSet count_set = connectionString.ConnectionString("EXEC SP_Android_Select_Max_Count_From_Detail '" + SaveData.Trans_HDRID + "'");
                                                                    while (count_set.next()) {
                                                                        count = count_set.getInt("CountPOS") + 1;
                                                                        break;
                                                                    }
                                                                    UUID SubsID = UUID.randomUUID();
                                                                    Double SetMenuQty = Double.parseDouble(btn_picker.getText().toString());
                                                                    Double _Amount = Double.parseDouble(btn_picker.getText().toString()) * _Price;
                                                                    SaveData.OrderItemSaveForSetMenu(SaveData.Trans_HDRID, itemID, SubsID, SetMenuQty, _Price, _Amount, count, false);
//                                                            SaveData.OrderItemSave(SaveData.Trans_HDRID, itemID,  Qty, _Price, _Amount, count, true);
                                                                    while (set.next()) {
//                                                              SaveData.OrderItemSaveForSetMenu(SaveData.Trans_HDRID, itemID, SubsID, Double.parseDouble(btn_picker.getText().toString()), Price, Amount, count, false);
                                                                        final UUID itemID = UUID.fromString(set.getString("itemID"));
                                                                        final Double Price = set.getDouble("RegularPrice");

                                                                        final Double qty = set.getDouble("quantity") * SetMenuQty;
                                                                        count = count + 1;
                                                                        final Double Amount = set.getDouble("Amount");

//                                                                SaveData.OrderItemSave(SaveData.Trans_HDRID, itemID,  Qty, Price, Amount, count, true);
                                                                        SaveData.OrderItemSaveForSetMenu(SaveData.Trans_HDRID, itemID, SubsID, qty, Price, Amount, count, true);
                                                                    }
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                    builder.setTitle("Ordered Successfully!");
                                                                    builder.setMessage(description.getText().toString() + " has been added to your order");
                                                                    builder.setPositiveButton("OK", null);
                                                                    AlertDialog dialog2 = builder.show();
                                                                    dialog2.show();
                                                                } catch (Exception e) {
                                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }

                                                        } else {
                                                            //region If menu is not set menu
                                                            Toast.makeText(getContext(), description.getText().toString() + " is successfully added.", Toast.LENGTH_LONG).show();
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
                                                                    SaveData.OrderItemSave(SaveData.Trans_HDRID, ItemID, x._quantity, Price, Amount, count, false);
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                            }
                                                            //endregion
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
                                            } else {
                                                StaticClass.showDialog(getContext(), "Add Order(s)", "Quantity cannot be equal to zero");
                                            }
                                        } else {
                                            StaticClass.showDialog(getContext(), "Add Order(s)", "Quantity cannot be empty");
                                        }
                                    }
                                } catch (Exception e) {
                                    myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                                    myToast.show();
                                }
                            }
                        })
                );
                //endregion
            }
        } catch (Exception e) {
        }
        //endregion
    }
}
