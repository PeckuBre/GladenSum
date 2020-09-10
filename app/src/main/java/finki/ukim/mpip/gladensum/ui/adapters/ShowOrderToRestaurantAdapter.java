package finki.ukim.mpip.gladensum.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.OrderItem;

public class ShowOrderToRestaurantAdapter extends RecyclerView.Adapter<ShowOrderToRestaurantAdapter.ViewHolder> {

    private List<OrderItem> dataset;

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

    public ShowOrderToRestaurantAdapter(List<OrderItem> data) {
        dataset=data;
    }


    @Override
    public ShowOrderToRestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
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
        OrderItem oi = dataset.get(position);
        holder.comment.setText(oi.comment);
        holder.quantity.setText(Integer.valueOf(oi.qty).toString());
        holder.itemName.setText(oi.name);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
