package com.nutstechnologies.orderingsystem;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nutstechnologies.orderingsystem.models.SingleItemModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Adrian on 6/1/2017.
 */

    public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {
        public ImageView Item;
        public TextView title;
        public String img;
        private ArrayList<SingleItemModel> itemsList;
        private ArrayList<SingleItemModel> categoryList;
        private Context mContext;
        public static int ID;
        public static String Category;
        public static List<String> item = new ArrayList<String>();
        public static String Tag_IDCount;
        public SectionListDataAdapter(Context context, ArrayList<SingleItemModel> itemsList) {
            this.itemsList = itemsList;
            this.mContext = context;

        }
        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(SingleItemRowHolder holder, int i) {
            SingleItemModel singleItem = itemsList.get(i);
            holder.tvTitle.setText(singleItem.getName());
            holder.tvTitle.setTag(singleItem.getcategory());
            holder.itemImage.setImageBitmap(StaticClass.bmp(singleItem.getimg()));
            holder.itemImage.setScaleType(ImageView.ScaleType.FIT_XY);

       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
        }
        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class SingleItemRowHolder extends RecyclerView.ViewHolder {
            protected TextView tvTitle;
            protected ImageView itemImage;
            public SingleItemRowHolder (final View view)  {
                super(view);
                this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), tvTitle.getText(), Toast.LENGTH_SHORT).show();
                        Fragment fragment = new Fragment();
                        ResultSet set = null;
                        String Title = (String)tvTitle.getText();
                        String Title_Tag = (String)tvTitle.getTag();
                        switch(Title_Tag){

                            case "CategoryActivity":
                                set = connectionString.ConnectionString("EXEC SP_Android_SelectCategoryByDesc " +  Title);
                                try {
                                    while(set.next()) {
                                        ID = Integer.parseInt(set.getString("CategoryCount"));
                                    }
                                    set.close();
                                } catch (Exception e) {}
                                CategoryActivity.From = "Menu";
                                CategoryActivity.menuName = StaticClass.TableName + " > Menu > " + Title;
                                break;
                            case "PromoActivity":
                                set = connectionString.ConnectionString("EXEC SP_Android_SelectPromoByDesc " + Title);
                                try {
                                    while(set.next()) {
                                        ID = Integer.parseInt(set.getString("Promo_ID_Int"));
                                    }
                                    set.close();
                                } catch (Exception e) {}
                                CategoryActivity.From = "Promo-CardView";
                                CategoryActivity.menuName = StaticClass.TableName + " > Promo > " + Title;
                                break;

                            case "ChefActivity":
                                set = connectionString.ConnectionString("EXEC SP_Android_Select_ChefRec 'GETHDR'," + Title );
                                try {
                                    while(set.next()) {
                                        ID = Integer.parseInt(set.getString("Chef_ID_Int"));
                                    }
                                    set.close();
                                } catch (Exception e) {}
                                CategoryActivity.From = "Chef-CardView";
                                CategoryActivity.menuName = StaticClass.TableName + " > Chef Recommendation > " + Title;
                                break;
                        }
                        fragment = new CategoryActivity();
                        CategoryActivity.menuID = ID;
                        int ft;
                        ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, fragment)
                                .commit();
                    }
                }
                );
            }
        }
    }
