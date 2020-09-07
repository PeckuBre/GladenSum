package finki.ukim.mpip.gladensum.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.Menu;
import finki.ukim.mpip.gladensum.classes.OrderItem;
import finki.ukim.mpip.gladensum.classes.Restaurant;
import finki.ukim.mpip.gladensum.ui.adapters.ItemCategoryAdapter;
import finki.ukim.mpip.gladensum.ui.small_fragments.Counter;


public class ShowMenuFragment extends Fragment {

    private String restaurantId;
    private Restaurant restaurant;
    private Menu menu;
    private HomeViewModel viewModel;
    private RecyclerView rv;
    private TextView showRestaurantName;
    private View v;

    public ShowMenuFragment() {
    }

    public static ShowMenuFragment newInstance() {
        return new ShowMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantId = getArguments().getString("restaurant_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_show_menu_to_user, container, false);
        rv = v.findViewById(R.id.show_categories1);
        showRestaurantName = v.findViewById(R.id.restaurant_name);
        viewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        viewModel.getRestaurantById(restaurantId).observe(getViewLifecycleOwner(), restaurant1 -> {
            restaurant = restaurant1;
            showRestaurantName.setText(restaurant1.name);
        });
        viewModel.getMenu().observe(getViewLifecycleOwner(), this::initRecyclerView);
        viewModel.readMenuForRestaurant(restaurantId);
        return v;
    }

    private void initRecyclerView(Menu menu1) {
        if (menu1 != null) {
            menu = menu1;
            ArrayList<Category> list = new ArrayList<>(menu.categories.values());
            ItemCategoryAdapter adapter = new ItemCategoryAdapter(list, this, false);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }


}
