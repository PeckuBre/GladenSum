package finki.ukim.mpip.gladensum.ui.restaurant_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.ui.adapters.ShowOrderToRestaurantAdapter;
import finki.ukim.mpip.gladensum.viewModels.RestaurantViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayOrderRestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayOrderRestaurantFragment extends Fragment {

    public static final String ARG_ORDER_ID= "param1";
    public static final String ARG_ORDER = "param2";

    private TextView status;
    private Button proceed;
    private Order order;
    private RestaurantViewModel viewModel;
    private RecyclerView rw;


    public DisplayOrderRestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param order Parameter 1.
     * @return A new instance of fragment DisplayOrderRestaurantFragment.
     */
    public static DisplayOrderRestaurantFragment newInstance(Order order) {
        DisplayOrderRestaurantFragment fragment = new DisplayOrderRestaurantFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER,order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order)getArguments().getSerializable(ARG_ORDER);
            viewModel= new ViewModelProvider(this).get(RestaurantViewModel.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_display_order_restaurant, container, false);
        status=v.findViewById(R.id.order_status);
        status.setText(order.decodeStatus());
        proceed=v.findViewById(R.id.proceed);
        proceed.setOnClickListener(v1 -> {
            status.setText(order.proceed());
            FirebaseFirestore.getInstance().collection("orders").document(order.id).set(order);
        });
        RecyclerView rw=v.findViewById(R.id.show_order_items);
        rw.setHasFixedSize(false);
        rw.setLayoutManager(new LinearLayoutManager(getContext()));
        rw.setAdapter(new ShowOrderToRestaurantAdapter(order.items));
        return v;
    }

}