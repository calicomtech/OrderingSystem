package com.nutstechnologies.orderingsystem;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nutstechnologies.orderingsystem.models.SectionDataModel;
import com.nutstechnologies.orderingsystem.models.SingleItemModel;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Adrian on 6/1/2017.
 */

//public class HomeActivity extends AppCompatActivity {
    public class HomeActivity extends Fragment {
    private Toolbar toolbar;
    public String noimage = "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";

    ArrayList<SectionDataModel> allSampleData;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar)getView(). findViewById(R.id.toolbar);
        allSampleData = new ArrayList<SectionDataModel>();
        if (toolbar != null) {
//            this.getActivity().setSupportActionBar(toolbar);
//            toolbar.setTitle("G PlayStore");
        }
        getActivity().setTitle(StaticClass.TableName + " > Home");
        createData();
        RecyclerView my_recycler_view = (RecyclerView)getView().findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this.getContext(), allSampleData);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(adapter);
        MainActivity.Prev_Module = "HomeActivity";
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)context;
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_menu).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_mainPage).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_home, container, false);
    }
    SectionDataModel dm = new SectionDataModel();
    ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();
    public static ArrayList<String> singleImage = new ArrayList<String>();
    public void createData() {
        getCategory();
        getChef();
        getPromo();

    }
    public void getChef(){
        singleItem = new ArrayList<SingleItemModel>();
        dm = new SectionDataModel();
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_Select_ChefRec 'TOP 15', NULL");
        dm.setHeaderTitle("Chef Recommendation");
        try {
            int i = 0;
            while(set.next()) {
                String Desc = set.getString("ChefDesc");
                String image = null;
                if (set.getString("ItemImage") != null) {
                  image = set.getString("ItemImage");
                }
                else{
                    image = noimage;
                }
                singleItem.add(new SingleItemModel(Desc, "ChefActivity", image));
                i++;
            }
            set.close();
        } catch (Exception e) {

        }
        dm.setAllItemsInSection(singleItem);
        allSampleData.add(dm);

    }
    public void getPromo(){
        SectionListDataAdapter.Category = null;
        singleItem = new ArrayList<SingleItemModel>();
        dm = new SectionDataModel();
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectPromo 'TOP 15', NULL");
        dm.setHeaderTitle("Promo");
        try {
            int i = 0;
            while(set.next()) {
                String Desc = set.getString("PromoDesc");
                String image = null;
                if (set.getString("ItemImage") != null) {
                    image = set.getString("ItemImage");
                }
                else{
                    image = noimage;
                }
                singleItem.add(new SingleItemModel(Desc, "PromoActivity", image));
                i++;
            }
            set.close();
        } catch (Exception e) {

        }
        dm.setAllItemsInSection(singleItem);
        allSampleData.add(dm);
    }

    public void getCategory(){
        SectionListDataAdapter.Category = null;
        singleItem = new ArrayList<SingleItemModel>();
        dm = new SectionDataModel();
        ResultSet set = connectionString.ConnectionString("EXEC SP_Android_SelectCategory 'TOP 15', NULL");
        dm.setHeaderTitle("Menu");
        try {
            SectionListDataAdapter.item = new ArrayList<String>();
            int i = 0;
            while(set.next()) {
                final ImageButton img = new ImageButton(getContext());
                String Desc = set.getString("CategoryDescription");
                String image = null;
                if (set.getString("ItemImage") != null) {
                    image = set.getString("ItemImage");
                }
                else{
                image = noimage;
                }
                singleItem.add(new SingleItemModel(Desc, "CategoryActivity", image));
                SectionListDataAdapter.ID = (Integer.parseInt(set.getString("CategoryCount")));
                SectionListDataAdapter.Category = "CategoryActivity";
                i++;
            }
            set.close();
        } catch (Exception e) {

        }
        dm.setAllItemsInSection(singleItem);
        allSampleData.add(dm);
    }

}
