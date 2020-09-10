package finki.ukim.mpip.gladensum.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;

public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.ViewHolder> {

    List<Order> orders;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView address,date,status;
        public Button cancel;
        public LinearLayout ll;

        public ViewHolder(ConstraintLayout cl) {
            super(cl);
            this.ll=cl.findViewById(R.id.order_ll);
            this.address=ll.findViewById(R.id.order_address);
            date=ll.findViewById(R.id.order_date_time);
            status=ll.findViewById(R.id.order_status);
            cancel=cl.findViewById(R.id.cancel_order_btn);
        }
    }

    public UserOrdersAdapter(List<Order> orders) {
        super();
        this.orders=orders;
    }

    @NonNull
    @Override
    public UserOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout ll = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_user ,parent, false);
        return new ViewHolder(ll);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrdersAdapter.ViewHolder holder, int position) {
        Order o=orders.get(position);
        if(o.address!=null)
            holder.address.setText(o.address);
        else
            holder.address.setText("NULL");
        holder.date.setText(orders.get(position).time.toDate().toString());
        holder.status.setText(orders.get(position).decodeStatus());
        if(o.status== Order.STATUS.CANCELED)
            holder.cancel.setEnabled(false);
        holder.cancel.setOnClickListener(v -> {
            holder.status.setText(o.cancel());
            FirebaseFirestore.getInstance().collection("orders").document(o.id).set(o);
            v.setEnabled(false);
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
