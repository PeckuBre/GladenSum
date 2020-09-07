package finki.ukim.mpip.gladensum.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.ui.adapters.UserOrdersAdapter;
import finki.ukim.mpip.gladensum.ui.home.HomeViewModel;

public class OrdersFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView ordersRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =new ViewModelProvider(this.getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        ordersRecyclerView=root.findViewById(R.id.orders_rw);
        homeViewModel.getPreviousOrders().observe(getViewLifecycleOwner(), this::viewOrders);
        return root;
    }
    private void viewOrders(Set<Order> orderSet){
        ordersRecyclerView.setHasFixedSize(false);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRecyclerView.setAdapter(new UserOrdersAdapter(new ArrayList<>(orderSet)));
    }
}