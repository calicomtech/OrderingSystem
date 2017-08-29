package com.nutstechnologies.orderingsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TableActivity extends Fragment {
    View _view;
    public List<String> item = new ArrayList<String>();
    String tag;
    Integer menuID;
    String menuName;
    String Status;
    public static String parent_table;
    public static String selectedTable;
    public int selectedTable_Tag;
    public static String table_status;
    public Bundle bundleinstance;
    Dialog merge_dialog;
    String merge_from = "";
    public static String _fromtable;
    public static CheckBox chkAll;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        StaticClass.TableName = null;

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(MainActivity.Title);
        _view = view;
        savedInstanceState = bundleinstance;
        DisplayTable(view);

        MainActivity.Prev_Module = "TableActivity";
        StaticClass.return_data = new ArrayList<String>();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem _Table = menu.findItem(R.id.nav_TOut);
        _Table.setVisible(false);
        MenuItem _menu = menu.findItem(R.id.nav_Tmenu);
        _menu.setVisible(false);
        MenuItem _back = menu.findItem(R.id.nav_return);
        _back.setVisible(false);
        MenuItem _Send = menu.findItem(R.id.nav_Send);
        _Send.setVisible(false);
        MenuItem _billing = menu.findItem(R.id.nav_Tbilling);
        _billing.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_table, container, false);
    }



    public void DisplayTable(View view) {
        try {
            ResultSet set = connectionString.ConnectionString("EXEC SP_AndroidSelectTable");
            GridLayout gridLayout = new GridLayout(getContext());
            gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            gridLayout.setColumnCount(6);
            int col_count = 0;
            int col_counter = 6;
            while (set.next()) {
                col_count += 1;
                //region Creating views for table
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
                final TextView description = new TextView((getContext()));
                final TextView TableName = new TextView(getContext());
                RelativeLayout.LayoutParams rlName = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rlName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                description.setLayoutParams(rlName);
                String tbl_name = set.getString("tableDesc");
                boolean isClose = SaveData.CheckIfClose(tbl_name);
                if (isClose){
                    int counter_size = 0;
                    ResultSet joiner_set = connectionString.ConnectionString("EXEC sp_android_selectTable_WhenParentIsClosed '" + tbl_name + "'");
                    while (joiner_set.next()) {
                        counter_size++;
                        description.setText(joiner_set.getString("tableDesc"));
                        description.setTextSize(35);
                        description.setPadding(0, 5, 0, 5);
                        TableName.setText(joiner_set.getString("tableDesc"));
                    }
                    if (counter_size == 0){
                        description.setText(set.getString("tableDesc"));
                        description.setTextSize(35);
                        description.setPadding(0, 5, 0, 5);
                        TableName.setText(set.getString("tableDesc"));
                    }
                } else {
                    description.setText(set.getString("tableDesc"));
                    description.setTextSize(35);
                    description.setPadding(0, 5, 0, 5);
                    TableName.setText(set.getString("tableDesc"));
                }

                final Button btnImage = new Button(getContext());
                btnImage.setPadding(0, 0, 0, 0);
                btnImage.setTop(5);
                btnImage.setLeft(0);
                btnImage.setRight(50);
                ResultSet set2 = connectionString.ConnectionString("EXEC SP_Android_SelectPaxPerTable '" + TableName.getText().toString() + "' ");
                String Pax = null;
                final TextView _pax = new TextView((getContext()));
                try {
                    while (set2.next()) {
                        Pax = set2.getString("Pax");
                    }
                } catch (Exception e) {

                }
                boolean isbilling = SaveData.CheckIfBilling(TableName.getText().toString());
                boolean isTransClose = SaveData.CheckIfClose(TableName.getText().toString());
                if (isbilling) {
                    btnImage.setBackgroundResource(R.color.darkorange);
                    description.setTag("InUse");
                    Pax = Pax == null ? "" : "Pax - " + Pax;
                } else if (!isTransClose) {
                    btnImage.setBackgroundResource(R.color.red);
                    description.setTag("InUse");
                    Pax = Pax == null ? "" : "Pax - " + Pax;
                } else {
                    btnImage.setBackgroundResource(R.color.yellowgreen);
                    description.setTag("Available");
                    Pax = "";
                }
                _pax.setText(Pax);
                btnImage.setText(TableName.getText() + "\n" + Pax);
                btnImage.setTextSize(25);
                LinearLayout.LayoutParams img_param = new LinearLayout.LayoutParams(190, 160);
                btnImage.setLayoutParams(img_param);
                btnImage.setTag("btn_menu" + set.getString("tableCount"));
                ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).bottomMargin = 20;
                if (col_count != col_counter){
                    ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).rightMargin = 16;
                } else {
                    col_counter += 6;
                }
                pictureRowHolder.addView(btnImage);
                pictureHolder.addView(pictureRowHolder);
//                albumDetailsLayout.addView(description);
                albumLayout.addView(albumDetailsLayout);
                albumLayout.addView(pictureHolder);
                gridLayout.addView(albumLayout);

                tag = "btn_menu" + set.getString("tableCount");
                item.add(tag);
                //endregion
                //region btnImage OnLongClickListener
                if (description.getTag().toString().contains("InUse")) {
                    btnImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            // TODO Auto-generated method stub
                            parent_table = (String) TableName.getText();
                            selectedTable = (String) TableName.getText();
                            try {
                                ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionCount_From_Table '" + selectedTable + "'");
                                while (set_x.next()) {
                                    selectedTable_Tag = set_x.getInt("TransNoCount");
                                    break;
                                }
                            } catch (Exception e) {
                                Log.v(e.getCause().toString(), e.getMessage());
                            }
                            showTableOptions(getContext(), selectedTable);
                            return true;
                        }
                    });
                }
                //endregion
                //region btn_image onclick listener
                btnImage.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                Toast myToast = null;
                                try {
                                    parent_table = (String) TableName.getText();
                                    int size = 0;
                                    Fragment fragment = new MyOrderActivity();
                                    String DescTag = (String) description.getTag();
                                    String Table = (String) TableName.getText();
                                    StaticClass.TableName = Table;
                                    String tbl_tag = btnImage.getTag().toString().replace("btn_menu", "");
                                    selectedTable = Table;
                                    ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionCount_From_Table '" + Table + "'");
                                    while (set_x.next()) {
                                        selectedTable_Tag = set_x.getInt("TransNoCount");
                                        break;
                                    }
                                    int counter = 0;
                                    if (selectedTable_Tag != 0) {
                                        boolean has_parent = false;
                                        ResultSet r_set = connectionString.ConnectionString("EXEC SP_Android_SelectJoinerTableByParentTag '" + selectedTable + "'");
                                        while (r_set.next()) {
                                            String tbl = r_set.getString("TABLEDESC");
                                            if (tbl == selectedTable){
                                                has_parent = true;
                                            }
                                            if(!has_parent && counter < 1) {
                                                Table = tbl;
                                                counter += 1;
                                            }
                                            size += 1;
                                        }
                                    }
                                    if (size > 1) {
                                        selectJoinerTable(getContext());
                                    } else {
                                        ResultSet tbl_set = connectionString.ConnectionString("EXEC SP_Android_Select_TBL_FromHDR2 '" + Table + "'");
                                        boolean curr_used = false;
                                        while (tbl_set.next()) {
                                            curr_used = tbl_set.getBoolean("isCurrentlyUsed");
                                            break;
                                        }
                                        if (!curr_used) {
                                            if (DescTag.contains("InUse")) {
                                                ResultSet setx = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionID_ForJoinTable '" + Table + "'");
                                                while (setx.next()) {
                                                    SaveData.Trans_HDRID = UUID.fromString(setx.getString("TransID"));
                                                    break;
                                                }
                                                setx.close();
                                                setx = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'in_used', '" + SaveData.Trans_HDRID + "'");
                                                MainActivity.Title = StaticClass.TableName + " > " + "My Order(s)";
                                                MainActivity.Order_Prev = "MenuCategory";
                                                String _person = (String) _pax.getText();
                                                SaveData.Table_Number = Table;
                                                SaveData.Pax_Number = Integer.parseInt(_person.replace("Pax - ", ""));
                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                ft.replace(R.id.content_main, fragment, "table summary");
                                                StaticClass.Current_Fragment = "My Order";
                                                ft.commit();
                                            } else {
                                                MainActivity.Title = StaticClass.TableName + " > " + "My Order(s)";
                                                StaticClass.Current_Fragment = "My Order";
                                                StaticClass.showNumPad(getContext(), fragment, Table, "", Table);
                                            }
                                        } else {
                                            Snackbar.make(getView(), Table + " is currently in used.", Snackbar.LENGTH_LONG).show();
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
            set.close();
            ScrollView scroll = new ScrollView(getContext());
            scroll.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            scroll.addView(gridLayout);
            ((ViewGroup.MarginLayoutParams) scroll.getLayoutParams()).leftMargin = 10;
            ((ViewGroup.MarginLayoutParams) scroll.getLayoutParams()).rightMargin = 10;
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.addView(scroll);
        } catch (Exception e) {
            Toast myToast = Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG);
            myToast.show();
            Log.w("Error connection", "" + e.getMessage());
        }
    }
    public static Button close;
    public static Button merge;
    public Dialog DOptions;
    public Dialog DGenerate;
    public Dialog Dtransfer;
    public UUID TransID;
    public void showTableOptions(final Context mContext, final String selectedTable) {
        final Dialog dialogOptions = new Dialog(mContext);
        dialogOptions.setContentView(R.layout.activity_table_option);
        close = (Button) dialogOptions.findViewById(R.id.Close_Table);
        merge = (Button) dialogOptions.findViewById(R.id.btnMerge);
        Button transfer = (Button) dialogOptions.findViewById(R.id.btnTransfer);
        Button PaxEdit = (Button) dialogOptions.findViewById(R.id.btnPaxEdit);
        Button join = (Button) dialogOptions.findViewById(R.id.btnJoin);
        close.setText("Close Table");
        close.setTextSize(20);
        merge.setText("Merge Table");
        merge.setTextSize(20);
        PaxEdit.setText("Edit Pax");
        PaxEdit.setTextSize(20);
        transfer.setText("Transfer Table");
        transfer.setTextSize(20);
        join.setText("Join Table");
        join.setTextSize(20);

        DOptions = dialogOptions;
        boolean isvalid = SaveData.Count_JoinerTable(parent_table);
        if (!isvalid){
            close.setVisibility(View.GONE);
        }
        final boolean ispaid = SaveData.CheckIfPaid(selectedTable);
        if (ispaid) {
            merge.setEnabled(false);
            PaxEdit.setEnabled(false);
            transfer.setEnabled(false);
            join.setEnabled(false);
        }

        //region Close OnClickListener
        close.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // region select currently open table from tbl_transHDR

                        String table = null;
                        try {
                            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_RemainingTable '" + parent_table + "'");
                            while (set.next()) {
                                table = set.getString("table_desc");
                            }
                        } catch (Exception ex) {
                            Log.v("Sql Exception", ex.getMessage());
                        }
                        //endregion
                        //Check if table have pending order(s)
                        boolean haveOrders = SaveData.CountDetail_ForCloseTable(table);
                        if (!haveOrders) {
                            StaticClass.showAccessCode(getContext(), "Table Activity");
                            if (StaticClass.CodeStatus == true) {
                                Joiner_CloseTable(getContext(), dialogOptions, table);
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Close Table");
                            builder.setMessage(table + " have orders. Pay or void the transaction first to proceed.");
                            builder.setIcon(R.mipmap.ic_warning);
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog2 = builder.show();
                            dialog2.show();
                        }
                    }
                })
        );
        //endregion
        //region Merge OnClickListener
        merge.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // region select currently open table from tbl_transHDR
                        String table = null;
                        try {
                            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_RemainingTable '" + parent_table + "'");
                            while (set.next()) {
                                table = set.getString("table_desc");
                                break;
                            }
                        } catch (Exception ex) {
                            Log.v("Sql Exception", ex.getMessage());
                        }
                        //endregion
                        //Check if table have pending order(s)
                        boolean haveOrders = SaveData.CountDetail_ForCloseTable(table);
                        if (haveOrders) {
                            if (StaticClass.CodeStatus == false) {
                                StaticClass.showAccessCode(getContext(), "Merge");
                            }
                            if (StaticClass.CodeStatus == true) {
                                Merge_TableOptions(getContext(), table);
                                StaticClass.CodeStatus = false;
                                dialogOptions.hide();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Merge Table");
                            builder.setMessage(table + " do not have any orders. Please add order(s) first to proceed.");
                            builder.setIcon(R.mipmap.ic_warning);
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog2 = builder.show();
                            dialog2.show();
                        }
                    }
                })
        );
        //endregion
        //region paxEdit OnClickListener
        PaxEdit.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        EditPax(getContext(), selectedTable);
                    }
                })
        );
        //endregion
        //region Transfer OnClickListener
        transfer.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionID_From_Table '" + selectedTable + "' ");
                        UUID TransID = null;
                        try {
                            while (set.next()) {
                                TransID = UUID.fromString(set.getString("TransID"));
                                break;
                            }
                        } catch (Exception e) {

                        }
                        showTable(mContext, null,selectedTable, TransID, _view, null);
                    }
                })
        );
        //endregion
        //region Join OnClickListener
        join.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        showJoinTable(mContext);
                    }
                })
        );
        //endregion
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = dialogOptions.getWindow().getAttributes();
        lp.width = 500;
        lp.dimAmount = 0.9f;
        dialogOptions.show();
    }
    public void GetTable() {
        ResultSet _countTable = connectionString.ConnectionString("EXEC SP_Android_Select_Table '" + _fromtable + "', 'COUNT'");
        try {
            while (_countTable.next()) {
                _fromtable = _countTable.getString("TABLEDESC");
                break;
            }
        } catch (Exception e) {

        }
    }
    public void GenerateTransferDetails(final Context mContext, final String FromTable, final String TransferToTable, UUID TransID , View v, final String filter) {
        DOptions.hide();
        if(filter == null){
            StaticClass.ItemsTobeMove.clear();
        }
        _fromtable = FromTable;
        GetTable();
        ResultSet _tableSet = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionID_From_Table '" + _fromtable + "'");
        try {
            while (_tableSet.next()) {
                TransID = UUID.fromString(_tableSet.getString("TransID"));
            }
        } catch (Exception e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT);
        }

        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_TransFerItem '" + TransID + "','" + _fromtable + "'");
        try {
            DGenerate = new Dialog(mContext);
            LinearLayout ll_main = new LinearLayout(mContext);
            ll_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ll_main.setOrientation(LinearLayout.VERTICAL);

            LinearLayout ll_header = new LinearLayout(mContext);
            ll_header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
            ll_header.setOrientation(LinearLayout.HORIZONTAL);
            ll_header.setBackgroundColor(Color.parseColor("#34495e"));

            LinearLayout ll_detail = new LinearLayout(mContext);
            ll_detail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ll_detail.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout ll_detail_all = new LinearLayout(mContext);
            ll_detail_all.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ll_detail_all.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams ll_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            ll_param.weight = 1;

            LinearLayout ll_from = new LinearLayout(mContext);
            ll_from.setOrientation(LinearLayout.VERTICAL);
            ll_from.setLayoutParams(ll_param);
            ll_from.setBackgroundColor(Color.WHITE);

            LinearLayout ll_from_d = new LinearLayout(mContext);
            ll_from_d.setOrientation(LinearLayout.VERTICAL);
            ll_from_d.setLayoutParams(ll_param);
            ll_from_d.setBackgroundColor(Color.WHITE);

            TextView txt_table_from = new TextView(mContext);
            txt_table_from.setText("From Table: " + _fromtable);
            txt_table_from.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60));
            txt_table_from.setGravity(Gravity.LEFT);
            txt_table_from.setTextSize(20);
            txt_table_from.setPadding(30, 0, 0, 0);
            txt_table_from.setTextColor(Color.parseColor("#34495e"));
            ll_from.addView(txt_table_from);

            final LinearLayout ll_to = new LinearLayout(mContext);
            ll_to.setOrientation(LinearLayout.VERTICAL);
            ll_to.setLayoutParams(ll_param);
            ll_to.setBackgroundColor(Color.parseColor("#F5F5F5"));

            final LinearLayout ll_to_d = new LinearLayout(mContext);
            ll_to_d.setOrientation(LinearLayout.VERTICAL);
            ll_to_d.setLayoutParams(ll_param);
            ll_to_d.setBackgroundColor(Color.parseColor("#F5F5F5"));

            TextView txt_table_to = new TextView(mContext);
            txt_table_to.setText("Destination Table: " + TransferToTable);
            final UUID finalTransID1 = TransID;
            txt_table_to.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    showTable(mContext, null,selectedTable, finalTransID1, _view, "ChangeTable");
                }
            }));
