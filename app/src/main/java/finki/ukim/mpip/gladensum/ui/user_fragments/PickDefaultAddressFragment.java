package finki.ukim.mpip.gladensum.ui.user_fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.services.ReverseGeocoderService;

public class PickDefaultAddressFragment extends Fragment {

    private static int LOCATION_PERMISSION_REQUEST_CODE=1231;

    public LatLng clickedLocation;

    private GoogleMap googleMap;

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
            setGoogleMap(googleMap);

            googleMap.setOnMapClickListener(latLng -> {
                googleMap.clear();
                clickedLocation=latLng;
                googleMap.addMarker(new MarkerOptions().position(latLng).title("This is your new default address"));
            });

            if (ContextCompat.checkSelfPermission(
                    getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                zoomToLocation(googleMap);
            }
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
    };



    private void setGoogleMap(GoogleMap googleMap){
        this.googleMap=googleMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.pick_default_address_fragment, container, false);
        Button setDefaultAddressButton=v.findViewById(R.id.set_default_address_btn);
        setDefaultAddressButton.setOnClickListener(vv->{
            if(clickedLocation!=null){
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection("app_users").document(userId).update("default_lat",clickedLocation.latitude);
                FirebaseFirestore.getInstance().collection("app_users").document(userId).update("default_lng",clickedLocation.longitude);
                Navigation.findNavController(v).popBackStack();
            }
        });




        return v;
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

    @SuppressLint("MissingPermission")
    private void zoomToLocation(GoogleMap googleMap) {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                try {
                    LatLng ll=new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,15));
                    googleMap.clear();
                    clickedLocation=ll;
                    googleMap.addMarker(new MarkerOptions().position(ll).title("This is your new default address"));

                }catch (NullPointerException e){
                    //2 pati se povikuva ova???
                    e.printStackTrace();
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            zoomToLocation(googleMap);
        }
    }

}