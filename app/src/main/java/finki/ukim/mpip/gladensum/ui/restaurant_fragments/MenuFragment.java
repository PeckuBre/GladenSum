package finki.ukim.mpip.gladensum.ui.restaurant_fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.ui.adapters.ItemCategoryAdapter;
import finki.ukim.mpip.gladensum.viewModels.RestaurantViewModel;

public class MenuFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;

    public FirebaseUser user;
    public FirebaseFirestore db;


    private Activity a;
    private View v;
    private RestaurantViewModel viewModel;
    private RecyclerView recyclerView;
    private HashMap<String, Category> categories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = getActivity();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        viewModel = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
        v = root;
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::populateRecyclerView);

        return root;
    }

    public void populateRecyclerView(HashMap<String, Category> categories) {
        this.categories = viewModel.getCategories().getValue();
        recyclerView = v.findViewById(R.id.show_categories);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemCategoryAdapter(new ArrayList<>(categories.values()), this, true));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button add = view.findViewById(R.id.add_item_to_menu);
        add.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_restaurant_menu_to_navigation_add_item_to_menu));
    }


}