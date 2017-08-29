package com.nutstechnologies.orderingsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nutstechnologies.orderingsystem.TableActivity.close;

public class MyOrderActivity extends Fragment {
    public static List<CheckBox> all_checkbox = new ArrayList<CheckBox>();
    public static List<CheckBox> all_to_checkbox = new ArrayList<CheckBox>();
    public static List<UUID> Detail_ID = new ArrayList<UUID>();
    public static String From;
    public static CheckBox chk;
    public static Boolean Single;
    public static Double Qty;
    public static UUID ModifID;
    public static UUID RowID;
    public static UUID SUBID;
    public static String Order;
    public static List<String> _ListModif = new ArrayList<String>();
    static double net_amt = 0;
    public Dialog DOptions;
    public UUID ReasonID;
    _totalData x_data;
    List<_totalData> _totals = new ArrayList<_totalData>();
    List<Boolean> all_check_state = new ArrayList<Boolean>();
    List<Boolean> all_to_check_state = new ArrayList<Boolean>();
    List<LinearLayout> llQty_Holder = new ArrayList<LinearLayout>();
    SharedPreferences sharedpreferences;

    public static void SendBilling(Context mContext) {
        if (StaticClass.CodeStatus == true) {
            ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_Update_TransHeader_ByID '" + SaveData.Trans_HDRID + "', '" + net_amt + "'");
            Toast.makeText(mContext, "Billing request successfully sent to Cashier.", Toast.LENGTH_LONG).show();
            StaticClass.CodeStatus = false;
        }
    }

    public static void SendOrder(Context mContext){
        if(StaticClass.CodeStatus == true)
        {
//            generateNoteOnSD(mContext, "Order.txt");
            SaveData.Send_Item(SaveData.Trans_HDRID);
            Fragment fragment = new MyOrderActivity();
            int ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }
    }

    public static void SetClickable(Dialog dialog) {
        if (StaticClass.CodeStatus == true) {
            if(Single == true){
                dialog.hide();
                chk.setChecked(chk.isChecked() == true ? false : true);
            }
            else{
                if (chk.isChecked() == false){
                    for (int i = 0; i < all_checkbox.size(); i++) {
                        all_checkbox.get(i).setChecked(true);
                    }
                } else{
                    for (int i = 0; i < all_checkbox.size(); i++) {
                        all_checkbox.get(i).setChecked(false);
                    }
                }
            }
        }
    }

