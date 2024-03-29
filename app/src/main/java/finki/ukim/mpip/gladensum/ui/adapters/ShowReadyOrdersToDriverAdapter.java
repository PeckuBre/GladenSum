package finki.ukim.mpip.gladensum.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Driver;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.viewModels.DriverViewModel;

public class ShowReadyOrdersToDriverAdapter extends RecyclerView.Adapter<ShowReadyOrdersToDriverAdapter.ViewHolder> {

    public List<Order> orders;
    Driver driver;
    DriverViewModel viewModel;

    public ShowReadyOrdersToDriverAdapter(List<Order> orders,Driver d,DriverViewModel viewModel){
        this.orders=orders;
        driver=d;
        this.viewModel=viewModel;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView address, date;
        public ConstraintLayout cl;
        public Button take;

        public ViewHolder(ConstraintLayout cl) {
            super(cl);
            this.cl = cl;
            address = cl.findViewById(R.id.address);
            date = cl.findViewById(R.id.date);
            take=cl.findViewById(R.id.take_order_btn);
        }
    }

    @NonNull
    @Override
    public ShowReadyOrdersToDriverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder((ConstraintLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.prepared_order_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowReadyOrdersToDriverAdapter.ViewHolder holder, int position) {
        Order o = orders.get(position);
        holder.address.setText(o.address);
        holder.date.setText(o.time.toDate().toString());
        holder.take.setOnClickListener(v -> {
            viewModel.removePreparedOrderAt(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,getItemCount());
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
