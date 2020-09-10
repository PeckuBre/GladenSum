package finki.ukim.mpip.gladensum.ui.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.ui.restaurant_fragments.DisplayOrderRestaurantFragment;

public class RestaurantOrdersAdapter extends RecyclerView.Adapter<RestaurantOrdersAdapter.ViewHolder> {

    List<Order> orders;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView address,date,status;
        public Button proceed;
        public LinearLayout ll;

        public ViewHolder(ConstraintLayout cl) {
            super(cl);
            this.ll=cl.findViewById(R.id.order_ll);
            this.address=ll.findViewById(R.id.order_address);
            date=ll.findViewById(R.id.order_date_time);
            status=ll.findViewById(R.id.order_status);
            proceed=cl.findViewById(R.id.proceed);
        }
    }

    public RestaurantOrdersAdapter(List<Order> orders) {
        super();
        this.orders=orders;
    }

    @NonNull
    @Override
    public RestaurantOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout ll = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order, parent, false);
        return new ViewHolder(ll);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantOrdersAdapter.ViewHolder holder, int position) {
        Order o=orders.get(position);
        if(o.address!=null)
        holder.address.setText(o.address);
        else
            holder.address.setText("NULL");
        holder.date.setText(orders.get(position).time.toDate().toString());
        holder.status.setText(orders.get(position).decodeStatus());
        holder.ll.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable(DisplayOrderRestaurantFragment.ARG_ORDER, orders.get(position));
//                args.putSerializable(ShowMenuItemFragment.ARG_ITEM, mi);
            args.putInt("position", position);
            Navigation.findNavController(v).navigate(R.id.displayOrderRestaurantFragment, args);
        });
        holder.proceed.setOnClickListener(v -> {
            holder.status.setText(o.proceed());
            FirebaseFirestore.getInstance().collection("orders").document(o.id).set(o);
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