//            (final Context mContext, final Fragment fragment, final String Table, final UUID TransID, final View view){
            txt_table_to.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60));
            txt_table_to.setGravity(Gravity.LEFT);
            txt_table_to.setTextSize(20);
            txt_table_to.setPadding(30, 0, 0, 0);
            txt_table_to.setTextColor(Color.parseColor("#34495e"));
            ll_to.addView(txt_table_to);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            ScrollView scrollView1 = new ScrollView(mContext);
            scrollView1.setLayoutParams(lp);
            scrollView1.addView(ll_from_d);
            ScrollView scrollView2 = new ScrollView(mContext);
            scrollView2.setLayoutParams(lp);
            scrollView2.addView(ll_to_d);

            ll_from.addView(scrollView1);
            ll_to.addView(scrollView2);
            ll_detail.addView(ll_from);
            ll_detail.addView(ll_to);


            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_blank, null);
            int ctr = 0;
            final List<TextView> _listdec = new ArrayList<>();
            final List<Float> _listqty = new ArrayList<>();
            final List<UUID> _listID = new ArrayList<>();
            final List<LinearLayout> _Linear = new ArrayList<>();
            while (set.next()) {
                ctr++;
                LinearLayout insideLinear1 = new LinearLayout(mContext);
                final LinearLayout insideLinear2 = new LinearLayout(mContext);
                final LinearLayout insideLinearT = new LinearLayout(mContext);
                final TextView desc = new TextView(mContext);
                final TextView txtqty = new TextView(mContext);
                final TextView desc2 = new TextView(mContext);
                final TextView descT = new TextView(mContext);
                final UUID _RowID = UUID.fromString(set.getString("_RowID"));
                final UUID ItemID = UUID.fromString(set.getString("itemID"));
                final float qty = set.getFloat("quantity");
                final CheckBox chk = new CheckBox(mContext);

                chk.setText("All");
                chkAll = chk;
                chk.setOnClickListener((new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            chk.setChecked(chk.isChecked() == true ? false : true);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("");
                            builder.setMessage("Select all Item(s) ?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    chk.setChecked(chk.isChecked() == true ? false : true);
                                    if(chk.isChecked() == true){
                                        int ctr = 0;
//                                        StaticClass.ItemsTobeMove.clear();
//                                        _Linear.clear();
//                                        ll_to.removeAllViews();
                                        for (TextView tv : _listdec){
//                                            insideLinear2.removeAllViews();
//                                            LinearLayout ll =  _Linear.get(ctr);
//                                            TextView tt = (TextView) ll.getChildAt(0);
//                                            if(!tt.getText().toString().contains(tv.getText().toString())){
                                                try{
                                                    setTransfer(mContext, _Linear.get(ctr), ll_to, _listID.get(ctr), tv, _listqty.get(ctr), "all");
                                                    StaticClass.ItemsTobeMove.put(_listID.get(ctr),_listqty.get(ctr));
                                                }
                                                catch (Exception e){
                                                }
//                                            }
                                            ctr++;
                                        }
//                                        _listdec.clear();
                                    }
                                    else{

                                    }
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            builder.show();
                        }
                    }));
                desc.setText(set.getString("Description"));
                txtqty.setText(String.valueOf(qty));
                descT.setText(set.getString("Description"));
                desc.setTextSize(16);
                desc.setPadding(30, 0, 0, 0);
                txtqty.setTextSize(16);
                txtqty.setPadding(30, 0, 0, 0);
                desc.setLayoutParams(new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT));
                txtqty.setLayoutParams(new LinearLayout.LayoutParams(85, LinearLayout.LayoutParams.MATCH_PARENT));
                desc2.setText("");
                desc.setOnClickListener((new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if((desc.getPaintFlags() | ~Paint.STRIKE_THRU_TEXT_FLAG) == ~Paint.STRIKE_THRU_TEXT_FLAG){
                                if (qty > 1) {
                                    StaticClass.showQty(mContext, _fromtable, _RowID, ItemID, insideLinear2, desc, qty);
                                } else {
                                    setTransfer(mContext, insideLinear2, ll_to, _RowID, desc, qty, null);
                                }
                            }
                  }
                 }));
                ((ViewGroup.MarginLayoutParams) desc.getLayoutParams()).rightMargin = 120;
                ((ViewGroup.MarginLayoutParams) desc.getLayoutParams()).leftMargin = 10;
                ((ViewGroup.MarginLayoutParams) desc.getLayoutParams()).bottomMargin = 2;
                ((ViewGroup.MarginLayoutParams) desc.getLayoutParams()).topMargin = 5;
                if(ctr <= 1){
                    insideLinear1.addView(chk);
                    ll_from_d.addView(insideLinear1);
                    insideLinear1 = new LinearLayout(mContext);
                    insideLinear1.addView(txtqty);
                    insideLinear1.addView(desc);
                    ll_from_d.addView(insideLinear1);
                    ll_to_d.addView(insideLinear2);
                }
                else{
                    insideLinear1.addView(txtqty);
                    insideLinear1.addView(desc);
                    ll_from_d.addView(insideLinear1);
                    ll_to_d.addView(insideLinear2);
                }
                insideLinearT.addView(descT);
                _listdec.add(desc);
                _listqty.add(qty);
                _listID.add(_RowID);
                _Linear.add(insideLinearT);
            }
            Button btnAll = new Button(mContext);
            Button btnRemoveAll = new Button(mContext);
            Button btnTransfer = new Button(mContext);
            btnAll.setText("All");
            btnRemoveAll.setText("RemoveAll");
            btnTransfer.setText("Transfer");
            final UUID finalTransID = TransID;
            btnTransfer.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    UUID _newTransID = UUID.randomUUID();
                    if(StaticClass.ItemsTobeMove.entrySet().size() > 0 ){
                        for (Map.Entry<UUID, Float> Item : StaticClass.ItemsTobeMove.entrySet()) {
                            UUID ItemID = StaticClass.getItemID(Item.getKey());
                            Float Qty = Item.getValue();
                            SaveData.TransferItem(_fromtable, TransferToTable, finalTransID, _newTransID, Item.getKey(), ItemID, Qty);
                        }
                        if (joiner_from) {
                            joiner_from = false;
                            SaveData.Pax_Edit(prnt_tbl, total_pax);
                        }
                        int ft;
                        Fragment fragment = new TableActivity();
                        ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, fragment)
                                .commit();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Successfully Transfer");
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog2 = builder.show();
                        dialog2.show();
                        DGenerate.hide();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Please Select Item(s) to be Transfer");
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog2 = builder.show();
                        dialog2.show();
                    }
