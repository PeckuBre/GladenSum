package finki.ukim.mpip.gladensum.ui.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.classes.OrderItem;
import finki.ukim.mpip.gladensum.ui.home.ShowMenuItemFragment;

public class ShowOrderToRestaurantAdapter extends RecyclerView.Adapter<ShowOrderToRestaurantAdapter.ViewHolder> {

    private List<OrderItem> dataset;
//    private LayoutInflater layoutInflater;
//    private Fragment parent;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, quantity, comment;
        public ConstraintLayout cl;

        public ViewHolder(ConstraintLayout ll) {
            super(ll);
            cl = ll;
            itemName = ll.findViewById(R.id.item_name);
            quantity = ll.findViewById(R.id.item_qty);
            comment = ll.findViewById(R.id.comment);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShowOrderToRestaurantAdapter(List<OrderItem> data) {
//        this.parent = parent;
        dataset=data;
//        layoutInflater = parent.getLayoutInflater();
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ShowOrderToRestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        ConstraintLayout cl = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_layout, parent, false);
        ShowOrderToRestaurantAdapter.ViewHolder vh = new ShowOrderToRestaurantAdapter.ViewHolder(cl);
        return vh;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        OrderItem oi = dataset.get(position);
        holder.comment.setText(oi.comment);
        holder.quantity.setText(Integer.valueOf(oi.qty).toString());
        holder.itemName.setText(oi.name);
//        holder.cl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle args = new Bundle();
//                args.putSerializable(ShowMenuItemFragment.ARG_ORDER_ITEM, oi);
//                args.putInt("position", position);
//                Navigation.findNavController(v).navigate(R.id.showMenuItemFragment, args);
//            }
//        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
