package finki.ukim.mpip.gladensum.ui.user_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.Menu;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.classes.OrderItem;
import finki.ukim.mpip.gladensum.services.ReverseGeocoderService;
import finki.ukim.mpip.gladensum.ui.adapters.ShowOrderToUserAdapter;
import finki.ukim.mpip.gladensum.viewModels.UserViewModel;


public class ShowOrderUserFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private UserViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView totalCost;
    private String restaurant_id;
    private HashMap<String, MenuItem> itemsMap;
    private ReverseGeocoderService geocoderService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this.getActivity()).get(UserViewModel.class);
        geocoderService = new ReverseGeocoderService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_order, container, false);
        Button completeOrder = v.findViewById(R.id.complete_order_button);
        totalCost = v.findViewById(R.id.total_order_cost);
        totalCost.setText("Order is empty");
        recyclerView = v.findViewById(R.id.show_order_items);
        viewModel.getCurrentOrder().observe(getViewLifecycleOwner(), order -> {
            if (order == null) {
                return;
            }
            if (!order.restaurant_id.equals(restaurant_id)) {
                restaurant_id = order.restaurant_id;
                initMenuMap();
            }
            double newCost = 0.0;
            for (OrderItem oi : order.items) {
                MenuItem mi = itemsMap.get(oi.item_id);
                newCost += mi.price * oi.qty;
            }
            totalCost.setText(String.format("Total: %.2f MKD", newCost));
            initRecyclerView(order);
        });
        completeOrder = v.findViewById(R.id.complete_order_button);
        completeOrder.setOnClickListener(v1 -> {

            if (ContextCompat.checkSelfPermission(
                    getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                finishOrder(true);
            }
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
            Navigation.findNavController(v).navigateUp();
        });
        return v;
    }


    @SuppressLint("MissingPermission")
    private void finishOrder(boolean location_granted) {
        Order o = viewModel.getCurrentOrder().getValue();
        if (!location_granted) {
            finishOrderWithoutLocation(o);
            return;
        }
        afterLocationTurnedOnCallback(o);
    }


    private void finishOrderWithoutLocation(Order o) {
        if (o != null && o.items.size() > 0) {
            o.changeOrderTime();
            o.latitude = o.longitude = null;
            FirebaseFirestore.getInstance().collection("orders").add(o).addOnCompleteListener(task -> viewModel.deleteOrder());
        }
    }


    @SuppressLint("MissingPermission")
    public void afterLocationTurnedOnCallback(Order o) {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (o != null && o.items.size() > 0) {
                o.changeOrderTime();
                try {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    String address = geocoderService.handleActionDecode(ll, o);

                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }

                FirebaseFirestore.getInstance().collection("orders").add(o).addOnCompleteListener(task -> viewModel.deleteOrder());
            }
        }).addOnFailureListener(e -> {
            // TODO: STAVI NEKOJA PREDEFINIRANA ADRESA NA KORISNIKOT (Taa od firebase) ili prasaj go dali saka da naraca na negovata adresa/da si izbere kade saka
            e.printStackTrace();
            finishOrderWithoutLocation(o);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            finishOrder(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
        }
    }

    private void initMenuMap() {
        itemsMap = new HashMap<>();
        Menu menu = viewModel.getMenus().getValue().get(restaurant_id);
        if (menu != null)
            for (Category cat : menu.categories.values()) {
                for (MenuItem item : cat.items) {
                    itemsMap.put(item.id, item);
                }
            }
    }

    private void initRecyclerView(Order order) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShowOrderToUserAdapter(order, itemsMap, viewModel));
    }


}