//                dialog.hide();
                }
            }));
            btnTransfer.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT));
//            ll_from.addView(btnAll);

            TextView txt_Title = new TextView(mContext);
            txt_Title.setText("TRANSFER TABLE");
            txt_Title.setLayoutParams(new LinearLayout.LayoutParams(940, LinearLayout.LayoutParams.MATCH_PARENT));
            txt_Title.setTextSize(40);
            txt_Title.setTextColor(Color.WHITE);
            ((ViewGroup.MarginLayoutParams) txt_Title.getLayoutParams()).leftMargin = 10;
            ((ViewGroup.MarginLayoutParams) txt_Title.getLayoutParams()).topMargin = 15;
//                            linear2.addView(btnRemoveAll);
            ll_header.addView(txt_Title);
            ll_header.addView(btnTransfer);
//            ll_header.addView(ll_detail_all);

            ll_main.addView(ll_header);
//            ll_main.addView(ll_detail_all);
            ll_main.addView(ll_detail);

            ((ViewGroup.MarginLayoutParams) ll_main.getLayoutParams()).bottomMargin = 20;
            DGenerate.setContentView(v);
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(ll_main);
            v = inflater.inflate(R.layout.activity_blank, viewGroup);
            final Dialog finalDialogOptions = DGenerate;
            ImageButton close = (ImageButton)DGenerate.findViewById(R.id.btnClose);
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
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT);
        }
        DGenerate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DGenerate.show();
    }
    public void showTable(final Context mContext, final Fragment fragment, final String Table, final UUID TransID, final View view, final String filter){
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.activity_showtable);
//        final Spinner sp1 = (Spinner) dialog.findViewById(R.id.Spinner1);
        final ArrayList<String> sp1Items = new ArrayList<String>();
        ResultSet _tables = connectionString.ConnectionString("EXEC SP_Android_GetAllTableExcept '" + Table + "'");
        try {
            while (_tables.next()) {
                sp1Items.add(_tables.getString("tableDesc"));
            }
        } catch (Exception e) {
        }

        final AutoCompleteTextView textView = (AutoCompleteTextView) dialog.findViewById(R.id.autoTextView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_dropdown_item_1line, sp1Items);

        textView.setAdapter(arrayAdapter);
        textView.setThreshold(0);
        arrayAdapter.getFilter().filter(null);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.showDropDown();
                return false;
            }
        });

        Button btnSelect = (Button) dialog.findViewById(R.id.BtnTableSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (sp1Items.contains(textView.getText().toString())) {
                    dialog.hide();
                if(filter != null){
                    DGenerate.hide();
//                    textView.setText();
                }
                    GenerateTransferDetails(mContext, Table, textView.getText().toString(), TransID, view,filter);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Value does not exist");
                    builder.setMessage("Value does not belong in the list");
                    builder.setIcon(R.mipmap.ic_warning);
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog2 = builder.show();
                    dialog2.show();
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
    public static void setTransfer(final Context mContext, final LinearLayout insideLinear2,final LinearLayout to, final UUID DetailID, final TextView desc, final Float qty, final String filter){
        desc.setPaintFlags(desc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if(!StaticClass.ItemsTobeMove.containsKey(DetailID)){

            final TextView _Item = new TextView(mContext);
            final TextView _Qty = new TextView(mContext);
            String descValue = (String) desc.getText();
            _Item.setText(descValue);
            _Item.setTextSize(15);
            _Qty.setText(String.valueOf(qty));
            _Qty.setTextSize(15);
            if(StaticClass.ItemsTobeMove.size() <= 0){
                _Qty.setPadding(30,45,5,10);
                _Item.setPadding(30,45, 5, 10);
            }
            else{
                _Qty.setPadding(30, 0, 5, 10);
                _Item.setPadding(30, 0, 5, 10);
            }
            if(filter == null){
                StaticClass.ItemsTobeMove.put(DetailID,qty);
            }
            _Item.setLayoutParams(new LinearLayout.LayoutParams(320, LinearLayout.LayoutParams.MATCH_PARENT));
            _Qty.setLayoutParams(new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.MATCH_PARENT));
            _Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    insideLinear2.removeAllViews();
                    if(StaticClass.ItemsTobeMove.size() == 0 ) {
                        chkAll.setChecked(false);
                    }
                    desc.setPaintFlags(desc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    StaticClass.ItemsTobeMove.remove(DetailID);
                }
            });
            if (filter == "all") {
                    insideLinear2.removeAllViews();
                    insideLinear2.addView(_Qty);
                    insideLinear2.addView(_Item);
                    to.addView(insideLinear2);
            } else {
                insideLinear2.addView(_Qty);
                insideLinear2.addView(_Item);
            }

        }
    }
    public static void CloseTable(Context mContext, Dialog dialog, String selectedTable) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Table_Close '" + selectedTable + "'");
        dialog.hide();
        StaticClass.CodeStatus = false;
        Toast T = Toast.makeText(mContext, selectedTable + " Successfully Closed", Toast.LENGTH_LONG);
        T.show();
        StaticClass.DialogCode.hide();
        Fragment fragment = new TableActivity();
        int ft;
        ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment, "table summary")
                .commit();
    }
    public void EditPax(final Context mContext, String Table) {
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Pax.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_MASK_ACTION) {
                    if (!TextUtils.isEmpty(Pax.getText())) {
                        Integer PaxNo = Integer.parseInt(String.valueOf(Pax.getText()));
                        if (!PaxNo.equals(0)){
                            dialog.hide();
                            int ft;
                            Fragment fragment = new TableActivity();
                            ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_main, fragment, "table summary")
                                    .commit();
                            SaveData.Pax_Edit(selectedTable, PaxNo);
                            DOptions.hide();
                        } else{
                            StaticClass.showDialog(mContext, "Pax", "Pax cannot be equal to zero");
                        }
                    } else {
                        StaticClass.showDialog(mContext, "Pax", "Pax cannot be empty");
                    }
                }
                return false;
            }
        });
    }
    public void showJoinTable(final Context mContext) {
        String Table = selectedTable;
//        ResultSet r_set = connectionString.ConnectionString("EXEC SP_Android_SelectJoinerTableByParentTag '" + selectedTable + "'");
//        try {
//            while (r_set.next()) {
//                Table = r_set.getString("TABLEDESC");
//                break;
//            }
//        } catch (Exception ex) {
//            Log.v(ex.getCause().toString(), ex.getMessage());
//        }
        //region Create new Table
        String name = "";
        boolean isValid = false;
        List<String> joiner_list = Arrays.asList(getResources().getStringArray(R.array.joiner_array));
        for (int i = 0; i < joiner_list.size(); i++) {
            name = Table + joiner_list.get(i);
            isValid = SaveData.Get_TableValidity(name);
            if (isValid) {
                break;
            }
        }
        if (isValid) {
            String join_count = SaveData.Insert_JoinerTable(selectedTable_Tag, name);
            try {
                Fragment fragment = new MyOrderActivity();
                String Table1 = selectedTable;
                StaticClass.TableName = name;
                StaticClass.Current_Fragment = "My Order";
                MainActivity.Title = StaticClass.TableName + " > " + "My Order(s)";
                showNumPad(mContext, fragment, Table1, join_count, selectedTable);
                DOptions.hide();
            } catch (Exception e) {
                Log.v(e.getCause().toString(), e.getMessage());
            }
        } else {
//            showDialog(mContext, "Invalid Table Name", "Table name already exist");
            StaticClass.showDialog(mContext, "Invalid Table Name", "Table name already exist");
        }
        //endregion
    }
    public static int PaxNo;
    public int total_pax;
    public boolean joiner_from = false;
    public static String OrderStatus;
    public static String TableName;
    public static String prnt_tbl;
    boolean has_error = false;

    public void showNumPad(final Context mContext, final Fragment fragment, final String Table, final String joinTable_id, final String parent_Table) {
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
//        final UUID old_id = SaveData.Trans_HDRID;
        Pax.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    PaxNo = Integer.parseInt(String.valueOf(Pax.getText()));
                    final int tbl_pax = SaveData.Getpax(parent_Table);
                    if (PaxNo == 0) {
                        error_pax(mContext, "Pax cannot be equal to zero(0).");
                    } else {
                        total_pax = tbl_pax - PaxNo;
                        prnt_tbl = parent_Table;
                        if (!has_error){
                            SaveData.TableSave(StaticClass.TableName, PaxNo, joinTable_id, parent_Table);
                        }
                        MainActivity.Order_Prev = "MenuCategory";
                        if (!TextUtils.isEmpty(joinTable_id)) {
                            // Use the Builder class for convenient dialog construction
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Join Table");
                            builder.setMessage("Do you want to transfer order(s) from " + parent_Table + " to " + StaticClass.TableName + "?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    if (PaxNo >= tbl_pax) {
                                        has_error = true;
                                        error_pax(mContext, "Pax cannot be greater than or equals to " + parent_Table + "'s " + tbl_pax + " Pax.");
                                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + SaveData.Trans_HDRID + "'");
                                    } else {
                                        has_error = false;
                                        dialog.hide();
                                        joiner_from = true;
                                        GenerateTransferDetails(mContext, TableActivity.selectedTable, StaticClass.TableName, SaveData.Trans_HDRID, _view, null);
                                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + SaveData.Trans_HDRID + "'");
                                    }
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int id) {
                                    dialog.hide();
                                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'un_used', '" + SaveData.Trans_HDRID + "'");
                                }
                            });
                            builder.show();
                        }
                    }
                }
                return false;
            }
        });
    }
    private void error_pax(Context mContext, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Pax");
        builder.setMessage(msg);
        builder.setIcon(R.mipmap.ic_warning);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog2 = builder.show();
        dialog2.show();
    }

    public void selectJoinerTable(final Context mContext)  {
        merge_from = "joiner";
        merge_dialog = new Dialog(mContext);
        merge_dialog.setContentView(R.layout.activity_select_joiner_table);
        final TextView tbl_name = (TextView) merge_dialog.findViewById(R.id.txtJRT_tblName);
        tbl_name.setText(selectedTable);
        ScrollView scroll = (ScrollView) merge_dialog.findViewById(R.id.ll_JRT_Detail);
        GridLayout gridLayout = new GridLayout(getContext());
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setColumnCount(6);
        DOptions = merge_dialog;

        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectJoinerTableByParentTag_OnTableClick '" + selectedTable + "'");
        try {
            while (set.next()) {
                //region Creating views for table
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
                final TextView description = new TextView((getContext()));
                final TextView TableName = new TextView(getContext());
                RelativeLayout.LayoutParams rlName = new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rlName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                description.setLayoutParams(rlName);
                description.setText("");
                description.setTextSize(35);
                description.setPadding(0, 5, 0, 5);
                TableName.setText(set.getString("join_tbl_name"));
                final Button btnImage = new Button(getContext());
                btnImage.setPadding(0, 0, 0, 0);
                btnImage.setTop(5);
                btnImage.setLeft(0);
                btnImage.setRight(50);
                ResultSet set2 = connectionString.ConnectionString("EXEC SP_Android_Select_Pax_PerTable '" + TableName.getText().toString() + "' ");
                String Pax = null;
                final TextView _pax = new TextView((getContext()));
                try {
                    while (set2.next()) {
                        Pax = set2.getString("Pax");
                    }
                } catch (Exception e) {

                }
                boolean isbilling = SaveData.CheckIfBilling(TableName.getText().toString());
                boolean isTransClose = SaveData.CheckIfClose(TableName.getText().toString());
                if (isbilling) {
                    btnImage.setBackgroundResource(R.color.darkorange);
                    description.setTag("InUse");
                    Pax = Pax == null ? "" : "Pax - " + Pax;
                } else if (!isTransClose) {
                    btnImage.setBackgroundResource(R.color.red);
                    description.setTag("InUse");
                    Pax = Pax == null ? "" : "Pax - " + Pax;
                } else {
                    btnImage.setBackgroundResource(R.color.yellowgreen);
                    description.setTag("Available");
                    Pax = "";
                }



                _pax.setText(Pax);
                btnImage.setText(set.getString("join_tbl_name") + "\n" + Pax);
                btnImage.setTextSize(20);
                LinearLayout.LayoutParams img_param = new LinearLayout.LayoutParams(180, 160);
                btnImage.setLayoutParams(img_param);
                btnImage.setTag(set.getString("TransNoCount"));
                //endregion
                //region btnImage OnClickListener
                btnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Fragment fragment = new MyOrderActivity();
                        String DescTag = (String) description.getTag();
                        String Table = (String) TableName.getText();
                        StaticClass.TableName = Table;
                        String tbl_tag = btnImage.getTag().toString();
                        selectedTable = Table;
                        try {
                            ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionCount_From_Table '" + Table + "'");
                            while (set_x.next()) {
                                selectedTable_Tag = set_x.getInt("TransNoCount");
                                break;
                            }
                            ResultSet tbl_set = connectionString.ConnectionString("EXEC SP_Android_CheckIf_tableInUse '" + Table + "'");
                            boolean curr_used = false;
                            while (tbl_set.next()) {
                                curr_used = tbl_set.getBoolean("isCurrentlyUsed");
                                break;
                            }
                            if (!curr_used) {
                                if (DescTag.contains("InUse")) {
                                    DOptions.hide();
                                    ResultSet setx = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionID_ForJoinTable '" + Table + "'");
                                    while (setx.next()) {
                                        SaveData.Trans_HDRID = UUID.fromString(setx.getString("TransID"));
                                        break;
                                    }
                                    setx.close();
                                    setx = connectionString.ConnectionString("EXEC SP_Android_Update_Hdr_InUsed 'in_used', '" + SaveData.Trans_HDRID + "'");
                                    MainActivity.Title = StaticClass.TableName + " > " + "My Order(s)";
                                    MainActivity.Order_Prev = "MenuCategory";
                                    String _person = (String) _pax.getText();
                                    SaveData.Table_Number = Table;
                                    SaveData.Pax_Number = Integer.parseInt(_person.replace("Pax - ", ""));
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_main, fragment, "table summary");
                                    StaticClass.Current_Fragment = "My Order";
                                    ft.commit();
                                } else {
                                    MainActivity.Title = StaticClass.TableName + " > " + "My Order(s)";
                                    StaticClass.Current_Fragment = "My Order";
                                    StaticClass.showNumPad(getContext(), fragment, Table, tbl_tag, selectedTable);
                                    DOptions.hide();
                                }
                            } else {
                                Snackbar.make(getView(), Table + " is currently in used.", Snackbar.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.v(e.getCause().toString(), e.getMessage());
                        }
                    }
                });
                //endregion
                //region btnImage OnLongClickListener
                if (description.getTag().toString().contains("InUse")) {
                    btnImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            // TODO Auto-generated method stub
                            selectedTable = (String) TableName.getText();
                            try {
                                ResultSet set_x = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionCount_From_Table '" + selectedTable + "'");
                                while (set_x.next()) {
                                    selectedTable_Tag = set_x.getInt("TransNoCount");
                                    break;
                                }
                            } catch (Exception e) {
                                Log.v(e.getCause().toString(), e.getMessage());
                            }
                            showJoiner_TableOptions(getContext(), selectedTable);
                            return true;
                        }
                    });
                }
                //endregion
                ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).topMargin = 10;
                ((ViewGroup.MarginLayoutParams) btnImage.getLayoutParams()).rightMargin = 3;
                pictureRowHolder.addView(btnImage);
                pictureHolder.addView(pictureRowHolder);
                albumLayout.addView(albumDetailsLayout);
                albumLayout.addView(pictureHolder);
                gridLayout.addView(albumLayout);
            }
            scroll.addView(gridLayout);
        } catch (Exception e) {
            Log.v("SqlException", e.getMessage());
        }

        //region Close Button
        ImageButton btnClose = (ImageButton) merge_dialog.findViewById(R.id.btnJRTClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DOptions.hide();
            }
        });
        //endregion
        merge_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = merge_dialog.getWindow().getAttributes();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0.9f;
        merge_dialog.show();
    }
    public void showJoiner_TableOptions(final Context mContext, final String selectedTable) {
        final Dialog joiner_options = new Dialog(mContext);
        joiner_options.setContentView(R.layout.activity_joiner_option);
        close = (Button) joiner_options.findViewById(R.id.Close_Table1);
        merge = (Button) joiner_options.findViewById(R.id.btnMerge1);
        Button transfer = (Button) joiner_options.findViewById(R.id.btnTransfer1);
        Button PaxEdit = (Button) joiner_options.findViewById(R.id.btnPaxEdit1);
        Button join = (Button) joiner_options.findViewById(R.id.btnJoin1);
        close.setText("Close Table");
        close.setTextSize(20);
        merge.setText("Merge Table");
        merge.setTextSize(20);
        PaxEdit.setText("Edit Pax");
        PaxEdit.setTextSize(20);
        transfer.setText("Transfer Table");
        transfer.setTextSize(20);
        join.setText("Join Table");
        join.setTextSize(20);

        final boolean ispaid = SaveData.CheckIfPaid(selectedTable);
        if (ispaid) {
            merge.setEnabled(false);
            PaxEdit.setEnabled(false);
            transfer.setEnabled(false);
            join.setEnabled(false);
        }

        //region Close OnClickListener
        close.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //Check if table have pending order(s)
                        boolean haveOrders = SaveData.CountDetail_ForCloseTable(selectedTable);
                        if(!haveOrders) {
                            StaticClass.showAccessCode(getContext(), "Table Activity");
                            if (StaticClass.CodeStatus == true) {
                                Joiner_CloseTable(getContext(), joiner_options, selectedTable);
                                joiner_options.hide();
                            }
                        } else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Close Table");
                            builder.setMessage(selectedTable + " have orders. Pay or void the transaction first to proceed.");
                            builder.setIcon(R.mipmap.ic_warning);
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog2 = builder.show();
                            dialog2.show();
                        }
                    }
                })
        );
        //endregion
        //region Merge OnClickListener
        merge.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //Check if table have pending order(s)
                        boolean haveOrders = SaveData.CountDetail_ForCloseTable(selectedTable);
                        if (haveOrders) {
                            if (StaticClass.CodeStatus == false) {
                                StaticClass.showAccessCode(getContext(), "joiner merge");
                            }
                            if (StaticClass.CodeStatus == true) {
                                Merge_TableOptions(getContext(), selectedTable);
                                StaticClass.CodeStatus = false;
                                joiner_options.hide();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Merge Table");
                            builder.setMessage(selectedTable + " do not have any orders. Please add order(s) first to proceed.");
                            builder.setIcon(R.mipmap.ic_warning);
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog2 = builder.show();
                            dialog2.show();
                        }
                    }
                })
        );
        //endregion
        //region paxEdit OnClickListener
        PaxEdit.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        EditPax(getContext(), selectedTable);
                    }
                })
        );
        //endregion
        //region Transfer OnClickListener
        transfer.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionID_From_Table '" + selectedTable + "' ");
                        UUID TransID = null;
                        try {
                            while (set.next()) {
                                TransID = UUID.fromString(set.getString("TransID"));
                                break;
                            }
                        } catch (Exception e) {

                        }
                        showTable(mContext, null, selectedTable, TransID, _view, null);
                    }
                })
        );
        //endregion
        //region Join OnClickListener
        join.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        showJoinTable(mContext);
                    }
                })
        );

        joiner_options.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = joiner_options.getWindow().getAttributes();
        lp.width = 500;
        lp.dimAmount = 0.9f;
        joiner_options.show();
    }
    public void Joiner_CloseTable(Context mContext, Dialog dialog, String Table) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Joiner_Close '" + Table + "'");
        ResultSet setx = connectionString.ConnectionString("EXEC SP_Android_Joiner_CleanTable '" + parent_table + "'");
        dialog.hide();
        StaticClass.CodeStatus = false;
        Toast T = Toast.makeText(mContext, selectedTable + " Successfully Closed", Toast.LENGTH_LONG);
        T.show();
        StaticClass.DialogCode.hide();
        DOptions.hide();
        Fragment fragment = new TableActivity();
        int ft;
        ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment, "table summary")
                .commit();
