package com.nutstechnologies.orderingsystem;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.text.format.Formatter;
import android.util.Log;

import java.sql.ResultSet;
import java.util.UUID;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Adrian on 6/8/2017.
 */

public class SaveData {
    public static UUID paymentID;
    public static UUID Trans_HDRID;
    public static UUID Waiter_ID;
    public static String Table_Number;
    public static Integer Pax_Number;
    public static String Device = "ANDROID/" + Build.BRAND + "-" +  Build.MODEL;
    public static String AndroidDevice = Build.BRAND + "-" +  Build.MODEL;
    public static String DeviceIP = "192.168.1.1";
//    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
//    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

    //region TODO: Save table to msSql Database
    public static void TableSave(String TableName, Integer PaxNo, String joinTable_id, String parent_TableName){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_InsertTable '" +  TableName +"'");
        Trans_HDRID = UUID.randomUUID();
        paymentID = UUID.randomUUID();
        Table_Number = TableName;
        Pax_Number = PaxNo;
        TransactionHeaderSave(TableName, PaxNo, Trans_HDRID, paymentID, joinTable_id, parent_TableName);
    }
    //endregion
    //region TODO: Save Transaction Header to msSql Database
    public static void TransactionHeaderSave(String TableName, Integer Pax, UUID uuid, UUID PaymentID, String joinTable_id, String parent_TableName) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_InsertTransHDR '" + uuid + "','" + PaymentID
                + "','" + TableName + "','" + Pax + "', '"  + Device + "', '"  + joinTable_id + "', '" + parent_TableName + "', '" + SaveData.Waiter_ID + "'");
    }
    //endregion

    public static void OrderItemSave(UUID HeaderID, UUID ItemID, Double Qty, Double Price, Double Amount, int count_pos, boolean set_menu){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_InsertTransDTL '"+ HeaderID +"','"+ItemID
                +"','"+ Qty +"','"+ Price + "', '"+ Amount + "', '"+ count_pos + "', '"+ set_menu + "', '"+ false + "'");
    }

    public static void OrderItemSaveForSetMenu(UUID HeaderID, UUID ItemID, UUID SubID, Double Qty, Double Price, Double Amount, int count_pos, boolean set_menu){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_InsertTransDTLForSetMenu '"+ HeaderID +"','"+ItemID
                +"','"+ SubID +"','"+ Qty +"','"+ Price + "', '"+ Amount + "', '"+ count_pos + "', '"+ set_menu + "'");
    }

    public static void OrderItemSaveFromSetMenu(UUID HeaderID, UUID ItemID, Float Qty, Double Price, Double Amount, String TableName){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_InsertTransDTL_FromSetMenu '"+ HeaderID +"','" + ItemID + "','"+ Qty +"','"+ Price +"', '"+ Amount +"','" + TableName + "'");
    }

    public static void Update_Order(UUID HeaderID, UUID ItemID, Double Qty, String CountPos){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Qty_item '"+ HeaderID +"','"+ ItemID
                +"','"+ Qty +"','"+ CountPos +"'");
    }

    public static void Void_Item(UUID trans_HDRID, UUID ItemID, String voidTag, UUID ReasonID){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_VoidItem '"+ trans_HDRID +"','"+ ItemID +"','"+ voidTag +"','"+ ReasonID +"'");
    }
    public static void Pax_Edit(String TableName, Integer PaxNo){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Pax_Edit '"+ TableName +"','"+ PaxNo +"'");
    }
    public static void Hold_Item(UUID trans_HDRID, UUID ItemID, String holdTag, Boolean _hold){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Hold_item '"+ trans_HDRID +"','"+ ItemID +"','"+ holdTag +"','"+ _hold +"'");
    }
    public static void OTH_Item(UUID trans_HDRID, UUID ItemID, String _tag, Boolean _oth){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_OTH_item '"+ trans_HDRID +"','"+ ItemID +"','"+ _tag +"','"+ _oth +"'");
    }
    public static void Send_Item(UUID ID){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Send '"+ ID +"'");
    }
    public static void Resend_Item(UUID trans_HDRID, UUID ItemID, String resendTag){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_ResendItem '"+ trans_HDRID +"','"+ ItemID +"','"+ resendTag +"'");
    }
    public static void Insert_DetailModifier(UUID trans_HDRID, UUID SUBID, UUID MODIF, Double Qty, UUID ITEMID){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Insert_TransDTL_Modif '"+ trans_HDRID +"','"+ SUBID +"','"+ MODIF +"','"+ Qty +"','"+ ITEMID +"'");
    }
    public static void Delete_Modifier(UUID ROWID, UUID MODIFIER, UUID SubsID){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Delete_Modifier '"+ ROWID +"','"+ MODIFIER +"', '" + SubsID + "'");
    }
    public static void Update_Qty_TransModifierDtl(UUID SUBID, double QTY){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_Qty_TransModifierDtl '"+ SUBID +"','"+ QTY +"'");
    }