    public static void TakeOut_SetClickable(Dialog dialog) {
        if (StaticClass.CodeStatus == true) {
            if(Single == true){
                dialog.hide();
                chk.setChecked(chk.isChecked() == true ? false : true);
            }
            else{
                if (chk.isChecked() == false){
                    for (int i = 0; i < all_checkbox.size(); i++) {
                        all_to_checkbox.get(i).setChecked(true);
                    }
                } else{
                    for (int i = 0; i < all_checkbox.size(); i++) {
                        all_to_checkbox.get(i).setChecked(false);
                    }
                }
            }
        }
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Order(s)");
        MainActivity.Prev_Module = "my_orders";
        MainActivity.Order_Prev = "MenuCategory";
        GetTable();
        Generate_Layout(view);
        if (!StaticClass.return_data.contains("MyOrderActivity")) {
            StaticClass.return_data.add("MyOrderActivity");
        }
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("trans_id", SaveData.Trans_HDRID.toString());  // Saving string
        MenuCategory.prev_menu.clear();
        // Save the changes in SharedPreferences
        editor.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)context;
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_menu).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_mainPage).setVisible(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_cart, container, false);
    }

    int sent_count(){
        int x = 0;
        ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_Count_NotSent '" + SaveData.Trans_HDRID + "'");
        try {
            while (set_x.next()) {
                x = set_x.getInt("count_trans");
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    int billing_count(){
        int x = 0;
        ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_Count_Billing '" + SaveData.Trans_HDRID + "'");
        try {
            while (set_x.next()) {
                x = set_x.getInt("count_trans");
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    int count_Detail(){
        int x = 0;
        ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_Count_DetailItem '" + SaveData.Trans_HDRID + "'");
        try {
            while (set_x.next()) {
                x = set_x.getInt("count_trans");
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem _menu = menu.findItem(R.id.nav_Tmenu);
        _menu.setIcon(R.drawable.x_menu);
        _menu.setVisible(true);
        MenuItem _return = menu.findItem(R.id.nav_return);
        _return.setVisible(false);
        MenuItem _refresh = menu.findItem(R.id.nav_Refresh);
        _refresh.setVisible(false);
        MenuItem _billing = menu.findItem(R.id.nav_Tbilling);
//        _billing.setVisible(count_Detail() <= 0 ? false : billing_count() == 1 ? true : false);
        _billing.setVisible(true);
        MenuItem _Table = menu.findItem(R.id.nav_TOut);
        _Table.setIcon(R.drawable.exit);
        MenuItem _Send = menu.findItem(R.id.nav_Send);
        _Send.setVisible(sent_count() > 0 ? true : false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        // Handle actions based on the id field.
        if (id == R.id.nav_Send){
            if (count_Detail() > 0) {
                StaticClass.showAccessCode(getContext(), "Order Send");
                StaticClass.CodeStatus = false;
            } else {
                Snackbar.make(getView(), "There are no order(s) available.", Snackbar.LENGTH_LONG).show();
            }
        }
        if (id == R.id.nav_Tbilling) {
            if (count_Detail() > 0) {
                if (billing_count() > 0) {
                    if (all_checkbox.size() > 0) {
                        StaticClass.showAccessCode(getContext(), "Billing");
                        net_amt = net_amount();
                    } else {
                        Snackbar.make(getView(), "There are no order(s) available.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(getView(), "Please send the order(s) first.", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(getView(), "There are no order(s) available.", Snackbar.LENGTH_LONG).show();
            }
        }
        return true;
    }

    public void GetTable() {
        ResultSet _countTable = connectionString.ConnectionString("EXEC SP_Android_Select_Table '" + StaticClass.TableName + "', 'COUNT'");
        try {
            while (_countTable.next()) {
                StaticClass.TableName = _countTable.getString("TABLEDESC");
                break;
            }
        } catch (Exception e) {

        }
    }

    private void Generate_Layout(final View view) {
        try {
            final DecimalFormat formatter = new DecimalFormat("#,###.00");
            final TextView no_pax = new TextView((getContext()));
            final TextView my_total = new TextView((getContext()));
            final CheckBox h_check_all = new CheckBox((getContext()));
//            final CheckBox to_check_all = new CheckBox((getContext()));
            _totals = new ArrayList<_totalData>();
            String Transaction_Name = null;
            ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_Select_TransNoCount '" + SaveData.Trans_HDRID + "'");
            while (set_x.next()) {
                Transaction_Name = set_x.getString("TransNoCount");
                break;
            }
            set_x.close();
            RelativeLayout finalHolder = (RelativeLayout) view.findViewById(R.id.activity_cart);
            LinearLayout mainHolder = new LinearLayout(getContext());
            mainHolder.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            mainHolder.setOrientation(LinearLayout.VERTICAL);

            LinearLayout mainLayout = new LinearLayout(getContext());
            mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            mainLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout h_Layout = new LinearLayout(getContext());
            h_Layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            h_Layout.setOrientation(LinearLayout.HORIZONTAL);


            final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_OrderBy_ID2 '" + SaveData.Trans_HDRID + "', '" + StaticClass.TableName + "'");
            int set_size = 0;
            while (set.next()) {
                boolean isSetMenu = set.getBoolean("_isSetMenu");

                final LinearLayout holder_Layout = new LinearLayout(getContext());
                holder_Layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                holder_Layout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams rlName = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                rlName.weight = 1;

                final LinearLayout _detail_layout = new LinearLayout(getContext());
                _detail_layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                _detail_layout.setOrientation(LinearLayout.VERTICAL);

                final LinearLayout _desc_layout = new LinearLayout(getContext());
                _desc_layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                _desc_layout.setOrientation(LinearLayout.HORIZONTAL);


                LinearLayout a_layout = new LinearLayout(getContext());
                a_layout.setLayoutParams(new LinearLayout.LayoutParams(370, LinearLayout.LayoutParams.WRAP_CONTENT));
                a_layout.setOrientation(LinearLayout.VERTICAL);

                final LinearLayout b_layout = new LinearLayout(getContext());
                b_layout.setLayoutParams(new LinearLayout.LayoutParams(110, LinearLayout.LayoutParams.WRAP_CONTENT));
                b_layout.setTag( isSetMenu ? "is_set_menu" : "not_set_menu");
                b_layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout c_layout = new LinearLayout(getContext());
                c_layout.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT));
                c_layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout d_layout = new LinearLayout(getContext());
                d_layout.setLayoutParams(new LinearLayout.LayoutParams(190, LinearLayout.LayoutParams.WRAP_CONTENT));
                d_layout.setOrientation(LinearLayout.VERTICAL);

                final TextView description = new TextView((getContext()));
                final TextView Price = new TextView((getContext()));
                final TextView total_price = new TextView((getContext()));
                final EditText Quantity = new EditText(getContext());
                final Button _hold_item = new Button(getContext());
                final Button _void_item = new Button(getContext());
                final Button _resend_item = new Button(getContext());
                final CheckBox _oth_item = new CheckBox(getContext());
                final CheckBox _TO_item = new CheckBox(getContext());


                final UUID ItemID = UUID.fromString(set.getString("ItemID"));
                final UUID TransID = UUID.fromString(set.getString("TransID"));
                final UUID _RowID = UUID.fromString(set.getString("_RowID")); //ID ng DETAIL
//                description.setText(set.getString("ItemCode"));
                final UUID SubsID = UUID.fromString(set.getString("SubsID"));
                final String From = set.getString("From");
                description.setText(set.getString("Description"));
                description.setTextSize(15);


                Price.setLayoutParams(rlName);
                Price.setText(formatter.format(Double.parseDouble(set.getString("RegularPrice"))));
                Price.setTextSize(15);
                Price.setGravity(Gravity.RIGHT);
                Price.setLayoutParams(new LinearLayout.LayoutParams(140, LinearLayout.LayoutParams.WRAP_CONTENT));

                total_price.setLayoutParams(rlName);
                String my_tot = String.valueOf(Double.parseDouble(String.valueOf(set.getString("quantity"))) * Double.parseDouble(set.getString("RegularPrice")));
                total_price.setText(formatter.format(Double.parseDouble(my_tot)));
                total_price.setTextSize(15);
                total_price.setGravity(Gravity.RIGHT);
                total_price.setLayoutParams(new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT));

                String _tag = "btnCode" + String.valueOf(set.getString("ItemCount"));

                Quantity.setKeyListener(DigitsKeyListener.getInstance(true, true));
                Quantity.setLayoutParams(new LinearLayout.LayoutParams(110, LinearLayout.LayoutParams.WRAP_CONTENT));
                Quantity.setGravity(Gravity.RIGHT);
                Quantity.setTextSize(15);
                Quantity.setTag(_tag);
//                Quantity.setText(formatter.format(Double.parseDouble(set.getString("quantity"))));
                Quantity.setText(String.format("%.2f", Double.parseDouble(set.getString("quantity"))));
                Qty = Double.parseDouble(set.getString("quantity"));
                Quantity.setImeOptions(EditorInfo.IME_ACTION_DONE);
                // region TODO: Add filter on quantity to allow only 4 integer and two decimal places
                Quantity.setFilters(new InputFilter[]{StaticClass.filter});
                //endregion
                if (isSetMenu){
                    description.setEnabled(false);
                    Price.setEnabled(false);
                    total_price.setEnabled(false);
                    Quantity.setEnabled(false);
                    _hold_item.setEnabled(false);
                    _void_item.setEnabled(false);
                    _resend_item.setEnabled(false);
                    _oth_item.setEnabled(false);
//                    _TO_item.setVisibility(View.GONE);
                }

                x_data = new _totalData();
                x_data._itemID = Integer.parseInt(set.getString("ItemCount"));
                final String _t_price = total_price.getText().toString().replace("Total: ", "").replace(",", "");
                x_data._total_amount = Double.parseDouble(_t_price);
                if(From.contains("T_detail") && description.isEnabled()) {
                    _totals.add(x_data);
                }

                //region TODO: Quantity Events and Validations
                Quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_MASK_ACTION) {
                            if (!TextUtils.isEmpty(Quantity.getText())) {
                                double _qty = Double.parseDouble(String.valueOf(Quantity.getText()));
                                if (_qty > 0) {
                                    RowID = _RowID;
                                    SUBID = SubsID;
                                    String qty = Quantity.getText().toString().replace(",", "");
                                    String price = Price.getText().toString().replace(",", "");
                                    String _tot = String.valueOf(Double.parseDouble(qty) * Double.parseDouble(price));
                                    total_price.setText(formatter.format(Double.parseDouble(_tot)));
                                    OrderList x = new OrderList();
                                    String text = (String) Quantity.getTag();
                                    String split = text.replace("btnCode", "");
                                    x._menuID = Integer.parseInt(split);
                                    x._quantity = Double.parseDouble(qty);
                                    Qty = Double.parseDouble(qty);
//                            Quantity.setText(formatter.format(x._quantity));
                                    Quantity.setText(String.format("%.2f", x._quantity));
                                    if (!_totals.isEmpty()) {
                                        for (int i = 0; i < _totals.size(); i++) {
                                            _totalData y = _totals.get(i);
                                            if (y._itemID == x._menuID) {
                                                _totals.remove(i);
                                                y._total_amount = Double.parseDouble(_tot);
                                                _totals.add(y);
                                                break;
                                            }
                                        }
                                    }
                                    SaveData.Update_Qty_TransModifierDtl(SUBID, Qty);
                                    final String CountPos = (String) _hold_item.getTag();
                                    final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + x._menuID + "'");
                                    try {
                                        while (set.next()) {
                                            UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                            SaveData.Update_Order(SaveData.Trans_HDRID, ItemID, x._quantity, CountPos);

                                            ResultSet set_menu = connectionString.ConnectionString("EXEC SP_Android_Load_SetMenu_FromParent '" + SaveData.Trans_HDRID + "','" + CountPos + "'");
                                            while (set_menu.next()) {
                                                String qty_tag = set_menu.getString("CountPos");
                                                UUID qty_ItemID = UUID.fromString(set_menu.getString("itemID"));
                                                String item_count = set_menu.getString("ItemCount");

                                                for (int i = 0; i < llQty_Holder.size(); i++) {
                                                    LinearLayout ll_lout = llQty_Holder.get(i);
                                                    if (ll_lout.getTag().equals("is_set_menu")) {
                                                        View x_layout = ll_lout.getChildAt(0);
                                                        if (x_layout instanceof EditText) {
                                                            EditText _et_qty = (EditText) x_layout;
                                                            String _et_qty_tag = _et_qty.getTag().toString().replace("btnCode", "");
                                                            if (_et_qty_tag.equals(item_count)) {
                                                                double fromMFDTL_qty = 0;
                                                                ResultSet z_menu = connectionString.ConnectionString("EXEC SP_Android_Load_SetMenu_ForUpdateQty '" + ItemID + "','" + qty_ItemID + "'");
                                                                while (z_menu.next()) {
                                                                    fromMFDTL_qty = z_menu.getDouble("Qty");
                                                                    break;
                                                                }
                                                                double final_qty = (x._quantity * fromMFDTL_qty);
                                                                SaveData.Update_Order(SaveData.Trans_HDRID, qty_ItemID, final_qty, qty_tag);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }

                                    my_total.setText("Total: " + formatter.format(net_amount()));
                                    Fragment fragment = new MyOrderActivity();
                                    int ft;
                                    ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_main, fragment)
                                            .commit();
                                } else {
                                    StaticClass.showDialog(getContext(), "My Order(s)", "Quantity cannot be equal to zero");
                                }
                            } else {
                                StaticClass.showDialog(getContext(), "My Order(s)", "Quantity cannot be empty");
                            }
                        }
                        return false;
                    }
                });

                Quantity.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                    }
                }));

                Quantity.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        try {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    Quantity.setFocusable(true);
                                    Quantity.requestFocus();
                                    Quantity.setSelection(0, Quantity.getText().length());
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

                LinearLayout _button_layout = new LinearLayout(getContext());
                _button_layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                _button_layout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams b_rlName = new LinearLayout.LayoutParams(70, 40);
//                String hold_text = set.getBoolean("_isHold") == true ? "UnHold" : "Hold";
//                String hold_bgColor = set.getBoolean("_isHold") == true ? "#ffbfdd" : "#ffffff";

                //region HOLD Button
                String hold_text = null;
                String Layout_bgColor = null;
                Boolean _isSend = set.getBoolean("_isSend");
                if(_isSend == true){
                    Layout_bgColor =  "#FFFF00";
                }
                else{
                    Layout_bgColor =  set.getBoolean("_isHold") == true ? "#ffbfdd" : "#ffffff";
                }
                hold_text = set.getBoolean("_isHold") == true ? "Unhold" : "Hold";
                _hold_item.setText(hold_text);
                _hold_item.setBackgroundColor(Color.parseColor("#eaeaea"));
                _hold_item.setGravity(Gravity.CENTER);
                _hold_item.setLayoutParams(b_rlName);
                _hold_item.setTransformationMethod(null);
                _hold_item.setTag(set.getString("CountPos"));
                _hold_item.setTextSize(11);

                ((ViewGroup.MarginLayoutParams) _hold_item.getLayoutParams()).rightMargin = 10;
                ((ViewGroup.MarginLayoutParams) _hold_item.getLayoutParams()).topMargin = 7;
                //region TODO: _hold_item.setOnClickListener((new View.OnClickListener()
                _hold_item.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                Toast myToast = null;
                                try {
                                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                    final OrderList x = new OrderList();
                                    final String split = Quantity.getTag().toString().replace("btnCode", "");
                                    final String holdTag = (String) _hold_item.getTag();
                                    String hold_order = (String) _hold_item.getText();
                                    // Use the Builder class for convenient dialog construction
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    if (hold_order == "Hold") {
                                        builder.setTitle("Hold Order");
                                        builder.setMessage("Are you sure you want to hold '" + description.getText().toString() + "'?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + Integer.parseInt(split) + "'");
                                                try {
                                                    while (set.next()) {
                                                        UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                                        SaveData.Hold_Item(SaveData.Trans_HDRID, ItemID, holdTag, true);
                                                        break;
                                                    }
                                                    Toast.makeText(getContext(), "Order is Hold.", Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {
                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                                _hold_item.setText("Unhold");
                                                holder_Layout.setBackgroundColor(Color.parseColor("#ffbfdd"));
                                                Fragment fragment = new MyOrderActivity();
                                                int ft;
                                                ft = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.content_main, fragment)
                                                        .commit();
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // You don't have to do anything here if you just want it dismissed when clicked
                                                Toast.makeText(getContext(), "Hold is cancelled", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        builder.setTitle("Unhold Order");
                                        builder.setMessage("Are you sure you want to unhold '" + description.getText().toString() + "'?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + Integer.parseInt(split) + "'");
                                                try {
                                                    while (set.next()) {
                                                        UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                                        SaveData.Hold_Item(SaveData.Trans_HDRID, ItemID, holdTag, false);
                                                        break;
                                                    }
                                                    Toast.makeText(getContext(), "Order is unhold.", Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {
                                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                                _hold_item.setText("Hold");
                                                if (!description.isEnabled()){
                                                    SaveData.Update_SetMenuParentByChild(SaveData.Trans_HDRID, ItemID, holdTag);
                                                }
                                                holder_Layout.setBackgroundColor(Color.WHITE);
                                                Fragment fragment = new MyOrderActivity();
                                                int ft;
                                                ft = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.content_main, fragment)
                                                        .commit();
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // You don't have to do anything here if you just want it dismissed when clicked
                                                Toast.makeText(getContext(), "Unhold is cancelled", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    builder.show();
                                } catch (Exception e) {
                                    myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                                    myToast.show();
                                }
                            }
                        })
                );
                //endregion
                //endregion
                //region VOID Button
                _void_item.setText("Void");
                _void_item.setGravity(Gravity.CENTER);
                _void_item.setBackgroundColor(Color.parseColor("#eaeaea"));
                _void_item.setLayoutParams(b_rlName);
                _void_item.setTag(set.getString("CountPos"));
                _void_item.setTransformationMethod(null);
                _void_item.setTextSize(11);
                //region TODO: _void_item.setOnClickListener((new View.OnClickListener()
                _void_item.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                Toast myToast = null;
                                try {
                                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                    final OrderList x = new OrderList();
                                    String text = (String) Quantity.getTag();
                                    final String voidTag = (String) _void_item.getTag();
                                    String split = text.replace("btnCode", "");
                                    x._menuID = Integer.parseInt(split);
                                    x._quantity = Double.parseDouble(Quantity.getText().toString().replace(",", ""));

                                    // Use the Builder class for convenient dialog construction
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Void Order");
                                    builder.setMessage("Are you sure you want to void '" + description.getText().toString() + "'?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            showReason(view, getContext(), x._menuID, voidTag,my_total, formatter, _detail_layout);
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // You don't have to do anything here if you just want it dismissed when clicked
                                            Toast.makeText(getContext(), "Void is cancelled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    builder.show();
                                } catch (Exception e) {
                                    myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                                    myToast.show();
                                }
                            }
                        })
                );
                ((ViewGroup.MarginLayoutParams) _void_item.getLayoutParams()).rightMargin = 10;
                //endregion
                //endregion
                //region RESEND Button
                _resend_item.setEnabled(set.getBoolean("_isSend") == true ? true : false);
                _resend_item.setText("Resend");
                _resend_item.setGravity(Gravity.CENTER);
                _resend_item.setBackgroundColor(Color.parseColor("#eaeaea"));
                _resend_item.setLayoutParams(b_rlName);
                _resend_item.setTag(set.getString("CountPos"));
                _resend_item.setTextSize(11);
                _resend_item.setTransformationMethod(null);
                //region TODO: _resend_item.setOnClickListener((new View.OnClickListener()
                _resend_item.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                Toast myToast = null;
                                try {
                                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                    final OrderList x = new OrderList();
                                    String text = (String) Quantity.getTag();
                                    final String _resend_itemTag = (String) _resend_item.getTag();
                                    String split = text.replace("btnCode", "");
                                    x._menuID = Integer.parseInt(split);
                                    x._quantity = Double.parseDouble(Quantity.getText().toString());

                                    // Use the Builder class for convenient dialog construction
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Resend Order");
                                    builder.setMessage("Resend '" + description.getText().toString() + "' to kitchen?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //region TODO: For future purposes for resend
                                            final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + x._menuID + "'");
                                            try {
                                                while (set.next()) {
                                                    UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                                    SaveData.Resend_Item(SaveData.Trans_HDRID, ItemID, _resend_itemTag);
                                                    break;
                                                }
                                                Toast.makeText(getContext(), "Order is successfully resend.", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                            //endregion
                                            Toast.makeText(getContext(), "Order is successfully resend.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // You don't have to do anything here if you just want it dismissed when clicked
                                            Toast.makeText(getContext(), "Resend is cancelled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    builder.show();
                                } catch (Exception e) {
                                    myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
                                    myToast.show();
                                }
                            }
                        })
                );
                //endregion
                //endregion
                //region ON THE HOUSE Button
                boolean _OTH = set.getBoolean("_OTH") == true ? true : false;
                _oth_item.setText("OTH");
                _oth_item.setChecked(_OTH);
                _oth_item.setGravity(Gravity.CENTER);
                _oth_item.setLayoutParams(new LinearLayout.LayoutParams(80, 40));
                _oth_item.setTag(set.getString("CountPos"));
                _oth_item.setTransformationMethod(null);
                _oth_item.setTextSize(11);

                ((ViewGroup.MarginLayoutParams) _oth_item.getLayoutParams()).topMargin = 7;
                if (_OTH == true){
                    all_check_state.add(_OTH);
                }
                _oth_item.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                chk = _oth_item;
                                Single = true;
                                _oth_item.setChecked(_oth_item.isChecked() == true ? false : true);
                                StaticClass.showAccessCode(getContext(), "OTH");
                            }
                        })
                );
                //endregion
                //region Take Out Button
                boolean _TO = set.getBoolean("isTakeOut") == true ? true : false;
                _TO_item.setText("T.O");
                _TO_item.setChecked(_TO);
                _TO_item.setGravity(Gravity.CENTER);
                _TO_item.setLayoutParams(new LinearLayout.LayoutParams(80, 40));
                _TO_item.setTag(set.getString("CountPos"));
                _TO_item.setTransformationMethod(null);
                _TO_item.setTextSize(11);
                ((ViewGroup.MarginLayoutParams) _TO_item.getLayoutParams()).topMargin = 7;
                //endregion
                //region TODO: Checked Change Listener when OTH_item is clicked
                _oth_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(final CompoundButton button, boolean b) {
                        final OrderList x = new OrderList();
                        final String item_id = Quantity.getTag().toString().replace("btnCode", "");
                        final String _tag = (String) _hold_item.getTag();
                        if (b) {
                            final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + Integer.parseInt(item_id) + "'");
                            try {
                                while (set.next()) {
                                    UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                    SaveData.OTH_Item(SaveData.Trans_HDRID, ItemID, _tag, true);
                                    break;
                                }
                                h_check_all.setChecked(check_state());
//                                Toast.makeText(getContext(), description.getText().toString() + " is successfully added to OTH.", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + Integer.parseInt(item_id) + "'");
                            try {
                                while (set.next()) {
                                    UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                    SaveData.OTH_Item(SaveData.Trans_HDRID, ItemID, _tag, false);
                                    break;
                                }
                                h_check_all.setChecked(false);
//                                Toast.makeText(getContext(), description.getText().toString() + " is successfully removed from OTH.", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                _hold_item.setEnabled(set.getBoolean("_isSend") == true ? false :  isSetMenu ?  false : true);
//                _hold_item.setEnabled(set.getBoolean("_isSend") == true ? false : true);
//                    _void_item.setEnabled(set.getBoolean("_isSend") == true ? false : isSetMenu ?  false : true);
                Quantity.setEnabled(set.getBoolean("_isSend") == true ? false : isSetMenu ?  false : true);

                if(set.getBoolean("_isSend") == false){
                    description.setOnClickListener((new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showModifier(view,getContext(), TransID, _RowID, ItemID, SubsID);
                        }
                    }));
                }
                String ModifierID = set.getString("MODIFIERID");
                if(From.contains("T_Modifier")){
                    ResultSet _modifset = connectionString.ConnectionString("EXEC SP_Android_CheckModifier_Detail '" + SubsID + "','" + ModifierID + "' ");
                    while(_modifset.next()){
                        if(_modifset.getBoolean("_isSend") == true){
                            Layout_bgColor =  "#FFFF00";
                        }
                        else{
                            Layout_bgColor =  _modifset.getBoolean("_isHold") == true ? "#ffbfdd" : "#ffffff";
                        }
                        description.setTypeface(null, Typeface.ITALIC);
                        description.setText("\t\t" + description.getText());
                        Quantity.setVisibility(View.INVISIBLE);
//                        Price.setVisibility(View.INVISIBLE);
                        Price.setTypeface(null, Typeface.ITALIC);
                        Price.setText("(" + formatter.format(Double.parseDouble(set.getString("RegularPrice"))) + " X " + Qty + ")");
                        total_price.setText("0.00");
                        total_price.setVisibility(View.INVISIBLE);
                        _void_item.setVisibility(View.INVISIBLE);
                        _hold_item.setVisibility(View.INVISIBLE);
                        _oth_item.setVisibility(View.INVISIBLE);
                        _TO_item.setVisibility(View.INVISIBLE);
                        _resend_item.setVisibility(View.INVISIBLE);
                    }

                }
//                _oth_item.setEnabled(set.getBoolean("_isSend") == true ? false : true);
//                h_check_all.setEnabled(set.getBoolean("_isSend") == true ? false : true);
                //endregion
                //region TODO: Checked Change Listener when TO_item is clicked
                _TO_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(final CompoundButton button, boolean b) {
                        final OrderList x = new OrderList();
                        final String item_id = Quantity.getTag().toString().replace("btnCode", "");
                        final String _tag = (String) _hold_item.getTag();
                        if (b) {
                            final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + Integer.parseInt(item_id) + "'");
                            try {
                                while (set.next()) {
                                    UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                    SaveData.TakeOut_Item(SaveData.Trans_HDRID, ItemID, _tag, true, Double.parseDouble(total_price.getText().toString().replace(",", "")));
                                    break;
                                }
//                                to_check_all.setChecked(to_check_state());
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + Integer.parseInt(item_id) + "'");
                            try {
                                while (set.next()) {
                                    UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                    SaveData.TakeOut_Item(SaveData.Trans_HDRID, ItemID, _tag, false, Double.parseDouble(total_price.getText().toString().replace(",", "")));
                                    break;
                                }
//                                to_check_all.setChecked(false);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                _hold_item.setEnabled(set.getBoolean("_isSend") == true ? false : isSetMenu ?  false : true);
//                _hold_item.setEnabled(set.getBoolean("_isSend") == true ? false : true);
//                  _void_item.setEnabled(set.getBoolean("_isSend") == true ? false : isSetMenu ?  false : true);
                Quantity.setEnabled(set.getBoolean("_isSend") == true ? false : isSetMenu ?  false : true);
                if(set.getBoolean("_isSend") == false){
                    description.setOnClickListener((new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showModifier(view,getContext(), TransID, _RowID, ItemID, SubsID);
                        }
                    }));

                }
                String ModifiedID = set.getString("MODIFIERID");
                if(From.contains("T_Modifier")){
                    ResultSet _modifset = connectionString.ConnectionString("EXEC SP_Android_CheckModifier_Detail '" + SubsID + "','" + ModifiedID + "' ");
                    while(_modifset.next()){
                        if(_modifset.getBoolean("_isSend") == true){
                            Layout_bgColor =  "#FFFF00";
                        }
                        else{
                            Layout_bgColor =  _modifset.getBoolean("_isHold") == true ? "#ffbfdd" : "#ffffff";
                        }
                        description.setTypeface(null, Typeface.ITALIC);
                        description.setText("\t\t" + description.getText());
                        Quantity.setVisibility(View.INVISIBLE);
//                        Price.setVisibility(View.INVISIBLE);
//                        Price.setTypeface(null, Typeface.ITALIC);
//                        Price.setText("(" + formatter.format(Double.parseDouble(set.getString("RegularPrice"))) + " X " + Qty + ")");
                        Price.setVisibility(View.INVISIBLE);
//                        total_price.setText("0.00");
                        total_price.setVisibility(View.INVISIBLE);
                        _void_item.setVisibility(View.INVISIBLE);
                        _hold_item.setVisibility(View.INVISIBLE);
                        _oth_item.setVisibility(View.INVISIBLE);
                        _TO_item.setVisibility(View.INVISIBLE);
                        _resend_item.setVisibility(View.INVISIBLE);
                    }

                }
//                _oth_item.setEnabled(set.getBoolean("_isSend") == true ? false : true);
//                h_check_all.setEnabled(set.getBoolean("_isSend") == true ? false : true);
                //endregion
                //region TODO: Main Layout adding views
                a_layout.addView(description);
                b_layout.addView(Quantity);
                c_layout.addView(Price);
                d_layout.addView(total_price);

                //add b_layout to list for set menu purposes
                llQty_Holder.add(b_layout);

                ((ViewGroup.MarginLayoutParams) a_layout.getLayoutParams()).topMargin = 20;
                ((ViewGroup.MarginLayoutParams) c_layout.getLayoutParams()).topMargin = 20;
//                ((ViewGroup.MarginLayoutParams) c_layout.getLayoutParams()).leftMargin = 20;
                ((ViewGroup.MarginLayoutParams) d_layout.getLayoutParams()).topMargin = 20;

                _desc_layout.addView(a_layout);
                _desc_layout.addView(b_layout);
//                _desc_layout.addView(Quantity);
                _desc_layout.addView(c_layout);
                _desc_layout.addView(d_layout);
//                if(_isSend == true){
//                    _desc_layout.addView(_hold_item);
//                }
                _desc_layout.addView(_hold_item);
                _desc_layout.addView(_void_item);
                _desc_layout.addView(_resend_item);
                _desc_layout.addView(_oth_item);
                _desc_layout.addView(_TO_item);
                _detail_layout.addView(_desc_layout);

                holder_Layout.addView(_detail_layout);
                holder_Layout.setBackgroundColor(Color.parseColor(Layout_bgColor));

                ((ViewGroup.MarginLayoutParams) holder_Layout.getLayoutParams()).bottomMargin = 2;
                ((ViewGroup.MarginLayoutParams) _detail_layout.getLayoutParams()).leftMargin = 10;
                mainLayout.addView(holder_Layout);
                //endregion
                //add id for check/unchecked all usage

                all_checkbox.add(_oth_item);
                all_to_checkbox.add(_TO_item);
                set_size++;


            }
            set.close();
            h_check_all.setChecked(set_size == 0 ? false : set_size == all_check_state.size() ? true : false);
//            to_check_all.setChecked(set_size == 0 ? false : set_size == all_to_check_state.size() ? true : false);
//
            //region TODO: header
            LinearLayout.LayoutParams h_rlName = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            h_rlName.weight = 1;

            TextView my_orders = new TextView((getContext()));
            my_orders.setText("TN: " + SaveData.Table_Number.toLowerCase().replace("table ", "").toUpperCase());
            my_orders.setTextSize(20);
            my_orders.setPadding(0, 5, 0, 5);
            my_orders.setGravity(Gravity.LEFT);
            my_orders.setLayoutParams(h_rlName);
            my_orders.setLayoutParams(new LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView trans_number = new TextView((getContext()));
            trans_number.setText("Trans. #: " + Transaction_Name);
            trans_number.setTextSize(20);
            trans_number.setPadding(0, 5, 0, 5);
            trans_number.setGravity(Gravity.CENTER);
            trans_number.setLayoutParams(h_rlName);
            trans_number.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));

            no_pax.setText("PAX: " + SaveData.Pax_Number);
            no_pax.setTextSize(20);
            no_pax.setPadding(0, 5, 0, 5);
            no_pax.setGravity(Gravity.CENTER);
            no_pax.setLayoutParams(h_rlName);

            my_total.setTextSize(20);
            my_total.setText("Total: " + formatter.format(net_amount()));
            my_total.setGravity(Gravity.CENTER);
            my_total.setPadding(0, 5, 0, 5);

            h_Layout.addView(my_orders);
            h_Layout.addView(trans_number);
            h_Layout.addView(no_pax);
            h_Layout.addView(my_total);
            h_Layout.setBackgroundColor(Color.WHITE);

            ((ViewGroup.MarginLayoutParams) my_orders.getLayoutParams()).leftMargin = 10;
            ((ViewGroup.MarginLayoutParams) my_total.getLayoutParams()).rightMargin = 10;
            ((ViewGroup.MarginLayoutParams) h_Layout.getLayoutParams()).bottomMargin = 10;
            //endregion
            //region TODO: Menu Heading Items
            LinearLayout menuholder_Layout = new LinearLayout(getContext());
            menuholder_Layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 40));
            menuholder_Layout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams header_rlName = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
            header_rlName.weight = 1;

            LinearLayout menu_detail_layout = new LinearLayout(getContext());
            menu_detail_layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            menu_detail_layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout menu_desc_layout = new LinearLayout(getContext());
            menu_desc_layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            menu_desc_layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView h_description = new TextView((getContext()));
            h_description.setLayoutParams(header_rlName);
            h_description.setText("Menu");
            h_description.setTextSize(18);

            TextView h_price = new TextView((getContext()));
            h_price.setText("Price");
            h_price.setGravity(Gravity.RIGHT);
            h_price.setTextSize(18);
            h_price.setLayoutParams(new LinearLayout.LayoutParams(140, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView h_total_price = new TextView((getContext()));
            h_total_price.setText("Total");
            h_total_price.setTextSize(18);
            h_total_price.setGravity(Gravity.RIGHT);
            h_total_price.setLayoutParams(new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView h_quantity = new TextView(getContext());
            h_quantity.setLayoutParams(new LinearLayout.LayoutParams(110, LinearLayout.LayoutParams.WRAP_CONTENT));
            h_quantity.setTextSize(18);
            h_quantity.setText("Quantity");
            h_quantity.setGravity(Gravity.RIGHT);

            h_check_all.setLayoutParams(header_rlName);
            h_check_all.setText("All");
            h_check_all.setTextSize(14);
            h_check_all.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((ViewGroup.MarginLayoutParams) h_check_all.getLayoutParams()).leftMargin = 240;
            //region TODO: OnCLick Listener when check/uncheck all is clicked
            h_check_all.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Single = false;
                    h_check_all.setChecked(h_check_all.isChecked() == true ? false : true);
                    chk = h_check_all;

                    StaticClass.showAccessCode(getContext(),"OTH");
                }
            });
            //endregion

//            to_check_all.setLayoutParams(header_rlName);
//            to_check_all.setText("All");
//            to_check_all.setTextSize(14);
//            to_check_all.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            ((ViewGroup.MarginLayoutParams) to_check_all.getLayoutParams()).leftMargin = 16;
//            //region TODO: OnCLick Listener when check/uncheck all is clicked
//            to_check_all.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    Single = false;
//                    to_check_all.setChecked(to_check_all.isChecked() == true ? false : true);
//                    chk = to_check_all;
//                    StaticClass.showAccessCode(getContext(),"Take Out");
//                }
//            });
            //endregion
            LinearLayout a1_layout = new LinearLayout(getContext());
            a1_layout.setLayoutParams(new LinearLayout.LayoutParams(370, LinearLayout.LayoutParams.WRAP_CONTENT));
            a1_layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout b1_layout = new LinearLayout(getContext());
            b1_layout.setLayoutParams(new LinearLayout.LayoutParams(110, LinearLayout.LayoutParams.WRAP_CONTENT));
            b1_layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout c1_layout = new LinearLayout(getContext());
            c1_layout.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT));
            c1_layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout d1_layout = new LinearLayout(getContext());
            d1_layout.setLayoutParams(new LinearLayout.LayoutParams(190, LinearLayout.LayoutParams.WRAP_CONTENT));
            d1_layout.setOrientation(LinearLayout.VERTICAL);

            a1_layout.addView(h_description);
            b1_layout.addView(h_quantity);
            c1_layout.addView(h_price);
            d1_layout.addView(h_total_price);

//            ((ViewGroup.MarginLayoutParams) c1_layout.getLayoutParams()).leftMargin = 20;

            menu_desc_layout.addView(a1_layout);
            menu_desc_layout.addView(b1_layout);
            menu_desc_layout.addView(c1_layout);
            menu_desc_layout.addView(d1_layout);
            menu_desc_layout.addView(h_check_all);
//            menu_desc_layout.addView(to_check_all);
            menu_detail_layout.addView(menu_desc_layout);

            menuholder_Layout.addView(menu_detail_layout);
            ((ViewGroup.MarginLayoutParams) menuholder_Layout.getLayoutParams()).bottomMargin = 5;
            ((ViewGroup.MarginLayoutParams) menu_detail_layout.getLayoutParams()).leftMargin = 10;
            menuholder_Layout.setBackgroundColor(Color.WHITE);
            //endregion

            ScrollView scroll = new ScrollView(getContext());
            scroll.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            scroll.addView(mainLayout);

            mainHolder.addView(h_Layout);
            mainHolder.addView(menuholder_Layout);
            mainHolder.addView(scroll);
            finalHolder.addView(mainHolder);
        } catch (Exception e) {
//            Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            Log.w("Error connection", "" + e.getMessage());
        }
    }

    public boolean check_state(){
        boolean status = true;
        for (int i = 0; i < all_checkbox.size(); i++) {
            if (all_checkbox.get(i).isChecked() == false){
                status = false;
                break;
            }
        }
        return status;
    }

    public boolean to_check_state(){
        boolean status = true;
        for (int i = 0; i < all_to_checkbox.size(); i++) {
            if (all_to_checkbox.get(i).isChecked() == false){
                status = false;
                break;
            }
        }
        return status;
    }

    public double net_amount(){
        double _totalAmount = 0;
        if (!_totals.isEmpty()){
            for (int j = 0; j < _totals.size(); j++){
                _totalData _data = _totals.get(j);
                _totalAmount  += _data._total_amount;
            }
        }
        return _totalAmount;
    }

    public void showModifier(View view, final Context mContext, final UUID TransID, final UUID _RowID, final UUID itemID, final UUID SubsID) {
        _ListModif.clear();
        final Dialog dialogOptions = new Dialog(mContext);
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_Modifier '" + itemID  + "','" + itemID  + "','GET MODIFIER'");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate(R.layout.activity_modifier, null);

        final GridLayout gridLayout = new GridLayout(getContext());
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setColumnCount(5);
        int ctr = 0;
        try {
            while (set.next()) {
                ctr++;
                LinearLayout albumLayout = new LinearLayout(getContext());
                RelativeLayout albumDetailsLayout = new RelativeLayout(getContext());
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
                final Button btnModifier = new Button(getContext());
                btnModifier.setText(set.getString("Description"));
                final UUID ModifierID;
                ModifierID = UUID.fromString(set.getString("Modifier_Row_ID"));
                btnModifier.setTextColor(Color.BLACK);
//                LinearLayout.LayoutParams img_param = new LinearLayout.LayoutParams(100,100);
//                btnModifier.setLayoutParams(img_param);
                btnModifier.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                btnModifier.setPadding(0,0,0,0);
                btnModifier.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t = new TextView(mContext);
                                if(!_ListModif.contains((String) btnModifier.getText()))
                                {
                                    _ListModif.add((String) btnModifier.getText());
                                }
                                else{
                                    _ListModif.remove((String) btnModifier.getText());
                                }
                                ColorStateList cList = btnModifier.getTextColors();
                                int color = cList.getDefaultColor();
                                switch(color) {
                                    case Color.RED:
                                        btnModifier.setTextColor(Color.BLACK);
                                        SaveData.Delete_Modifier(_RowID,ModifierID,SubsID);
                                        break;
                                    case Color.BLACK:
                                        btnModifier.setTextColor(Color.RED);
                                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_Modifier '" + itemID + "','" + _RowID + "','GET MODIFIER DETAILS'");
                                        UUID SubsID = null;
                                        try {
                                            while (set.next()) {
                                                SubsID = UUID.fromString(set.getString("SubsID"));
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT);
                                        }
                                        SaveData.Insert_DetailModifier(TransID, SubsID,ModifierID, Qty, itemID);
                                        break;
                                }
                                Fragment fragment = new MyOrderActivity();
                                int ft;
                                ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_main, fragment)
                                        .commit();
                                //region getmodifiers
                                //                                String a ="";
//                                for(String Con : _ListModif){
//                                    a += Con;
//                                }
//                                t.setText(a);
//                                gridLayout.addView(t);
                                //endregion
                            }
                        })
                );

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(150,90);
                btnModifier.setLayoutParams(param);
                ((ViewGroup.MarginLayoutParams) btnModifier.getLayoutParams()).rightMargin = 10;
                ((ViewGroup.MarginLayoutParams) btnModifier.getLayoutParams()).leftMargin = 10;
                ((ViewGroup.MarginLayoutParams) btnModifier.getLayoutParams()).bottomMargin = 5;
                ((ViewGroup.MarginLayoutParams) btnModifier.getLayoutParams()).topMargin = 5;
                ResultSet _modifResult = connectionString.ConnectionString("EXEC SP_Android_Select_TransDtlModifier '" + _RowID + "'");
                String Modifier = (String)btnModifier.getText();
                while(_modifResult.next()){
                    String _modif = (String)_modifResult.getString("Modifier");
                    if(Modifier.contains(_modif)){
                        btnModifier.setTextColor(Color.RED);
                    }
                }
                pictureRowHolder.addView(btnModifier);
                pictureHolder.addView(pictureRowHolder);
                albumLayout.addView(pictureHolder);
                gridLayout.addView(albumLayout);
                gridLayout.setBackgroundColor(Color.WHITE);
            }
            dialogOptions.setContentView (view);
            if(ctr < 5){
                int initial_size = 210;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    gridLayout.setForegroundGravity(Gravity.CENTER);
                }
                WindowManager.LayoutParams paramView =  dialogOptions.getWindow().getAttributes();
                paramView.y = 30;
                paramView.x = 30;
                paramView.width = initial_size * ctr;
                dialogOptions.getWindow().setAttributes(paramView);

            }
            ScrollView scroll = new ScrollView(getContext());
            scroll.addView(gridLayout);
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.addView(scroll);
            view = inflater.inflate(R.layout.activity_modifier, viewGroup);


            dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogOptions.show();
        } catch (Exception e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
        DOptions = dialogOptions;

        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptions.show();
    }

    public void showReason(View view, final Context mContext, final Integer _menuID, final String voidTag, final TextView my_total, final DecimalFormat formatter, final LinearLayout _detail_layout) {
        final Dialog dialogOptions = new Dialog(mContext);
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_Reason 'VOID'");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate(R.layout.activity_blank, null);
        ImageButton close = (ImageButton)view.findViewById(R.id.btnClose);
        close.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DOptions.hide();
            }
        }));
        LinearLayout ll_main = new LinearLayout(mContext);
        ll_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ll_main.setOrientation(LinearLayout.VERTICAL);
        LinearLayout ll_header = new LinearLayout(mContext);
        ll_header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
        ll_header.setOrientation(LinearLayout.HORIZONTAL);
        ll_header.setBackgroundColor(Color.parseColor("#34495e"));
        try {
            RadioGroup rg = new RadioGroup(mContext);
            while (set.next()) {
                final RadioButton rb = new RadioButton(mContext);
                final UUID rb_ID = UUID.fromString(set.getString("ReasonID"));
                rb.setText(set.getString("ReasonDesc"));
                rb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReasonID = rb_ID;
                    }
                });
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
                rb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rb.setPadding(20,0,0,0);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(250,90);
                rb.setLayoutParams(param);
                ((ViewGroup.MarginLayoutParams) rb.getLayoutParams()).rightMargin = 0;
                ((ViewGroup.MarginLayoutParams) rb.getLayoutParams()).leftMargin = 150;
                ((ViewGroup.MarginLayoutParams) rb.getLayoutParams()).bottomMargin = 0;
                ((ViewGroup.MarginLayoutParams) rb.getLayoutParams()).topMargin = 0;
                rb.setLeft(50);
                rg.addView(rb);
                rg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            TextView txt_Title = new TextView(mContext);
            txt_Title.setText("VOID REASON");
            txt_Title.setTextSize(40);
            txt_Title.setPadding(10,0,20,0);
            txt_Title.setTextColor(Color.WHITE);
            Button btn_Finish = new Button(mContext);
            Button btn_All = new Button(mContext);
            btn_All.setText("Void All");
            btn_Finish.setText("Void");
            btn_All.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(ReasonID != null){
                        SaveData.VoidAll(SaveData.Trans_HDRID, ReasonID);
                        if (!_totals.isEmpty()) {
                            for (int i = 0; i < _totals.size(); i++) {
                                _totalData y = _totals.get(i);
                                if (y._itemID == _menuID) {
                                    _totals.remove(i);
                                }
                            }
                        }
                        my_total.setText("Total: " + formatter.format(0));
                        Toast.makeText(getContext(), "Order is successfully removed.", Toast.LENGTH_LONG).show();
                        DOptions.hide();
                        Fragment fragment = new MyOrderActivity();
                        int ft;
                        ft = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, fragment)
                                .commit();
                        _detail_layout.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(getContext(), "You must Select Reason First..", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            btn_Finish.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    if(ReasonID != null){
                        final ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + _menuID + "'");
                        try {
                            while (set.next()) {
                                UUID ItemID = UUID.fromString(set.getString("ItemID"));
                                SaveData.Void_Item(SaveData.Trans_HDRID, ItemID, voidTag, ReasonID);
                                break;
                            }
                            if (!_totals.isEmpty()) {
                                for (int i = 0; i < _totals.size(); i++) {
                                    _totalData y = _totals.get(i);
                                    if (y._itemID == _menuID) {
                                        _totals.remove(i);
                                    }
                                }
                            }
                            my_total.setText("Total: " + formatter.format(net_amount()));
                            Toast.makeText(getContext(), "Order is successfully removed.", Toast.LENGTH_LONG).show();
                            DOptions.hide();
                            Fragment fragment = new MyOrderActivity();
                            int ft;
                            ft = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, fragment)
                                    .commit();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                        _detail_layout.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(getContext(), "You must Select Reason First..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btn_Finish.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT));

            btn_All.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT));
            ll_header.addView(txt_Title);
            ll_header.addView(btn_All);
            ll_header.addView(btn_Finish);
            ll_main.addView(ll_header);
            ll_main.addView(rg);

            ll_main.setBackgroundColor(Color.WHITE);
            dialogOptions.setContentView (view);
            ScrollView scroll = new ScrollView(getContext());
            scroll.addView(ll_main);
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.addView(scroll);
            dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = dialogOptions.getWindow().getAttributes();
            lp.width = 770;
            lp.gravity = Gravity.CENTER;
            lp.dimAmount = 0.9f;
            dialogOptions.show();
        } catch (Exception e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
        DOptions = dialogOptions;
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = dialogOptions.getWindow().getAttributes();
        lp.width = 800;
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0.9f;
        dialogOptions.show();
    }
}
class _totalData{
    public int _itemID;
    public double _total_amount;
}
