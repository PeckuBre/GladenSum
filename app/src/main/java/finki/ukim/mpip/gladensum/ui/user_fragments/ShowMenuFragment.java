package finki.ukim.mpip.gladensum.ui.user_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.Menu;
import finki.ukim.mpip.gladensum.classes.Restaurant;
import finki.ukim.mpip.gladensum.ui.adapters.ItemCategoryAdapter;
import finki.ukim.mpip.gladensum.viewModels.UserViewModel;


public class ShowMenuFragment extends Fragment {

    private String restaurantId;
    private String placesId;
    private Restaurant restaurant;
    private Menu menu;
    private UserViewModel viewModel;
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
        placesId=getArguments().getString("places_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_show_menu_to_user, container, false);
        rv = v.findViewById(R.id.show_categories1);
        showRestaurantName = v.findViewById(R.id.restaurant_name);
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.getRestaurantById(restaurantId).observe(getViewLifecycleOwner(), restaurant1 -> {
            restaurant = restaurant1;
            showRestaurantName.setText(restaurant1.name);
            viewModel.getMenu().observe(getViewLifecycleOwner(), this::initRecyclerView);
            viewModel.loadMenuForRestaurant(restaurant);
        });

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
