package finki.ukim.mpip.gladensum.ui.driver_fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.ui.adapters.ShowReadyOrdersToDriverAdapter;
import finki.ukim.mpip.gladensum.ui.adapters.UserOrdersAdapter;
import finki.ukim.mpip.gladensum.viewModels.DriverViewModel;

public class DriverAvailableOrdersFragment extends Fragment {

    private DriverViewModel viewModel;

    public static DriverAvailableOrdersFragment newInstance() {
        return new DriverAvailableOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.driver_available_orders_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this.getActivity()).get(DriverViewModel.class);
        RecyclerView rw=getView().findViewById(R.id.orders_rw);
        viewModel.getReadyOrders().observe(getViewLifecycleOwner(), orders -> {
            rw.setHasFixedSize(false);
            rw.setLayoutManager(new LinearLayoutManager(getContext()));
            rw.setAdapter(new ShowReadyOrdersToDriverAdapter(viewModel));
        });
    }


}