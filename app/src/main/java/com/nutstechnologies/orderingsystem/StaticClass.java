package com.nutstechnologies.orderingsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JAYBOB on 5/31/2017.
 */

public class StaticClass {
    private Context mContext;
    public static int PaxNo;
    public static String TableName;
    public static String Current_Fragment;
    public static String OrderStatus;
    public static String FormCode;
    public static boolean CodeStatus;
    public static Dialog DialogCode;
    public static String From;
        public static Map<UUID,Float> ItemsTobeMove = new HashMap<UUID, Float>();
//    public static Map<String,Float> ItemsTobeMove = new HashMap<String, Float>();
//    public static List<String> ItemsTobeMove = new ArrayList<String>();
    public static boolean done = false;
    public static void showNumPad(final Context mContext, final Fragment fragment, final String Table, final String joinTable_id, final String parent_Table){
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.activity_keyboard);
        KeyListener keyListener = DigitsKeyListener.getInstance("1234567890");
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        final EditText Pax = (EditText) dialog.findViewById(R.id.editText);
        final TextView Label = (TextView) dialog.findViewById(R.id.TextViewFor);
        Pax.setTextSize(100);
        Pax.setMaxLines(3);
        int maxLength = 2;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        Pax.setFilters(fArray);
        Pax.setKeyListener(keyListener);
        Pax.setGravity(Gravity.CENTER);
        Label.setText("Enter Number of Person");
        Label.setVisibility(View.VISIBLE);
        Label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        OrderStatus = "New";
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lpx = dialog.getWindow().getAttributes();
        lpx.dimAmount = 0.9f;
        dialog.show();
        Pax.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PaxNo = Integer.parseInt(String.valueOf(Pax.getText()));
                    dialog.hide();

                    int ft;
                    ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, fragment,"table summary")
                            .commit();
                    SaveData.TableSave(TableName , PaxNo, joinTable_id, parent_Table);
                    TableName = Table;
                    MainActivity.Order_Prev = "MenuCategory";
                    if (!TextUtils.isEmpty(joinTable_id)){
//                        SaveData.Update_tblJoinerStatus("InUse", Integer.parseInt(joinTable_id));
                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'in_used', '" + SaveData.Trans_HDRID + "'");
                    }
                }
                return false;
            }
       });
    }
    public static void showQty(final Context mContext,final String Table,final UUID _RowID,final UUID itemID,final LinearLayout insideLinear2, final TextView desc, final float qty){
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.activity_showqty);
        KeyListener keyListener = DigitsKeyListener.getInstance("1234567890.");
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        final EditText TransQty = (EditText) dialog.findViewById(R.id.EditTransQty);
        final EditText OrigQty = (EditText) dialog.findViewById(R.id.EditOrigQty);
        final TextView _txtOrig = (TextView) dialog.findViewById(R.id.txtOrigQty);
        final TextView _transqty = (TextView) dialog.findViewById(R.id.txtTransQty);
        final TextView _txtItem = (TextView) dialog.findViewById(R.id.txtItem);

        TransQty.setTextSize(100);
        OrigQty.setTextSize(100);
        TransQty.setMaxLines(3);
        int maxLength = 3;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        TransQty.setFilters(fArray);
        TransQty.setKeyListener(keyListener);
        TransQty.setGravity(Gravity.CENTER);
        OrigQty.setText(String.valueOf(qty));
//      OrigQty.setGravity(Gravity.CENTER);
        String descValue = (String) desc.getText();
