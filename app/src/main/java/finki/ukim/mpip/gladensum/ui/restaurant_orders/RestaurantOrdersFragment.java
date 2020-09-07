package finki.ukim.mpip.gladensum.ui.restaurant_orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.ui.adapters.ItemCategoryAdapter;
import finki.ukim.mpip.gladensum.ui.adapters.RestaurantOrdersAdapter;

public class RestaurantOrdersFragment extends Fragment {

    private RestaurantOrdersViewModel viewModel;
    private RecyclerView ordersRecyclerView;

    public static RestaurantOrdersFragment newInstance() {
        return new RestaurantOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(getActivity()).get(RestaurantOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurant_orders, container, false);
        ordersRecyclerView=root.findViewById(R.id.orders_recycler_view);
        viewModel.getOrders().observe(getViewLifecycleOwner(), this::viewOrders);
        return root;
    }

    private void viewOrders(Set<Order> orderSet){
        ordersRecyclerView.setHasFixedSize(false);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRecyclerView.setAdapter(new RestaurantOrdersAdapter(new ArrayList<>(orderSet)));
    }


}
