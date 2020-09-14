package finki.ukim.mpip.gladensum.ui.driver_fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.viewModels.DriverViewModel;

public class ShowTakenOrdersOnMapFragment extends Fragment {

    private DriverViewModel viewModel;

    private Button setOrderDeliveredButton;
    Order selected_order;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            zoomToCurrentLocation(googleMap, viewModel.getTakenOrders().getValue());
            viewModel.getTakenOrders().observe(getViewLifecycleOwner(), orders -> {
                googleMap.clear();
                for (Order o : orders) {
                    if(o.latitude==null)
                        continue;
                    Log.d("Order taken: ",o.id);
                    LatLng latLng = new LatLng(o.latitude, o.longitude);
                    Marker marker=googleMap.addMarker(new MarkerOptions().position(latLng).title(o.address));
                    marker.setTag(o);
                }
                googleMap.setOnMarkerClickListener(marker -> {
                    marker.showInfoWindow();
                    setOrderDeliveredButton.setEnabled(true);
                    selected_order=(Order)marker.getTag();
                    return true;
                });


            });
        }
    };

    private void zoomToCurrentLocation(GoogleMap map, List<Order> orders) {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.getLastLocation().
                    addOnSuccessListener(loc -> {
                        if (loc != null)
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13));
                        else if (orders.size() > 0) {
                            Order o = orders.get(0);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(o.latitude, o.longitude), 13));
                        }
                    });
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this.getActivity()).get(DriverViewModel.class);
        View root= inflater.inflate(R.layout.fragment_show_taken_orders_on_map, container, false);

        setOrderDeliveredButton=root.findViewById(R.id.set_order_delivered_btn);
        if(selected_order==null)
            setOrderDeliveredButton.setEnabled(false);
        setOrderDeliveredButton.setOnClickListener(v -> {
            viewModel.removeTakenOrder(selected_order);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}