//        String[] Char = descValue.split("\t");
//        descValue = Char[1];
        _txtItem.setText(descValue);
        _txtItem.setVisibility(View.VISIBLE);
        _txtItem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        _txtOrig.setText("Original Quantity");
        _txtOrig.setVisibility(View.VISIBLE);
        _txtOrig.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        _transqty.setText("Enter Quantity");
        _transqty.setVisibility(View.VISIBLE);
        _transqty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        TransQty.requestFocus();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TransQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ResultSet _qtySet = connectionString.ConnectionString("EXEC SP_Android_CheckQty '" + Table + "', '" + _RowID + "', '" + itemID + "'");
                    float itemQty = 0;
                    UUID DetailID = null;
                    try {

                        while(_qtySet.next()){
                            itemQty =_qtySet.getFloat("_QTY");
                            DetailID = UUID.fromString(_qtySet.getString("_RowID"));
                           break;
                        }
                    } catch (Exception e) {

                    }
                    try{
                        float qty = Float.parseFloat(TransQty.getText().toString());
                            if(qty > itemQty){
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Error Occured");
                                builder.setMessage("Transfer Item must not be greater than the Orignal Qty");
                                builder.setIcon(R.mipmap.ic_warning);
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog2 = builder.show();
                                TextView messageText = (TextView)dialog.findViewById(R.id.txtOrigQty);
                                messageText.setGravity(Gravity.CENTER);
                                dialog2.show();
                            }
                            else if (qty <= 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Error Occured");
                                builder.setMessage("Quantity must not zero.");
                                builder.setIcon(R.mipmap.ic_warning);
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog2 = builder.show();
                                TextView messageText = (TextView)dialog.findViewById(R.id.txtOrigQty);
                                messageText.setGravity(Gravity.CENTER);
                                dialog2.show();
                            }
                            else{
                                TableActivity.setTransfer(mContext,insideLinear2,null,DetailID,desc,qty,null);
                                dialog.hide();
                            }
                    }
                    catch(Exception e){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setTitle("Error Occured");
                        builder.setMessage("Please Enter Quantity");
//                        builder.setIcon(R.mipmap.ic_warning);
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog2 = builder.show();
                        TextView messageText = (TextView)dialog.findViewById(R.id.txtOrigQty);
                        messageText.setGravity(Gravity.CENTER);
                        dialog2.show();
                    }
                }
                else {

                }
                return false;
            }
        });
    }
    public static void showAccessCode(final Context mContext, final String Command){
        final Dialog dialog = new Dialog(mContext);
        DialogCode = dialog;
        dialog.setContentView(R.layout.activity_keyboard);
        final EditText Code = (EditText) dialog.findViewById(R.id.editText);
        final TextView Label = (TextView) dialog.findViewById(R.id.TextViewFor);
        Label.setText("Enter Login Code");
        Label.setVisibility(View.VISIBLE);
        Label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Code.setTextSize(100);
//        Code.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Code.setTransformationMethod(new HiddenPassTransformationMethod());
        Code.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Code.getRevealOnFocusHint();
        }
        Code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_MASK_ACTION) {
                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_ManagerCode '" + Code.getText() + "'");
                    String ManagerName = null;
                    try {
                        while(set.next())
                        {
                            ManagerName = set.getString("EmployeeName");
                        }
                    } catch (Exception e) {
                        CodeStatus = false;
                    }
                    if(ManagerName != null) {
                        CodeStatus = true;
                        dialog.hide();
                        if (Command == "Table Activity") {
                            TableActivity.close.performClick();
                        } else if (Command == "Order Send") {
                            MyOrderActivity.SendOrder(mContext);
                        } else if (Command == "OTH") {
                            dialog.hide();
                            MyOrderActivity.SetClickable(dialog);
                        } else if (Command == "Take Out") {
                            dialog.hide();
                            MyOrderActivity.TakeOut_SetClickable(dialog);
                        } else if (Command == "Merge") {
                            TableActivity.merge.performClick();
                        } else if (Command == "joiner merge") {
                            TableActivity.merge.performClick();
                        } else if (Command == "Billing") {

                            MyOrderActivity.SendBilling(mContext);
                        }
                    }
                    else{
                        CodeStatus = false;
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Code Error");
                        builder.setMessage("Login Code is Incorrect");
                        builder.setIcon(R.mipmap.ic_warning);
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog2 = builder.show();
                        TextView messageText = (TextView)dialog.findViewById(R.id.TextViewMessage);
                        messageText.setGravity(Gravity.CENTER);
                        dialog2.show();
                    }
                }
                return false;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public static void CloseDialog(Dialog dialog){
        dialog.hide();
    }
    public static void showDialog(View v, int id) {
        // custom dialog
        final Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.custom_dialog);
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
//        close.setVisibility(View.GONE);
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectItembyIdentity '" + id + "'");
        try
        {
//            TextView text_name = (TextView) dialog.findViewById(R.id.txtItemName);
//            TextView text_price = (TextView) dialog.findViewById(R.id.txtPrice);
            ImageView img_view = (ImageView) dialog.findViewById(R.id.itemImage);
            while(set.next()){
//                text_name.setText(set.getString("Description"));
//                text_name.setTextSize(20);
//                text_price.setText("Price: " + set.getString("RegularPrice"));
//                text_price.setTextSize(20);
                if (set.getString("ItemImage") != null) {
                    img_view.setImageBitmap(bmp(set.getString("ItemImage")));
//                    img_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                else {
                    img_view.setImageResource(R.drawable.no_image);
//                    img_view.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                img_view.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
        catch (Exception e)
        {
            Toast myToast = null;
            myToast = Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_LONG);
            myToast.show();
        }
        // Close Button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public static Bitmap bmp (String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public static List<OrderList> save_data = new ArrayList<OrderList>();
    public static void Get_Data(OrderList lx) {
        if (save_data.isEmpty()){
            save_data = new ArrayList<OrderList>();
        }
        save_data.add(lx);
    }
    public static void showDialog2(View v, int id) {
        // custom dialog
        final Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.custom_dialog);
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);

    }
    public static InputFilter filter = new InputFilter() {
        final int maxDigitsBeforeDecimalPoint=4;
        final int maxDigitsAfterDecimalPoint=2;

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            StringBuilder builder = new StringBuilder(dest);
            builder.toString().replace(",","");
            builder.replace(dstart, dend, source
                    .subSequence(start, end).toString());
            if (!builder.toString().matches(
                    "(([1-9]{1})([0-9]{0,"+(maxDigitsBeforeDecimalPoint-1)+"})?)?(\\.[0-9]{0,"+maxDigitsAfterDecimalPoint+"})?"

            )) {
                if(source.length()==0)
                    return dest.subSequence(dstart, dend);
                return "";
            }

            return null;

        }
    };
    public static UUID getItemID(UUID _RowID){
//        public static UUID getItemID(String ItemDesc){
        UUID itemID = null;

//        if(ItemDesc.contains("'") == true){
//            ItemDesc.split("'");
//            String[] Char = ItemDesc.split("'");
//            ItemDesc = Char[0] + "''" + Char[1];
//        }
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_GetItemID '" + _RowID +"'");
        try {
            while(set.next()){
                itemID = UUID.fromString(set.getString("itemID"));
            }
        } catch (Exception e) {
        }
        return itemID;
    }
    public static List<String> return_data = new ArrayList<String>();
    public static class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }
    public static void GetTable() {
        ResultSet _countTable = connectionString.ConnectionString("EXEC SP_Android_Select_Table '" + TableName + "', 'COUNT'");
        try {
            while (_countTable.next()) {
                TableName = _countTable.getString("TABLEDESC");
                break;
            }
        } catch (Exception e) {

        }
    }
    public static void showDialog(Context mContext, String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.setIcon(R.mipmap.ic_warning);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog2 = builder.show();
        dialog2.show();
    }

    public static void conSettings_Dialog(final Context mContext, final String action) {
        final DatabaseHelper db = new DatabaseHelper(mContext);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.activity_connection);
        final Button clear = (Button) dialog.findViewById(R.id.btn_clear);
        final ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        final Button test = (Button) dialog.findViewById(R.id.btn_test);
        final Button save = (Button) dialog.findViewById(R.id.btn_save);
        final Button save_con = new Button(mContext);
        final EditText u_name = (EditText) dialog.findViewById(R.id.txt_username);
        final EditText p_word = (EditText) dialog.findViewById(R.id.txt_password);
        final EditText servername = (EditText) dialog.findViewById(R.id.txt_servername);
        final EditText db_name = (EditText) dialog.findViewById(R.id.txt_database);

        ContentValues  c_val = db.getData();
        String cs_u_name = c_val.getAsString("user_name");
        String cs_p_word = c_val.getAsString("p_word");
        String cs_db_name =c_val.getAsString("db_name");

        final String cs_server_name = c_val.getAsString("server_name");
        servername.setText(cs_server_name);
        db_name.setText(cs_db_name);
        u_name.setText(cs_u_name);
        p_word.setText(cs_p_word);

        save.setEnabled(false);
        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        //region servername OnTouchListener
        servername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        servername.setFocusable(true);
                        servername.requestFocus();
                        servername.setSelection(0, servername.getText().length());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        save.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;

            }
        });
        //endregion
        //region db_name OnTouchListener
        db_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        db_name.setFocusable(true);
                        db_name.requestFocus();
                        db_name.setSelection(0, db_name.getText().length());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        save.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //endregion
        //region p_word OnTouchListener
        p_word.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        p_word.setFocusable(true);
                        p_word.requestFocus();
                        p_word.setSelection(0, p_word.getText().length());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        save.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //endregion
        //region u_name OnTouchListener
        u_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        u_name.setFocusable(true);
                        u_name.requestFocus();
                        u_name.setSelection(0, u_name.getText().length());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        save.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;

            }
        });
        //endregion
        //region close.setOnClickListener
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        //endregion
        //region clear fields
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u_name.setText("");
                p_word.setText("");
                servername.setText("");
                db_name.setText("");
                servername.requestFocus();
            }
        });
        //endregion
        //test connection to database if valid
        //region Test Connection
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                ResultSet set = null;
                Log.i("Android", " MS SQL Connect Example.");
                try {
                    String driver = "net.sourceforge.jtds.jdbc.Driver";
                    Class.forName(driver).newInstance();
                    String connString = "jdbc:jtds:sqlserver://" + servername.getText().toString() + "/" + db_name.getText().toString() + ";encrypt=false;user=" + u_name.getText().toString() + ";password=" + p_word.getText().toString() + ";instance=SQLEXPRESS;";
                    Connection conn = null;
                    conn = DriverManager.getConnection(connString);
                    Log.w("Connection", "open");
                    Statement stmt = conn.createStatement();
                    set = stmt.executeQuery("SP_Android_Select_Companyinfo");
                    is_valid = true;
                    save.setEnabled(true);
                    StaticClass.showDialog(mContext, "Connection Settings", "Connection is valid");
                } catch (Exception e) {
                    is_valid = false;
                    save.setEnabled(false);
                    StaticClass.showDialog(mContext, "Connection Settings", "Connection is not valid");
                    Log.w("Error connection", "" + e.getMessage());
                }
            }
        });
        //endregion
        //region Save Settings for connection string
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CodeStatus = action.equals("splash_form") ? true : false;
                if (action.equals("login_form")) {
                    final Dialog dialog = new Dialog(mContext);
                    DialogCode = dialog;
                    dialog.setContentView(R.layout.activity_keyboard);
                    final EditText Code = (EditText) dialog.findViewById(R.id.editText);
                    final TextView Label = (TextView) dialog.findViewById(R.id.TextViewFor);
                    Label.setText("Enter Login Code");
                    Label.setVisibility(View.VISIBLE);
                    Label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    Code.setTextSize(100);
                    Code.setTransformationMethod(new HiddenPassTransformationMethod());
                    Code.setGravity(Gravity.CENTER);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        Code.getRevealOnFocusHint();
                    }
                    //region Code.setOnEditorActionListener
                    Code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_MASK_ACTION) {
                                ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_ManagerCode '" + Code.getText() + "'");
                                String ManagerName = null;
                                try {
                                    while (set.next()) {
                                        ManagerName = set.getString("EmployeeName");
                                    }
                                } catch (Exception e) {
                                    CodeStatus = false;
                                }
                                if (ManagerName != null) {
                                    CodeStatus = true;
                                    save_con.performClick();
                                    dialog.hide();
                                } else {
                                    CodeStatus = false;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Code Error");
                                    builder.setMessage("Login Code is Incorrect");
                                    builder.setIcon(R.mipmap.ic_warning);
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog2 = builder.show();
                                    TextView messageText = (TextView) dialog.findViewById(R.id.TextViewMessage);
                                    messageText.setGravity(Gravity.CENTER);
                                    dialog2.show();
                                }
                            }
                            return false;
                        }
                    });
                    //endregion

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                } else {
                    save_con.performClick();
                }
            }
        });
        //endregion
        //region save_data
        save_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_valid) {
                    is_valid = false;
                    if (CodeStatus == true) {
                        CodeStatus = false;
                        if (TextUtils.isEmpty(cs_server_name)){
                            db.insertData(servername.getText().toString(), db_name.getText().toString(),u_name.getText().toString(),p_word.getText().toString());
                        }
                        else{
                            db.updateData(servername.getText().toString(), db_name.getText().toString(),u_name.getText().toString(),p_word.getText().toString());
                        }
                        Toast.makeText(mContext, "Connection settings are saved. Application will restart", Toast.LENGTH_LONG);
                        dialog.hide();
                        Intent i = mContext.getPackageManager()
                                .getLaunchIntentForPackage(mContext.getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(i);
                    }
                } else {
                    is_valid = false;
                    StaticClass.showDialog(mContext, "Connection String", "Connection is not valid");
                }
            }
        });
        //endregion

        if (action.equals("splash_form")) {
            close.setVisibility(View.GONE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.9f;
        dialog.show();
    }
    public static boolean is_valid = false;
}