//    public static void TransferItem(String FromTable, String ToTable, UUID TransID, UUID _newTransID, UUID itemID){
//        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_TransferItem '"+ FromTable +"','"+ ToTable +"','"+ TransID +"','"+ _newTransID + "','"+ itemID +"','"  + Device + "'");
//    }
    public static boolean GetLoginCredentials(String password){
        boolean isvalid = false;
        try{
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_GetLogin_Validity '"+ password +"'");
            while (set.next()){
                isvalid = set.getBoolean("IsValid");
            }
        } catch(Exception ex){
            Log.v("Sql Exception", ex.getMessage());
        }

        return isvalid;
    }
    public static void TransferItem(String FromTable, String ToTable, UUID TransID, UUID _newTransID, UUID _RowID, UUID itemID, Float Qty){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_TransferItem '"+ FromTable +"','"+ ToTable +"','"+ TransID +"','"+ _newTransID +"','" + _RowID + "','"+ itemID +"','"  + Qty + "','"  + Device + "'");
    }

    public static void TakeOut_Item(UUID trans_HDRID, UUID ItemID, String _tag, Boolean _takeout, double amount){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_TakeOut_item '"+ trans_HDRID +"','"+ ItemID +"','"+ _tag +"','"+ _takeout +"','"+ amount +"'");
    }
    public static boolean Get_TableValidity(String tbl_name){
        boolean isvalid = false;
        try{
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_GetTable_Validity '"+ tbl_name +"'");
            while (set.next()){
                isvalid = set.getBoolean("IsValid");
            }
        } catch(Exception ex){
            Log.v("Sql Exception", ex.getMessage());
        }

        return isvalid;
    }
    public static String Insert_JoinerTable(int table_id, String tbl_name) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_InsertJoinTable '" + table_id + "','" + tbl_name + "'");
        String x = "";
        try {
            while (set.next()) {
                return set.getString("join_count");
            }
        } catch (Exception e) {
            Log.v(e.getCause().toString(), e.getMessage());
        }
        return x;
    }

    public static void Update_tblJoinerStatus(String status, int id) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_tblJoinStatus '" + status + "', '" + id + "'");
    }

    public static boolean Count_JoinerTable(String parent_table) {
        boolean isvalid = false;
        try{
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_CountJoiner_OnTable '"+ parent_table +"'");
            while (set.next()){
                isvalid = set.getBoolean("IsValid");
            }
        } catch(Exception ex){
            Log.v("Sql Exception", ex.getMessage());
        }

        return isvalid;
    }

    public static UUID GetTransID_From_Table(String selectedTable) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectTransactionID_ForJoinTable '" + selectedTable + "'");
        UUID TransID = null;
        try {
            while (set.next()) {
                TransID = UUID.fromString(set.getString("TransID"));
                break;
            }
        } catch (Exception e) {
            Log.v(e.getCause().toString(), e.getMessage());
        }
        return TransID;
    }

    public static void Mergetable(UUID source_id, UUID destination_id) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Merge_Update_DestinationTable '"+ source_id +"','"+ destination_id +"'");
    }
    public static int GetPax(UUID destination_id){
        int x =0;
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectPax_ByID '"+ destination_id +"'");
        try{
            while(set.next()){
                x = set.getInt("Pax");
            }
        } catch (Exception ex){
            Log.v(ex.getCause().toString(), ex.getMessage());
        }
        return x;
    }

    public static void Delete_SourceTable(UUID source_id) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Merge_Delete_SourceTable '"+ source_id +"'");
    }

    public static void Update_CountPOS_FromMerge(UUID destination_id, UUID item_id, int id) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_CountPOS_FromMerge '"+ destination_id +"', '"+ item_id +"', '"+ id +"'");
    }

    public static boolean CountDetail_ForCloseTable(String table) {
        boolean haveOrders = false;
        try {
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_CountDetail_ForCloseTable '" + table + "'");
            while (set.next()) {
                haveOrders = set.getBoolean("haveOrders");
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return  haveOrders;
    }

    public static boolean IsSetMenuFlexible(UUID itemID) {
        boolean isFlexible = false;
        try {
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_CheckIf_SetIsFlexible '" + itemID + "'");
            while (set.next()) {
                isFlexible = set.getBoolean("isFlexible");
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return  isFlexible;
    }

    public static int Getpax(String parent_table) {
       int pax = 0;
        try {
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Get_TblPax '" + parent_table + "'");
            while (set.next()) {
                pax = set.getInt("Pax");
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return pax;
    }
    public static void SetLanguage(String Language){
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SetLanguage '" + AndroidDevice + "', '" + DeviceIP + "', '" + Language +"'");
    }

    public static boolean CheckIfClose(String tbl_name) {
        boolean isClosed = false;
        try {
            ResultSet set = connectionString.ConnectionString("EXEC sp_android_checkTable_ifClosed '" + tbl_name + "'");
            while (set.next()) {
                isClosed = set.getBoolean("isCloseTable");
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return  isClosed;
    }

    public static boolean CheckIfBilling(String tbl_name) {
        boolean isBilling = false;
        try {
            ResultSet set = connectionString.ConnectionString("EXEC sp_android_check_if_billing '" + tbl_name + "'");
            while (set.next()) {
                isBilling = set.getBoolean("isBillPrinted");
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return  isBilling;
    }

    public static boolean CheckIfPaid(String tbl_name) {
        boolean ispaid = false;
        try {
            ResultSet set = connectionString.ConnectionString("EXEC sp_android_check_if_ispaid '" + tbl_name + "'");
            while (set.next()) {
                ispaid = set.getBoolean("isPaid");
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return  ispaid;
    }

    public static void Update_SetMenuParentByChild(UUID trans_hdrid, UUID itemID, String holdTag) {
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Update_SetMenuParentByChild '"+ trans_hdrid +"','"+ itemID +"','"+ holdTag +"'");

    }

    public static UUID Get_WaiterID(String password) {
        UUID waiterID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        try {
            ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Get_WaiterID '" + password + "'");
            while (set.next()) {
                waiterID = UUID.fromString(set.getString("EmployeeID"));
                break;
            }
        } catch (Exception ex) {
            Log.v("Sql Exception", ex.getMessage());
        }
        return waiterID;
    }
}