//        selectJoinerTable(getContext());
    }
    public void Merge_CloseTable(Context mContext, Dialog dialog, String Table) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Joiner_Close '" + Table + "'");
        ResultSet setx = connectionString.ConnectionString("EXEC SP_Android_Joiner_CleanTable '" + parent_table + "'");
        dialog.hide();
        StaticClass.CodeStatus = false;
        DOptions.hide();
        Fragment fragment = new MyOrderActivity();
        MainActivity.Title = StaticClass.TableName + " > " + "My Order(s)";
        MainActivity.Order_Prev = "MenuCategory";
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment, "table summary");
        StaticClass.Current_Fragment = "My Order";
        ft.commit();
    }
    public void Merge_TableOptions(final Context mContext, final String selectedTable) {
        final Dialog dialogOptions = new Dialog(mContext);
        dialogOptions.setContentView(R.layout.activity_showtable);
        DOptions = dialogOptions;

        final ArrayList<String> sp1Items = new ArrayList<String>();
        ResultSet _tables = connectionString.ConnectionString("EXEC SP_Android_GetAllTableExcept_ForMerge '" + selectedTable + "'");
        try {
            while (_tables.next()) {
                sp1Items.add(_tables.getString("TABLEDESC"));
            }
        } catch (Exception e) {
        }

        final AutoCompleteTextView textView = (AutoCompleteTextView) dialogOptions.findViewById(R.id.autoTextView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_dropdown_item_1line, sp1Items);

        textView.setAdapter(arrayAdapter);
        textView.setThreshold(0);
        arrayAdapter.getFilter().filter(null);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.showDropDown();
                return false;
            }
        });
        //region Merge OnClickListener
        Button btnSelect = (Button) dialogOptions.findViewById(R.id.BtnTableSelect);
        btnSelect.setText("Merge");
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (sp1Items.contains(textView.getText().toString())) {
                    String destination_table = textView.getText().toString();
                    UUID source_ID = SaveData.GetTransID_From_Table(selectedTable);
                    UUID destination_ID = SaveData.GetTransID_From_Table(destination_table);
                    SaveData.Mergetable(source_ID, destination_ID);
                    int pax = SaveData.GetPax(destination_ID);
                    //REORDERING COUNT POS TO REMOVE DUPLICATE COUNT POS
                    int id = 1;
//                    ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_Detail_From_Merge '"+ destination_ID +"'");
//                    try{
//                        while(set.next()){
////                            UUID item_id = UUID.fromString(set.getString("_ItemID"));
////                            SaveData.Update_CountPOS_FromMerge(destination_ID, item_id, id);
////                            id += 1;
//                              final UUID _RowID = UUID.fromString(set.getString("_RowID"));
//                              final UUID TransID = UUID.fromString(set.getString("TRANSID"));
//                              final UUID SubsID = UUID.fromString(set.getString("SubsID"));
//                              final UUID ItemID = UUID.fromString(set.getString("ItemID"));
//
//
//                        }
//                    }catch (Exception ex){
//                        Log.v(ex.getCause().toString(), ex.getMessage());
//                    }
                    SaveData.Trans_HDRID = destination_ID;
                    SaveData.Pax_Number = pax;
                    SaveData.Table_Number = destination_table;
                    StaticClass.TableName = destination_table;
                    Merge_CloseTable(getContext(), dialogOptions, selectedTable);
                    SaveData.Delete_SourceTable(source_ID);
                    if (merge_from != "") {
                        merge_dialog.hide();
                    }
                    merge_from = "";
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Value does not exist");
                    builder.setMessage("Value does not belong in the list");
                    builder.setIcon(R.mipmap.ic_warning);
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog2 = builder.show();
                    dialog2.show();
                }
            }
        });
        //endregion
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = dialogOptions.getWindow().getAttributes();
        lp.dimAmount = 0.9f;
        dialogOptions.show();
    }

}
