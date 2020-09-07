package finki.ukim.mpip.gladensum.ui.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.classes.OrderItem;
import finki.ukim.mpip.gladensum.ui.home.HomeViewModel;
import finki.ukim.mpip.gladensum.ui.home.ShowMenuItemFragment;

public class ShowOrderToUserAdapter extends RecyclerView.Adapter<ShowOrderToUserAdapter.ViewHolder> {

    private Order order;
    private HashMap<String, MenuItem> menuItems;
    private HomeViewModel viewModel;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, quantity, comment, cost;
        public ConstraintLayout cl;
        public Button remove;

        public ViewHolder(ConstraintLayout cl) {
            super(cl);
            this.cl = cl;
            itemName = cl.findViewById(R.id.item_name);
            quantity = cl.findViewById(R.id.item_qty);
            comment = cl.findViewById(R.id.comment);
            cost = cl.findViewById(R.id.cost);
            remove=cl.findViewById(R.id.remove_item_btn);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShowOrderToUserAdapter(Order order, HashMap<String, MenuItem> menuItems, HomeViewModel viewModel) {
        this.order = order;
        this.menuItems = menuItems;
        this.viewModel=viewModel;
//        clicked = new boolean[mDataset.size()];
//        this.editItemRights = editItemRights;
    }


    // Create new views (invoked by the layout manager)
//    @Override
    public ShowOrderToUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        ConstraintLayout cl = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_layout, parent, false);
        return new ViewHolder(cl);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        OrderItem oi = order.items.get(position);
        MenuItem mi = menuItems.get(oi.item_id);
        holder.comment.setText(oi.comment);
        holder.quantity.setText(Integer.valueOf(oi.qty).toString());
        holder.cost.setText(String.format("%.2f MKD", oi.qty * mi.price));
        holder.itemName.setText(mi.name);
        holder.cl.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable(ShowMenuItemFragment.ARG_ORDER_ITEM, oi);
            args.putSerializable(ShowMenuItemFragment.ARG_ITEM, mi);
            args.putInt("position", position);
            Navigation.findNavController(v).navigate(R.id.showMenuItemFragment, args);
        });
        holder.remove.setOnClickListener(v->{
//            order.items.remove(position);
            viewModel.removeOrderItemAt(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,getItemCount());

        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return order.items.size();
    }


}
