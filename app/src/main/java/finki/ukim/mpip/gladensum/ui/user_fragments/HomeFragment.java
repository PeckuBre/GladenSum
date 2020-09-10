package finki.ukim.mpip.gladensum.ui.user_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.ui.adapters.ShowRestaurantAdapter;
import finki.ukim.mpip.gladensum.viewModels.UserViewModel;

public class HomeFragment extends Fragment {

    public static final int REQUEST_PHONE_CALL = 124;
    public FirebaseUser user;
    public FirebaseFirestore db;
    public String phoneToCall;
    private View v;
    private UserViewModel userViewModel;
    private GridView gv;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userViewModel = new ViewModelProvider(this.getActivity()).get(UserViewModel.class);
        user = auth.getCurrentUser();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        v = root;
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        gv = v.findViewById(R.id.show_restaurants);
        userViewModel.getRestaurants().observe(getViewLifecycleOwner(),
                restaurants -> {
                    gv.setAdapter(new ShowRestaurantAdapter(new ArrayList<>(restaurants.values()), this));
                });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setGridViewContentByQuery(String query) {
        gv.setAdapter(new ShowRestaurantAdapter(userViewModel.getRestaurantsThatStartWith(query), this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.browse_restaurants_bar, menu);
        android.view.MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                setGridViewContentByQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (phoneToCall != null) {
                        callRestaurant();
                    }

                }
            }
        }
    }

    public void callRestaurant() {
        String uri = "tel:" + phoneToCall;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }
}