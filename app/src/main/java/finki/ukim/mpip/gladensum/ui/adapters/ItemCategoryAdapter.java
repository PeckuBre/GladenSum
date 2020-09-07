package finki.ukim.mpip.gladensum.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.MenuItem;

public class ItemCategoryAdapter extends RecyclerView.Adapter<ItemCategoryAdapter.ViewHolder> {

    private ArrayList<Category> mDataset;
    private boolean[] clicked;
    private LayoutInflater layoutInflater;
    private Fragment parent;
    private boolean editItemRights;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout layout;
        public TextView textView;

//        public ListView list;
//        Category c;

        public ViewHolder(LinearLayout ll) {
            super(ll);
            layout=ll;
//            list=layout.findViewById(R.id.show_items_in_category);
            textView = layout.findViewById(R.id.category_name);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemCategoryAdapter(ArrayList<Category> myDataset,Fragment parent,boolean editItemRights) {
        this.parent=parent;
        mDataset = myDataset;
        clicked=new boolean[mDataset.size()];
        layoutInflater=parent.getLayoutInflater();
        this.editItemRights=editItemRights;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ItemCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        LinearLayout ll = (LinearLayout)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_category, parent, false);
        return new ViewHolder(ll);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).name);

        holder.textView.setOnClickListener(v -> {
            if(clicked[position]){
                LinearLayout kids=holder.layout.findViewById(R.id.show_items_in_category);
                kids.removeAllViews();
//                    kids=(LinearLayout)layoutInflater.inflate(R.layout.show_category_items,holder.layout);
                clicked[position]=false;
                return;
            }
            else {
                clicked[position]=true;
                int count=0;
                LinearLayout ll=holder.layout.findViewById(R.id.show_items_in_category);
                for(MenuItem a:mDataset.get(position).items){
                    layoutInflater.inflate(R.layout.show_item_restaurant_menu,ll,true);
                    View kid=ll.getChildAt(count++);
                    if(editItemRights){
                    kid.setOnClickListener(v1 -> {
                        Bundle args=new Bundle();
                        args.putSerializable("item",a);
                        Navigation.findNavController(v1).navigate(R.id.showRestaurantItemFragment,args);
                    });
                    }
                    else {
                        kid.setOnClickListener(v1 -> {
                            Bundle args=new Bundle();
                            args.putSerializable("item",a);
                            Navigation.findNavController(v1).navigate(R.id.showMenuItemFragment,args);
                        });
                    }
                    TextView t=kid.findViewById(R.id.item_price);
                    t.setText(a.price.toString());
                    t=kid.findViewById(R.id.item_name);
                    t.setText(a.name);
                }
            }
        });
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();

    }

}
