package finki.ukim.mpip.gladensum.ui.restaurant_fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.viewModels.RestaurantViewModel;

public class AddItemFragment extends Fragment {


    public static AddItemFragment newInstance() {
        return new AddItemFragment();
    }

    private EditText itemName;
    private EditText price;
    private AutoCompleteTextView categoryPick;
    private Button submit;
    private RestaurantViewModel viewModel;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this.getActivity()).get(RestaurantViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_item_to_menu, container, false);
        price = view.findViewById(R.id.tb_price);
        categoryPick = view.findViewById(R.id.tb_category);
        itemName = view.findViewById(R.id.tb_name);
        List<String> cats = new ArrayList<>((viewModel.getCategories().getValue().keySet()));
        categoryPick.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, cats));
        submit = view.findViewById(R.id.add_item_to_db);
        submit.setOnClickListener(v -> {
            if (itemName.getText() != null && categoryPick.getText().toString().compareTo("") > 0 && Float.parseFloat(price.getText().toString()) > 0) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> nov = new HashMap<>();
                nov.put("restaurant_id", viewModel.getRestaurant().places_id);
                nov.put("category", categoryPick.getText().toString());
                nov.put("name", itemName.getText().toString());
                nov.put("price", Float.parseFloat(price.getText().toString()));
                db.collection("items").add(nov);
                Navigation.findNavController(getView()).navigateUp();
            }
        });
        return view;
    }

}
