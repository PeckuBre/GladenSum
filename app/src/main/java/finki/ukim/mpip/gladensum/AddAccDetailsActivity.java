package finki.ukim.mpip.gladensum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddAccDetailsActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore db;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private PlacesClient placesClient;
    private FusedLocationProviderClient locationProviderClient;

    private View.OnClickListener nextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioGroup group = findViewById(R.id.type_picker);
            if (group.getCheckedRadioButtonId() == R.id.radio_basic) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                Map<String, Object> app_user = new HashMap<>();
                app_user.put("type", getResources().getString(R.string.basic_user_name));
                db.collection("app_users").document(user.getUid()).set(app_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void a) {
                        Log.d("tag???", "Document added with id " + user.getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("drug tag???", "Error", e);
                    }
                });
                startActivity(intent);

            } else if (group.getCheckedRadioButtonId() == R.id.radio_driver) {
                setContentView(R.layout.activity_driver_details);
                Button finishDriver = findViewById(R.id.finish);
                finishDriver.setOnClickListener(finishDriverOnClickListener);
            } else {

                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    try {
                        searchForTheRestaurant(true);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
//                else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    // In an educational UI, explain to the user why your app requires this
//                    // permission for a specific feature to behave as expected. In this UI,
//                    // include a "cancel" or "no thanks" button that allows the user to
//                    // continue using your app without granting the permission.
//                    showInContextUI(...);
//                }
                else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }

            }

        }
    };


    @SuppressLint("MissingPermission")
    private void searchForTheRestaurant(boolean permission_granted) throws ExecutionException, InterruptedException {
        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle b = ai.metaData;
//                 Initialize the SDK
        Places.initialize(getApplicationContext(), b.getString("MAPS_API_KEY"));
//                 Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getApplicationContext());
        List<Place.Field> fields = Arrays.asList(Place.Field.values());
//                 Start the autocomplete intent.
        Intent intent;
        Task<Location> task;
        Autocomplete.IntentBuilder ib = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT).setHint("Search for your restaurant");
        if (!permission_granted) {
            intent = ib.build(getApplicationContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//            setContentView(R.layout.activity_restaurant_details);
//            Button finishRestaurant = findViewById(R.id.finish);
//            finishRestaurant.setOnClickListener(finishRestaurantOnClickListener);

        } else {
            locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location loc) {
                    Intent intent;
                    if (loc != null) {
                        intent = ib
                                .setLocationBias(RectangularBounds.newInstance(
                                        new LatLng(loc.getLatitude() - 0.005, loc.getLongitude() - 0.005),
                                        new LatLng(loc.getLatitude() + 0.005, loc.getLongitude() + 0.005)))
                                .build(getApplicationContext());
                    } else {
                        intent = ib.build(getApplicationContext());
                    }
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//                    setContentView(R.layout.activity_restaurant_details);
//                    Button finishRestaurant = findViewById(R.id.finish);
//                    finishRestaurant.setOnClickListener(finishRestaurantOnClickListener);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Intent intent = ib.build(getApplicationContext());
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//                    setContentView(R.layout.activity_restaurant_details);
//                    Button finishRestaurant = findViewById(R.id.finish);
//                    finishRestaurant.setOnClickListener(finishRestaurantOnClickListener);
                }
            });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            try {
                searchForTheRestaurant(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener finishDriverOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText restaurantName = findViewById(R.id.ime_vozac_restoran_tb);
            EditText driverNumber = findViewById(R.id.telefon_vozac_tb);
            EditText driverAddress = findViewById(R.id.adresa_vozac_tb);

            Task<QuerySnapshot> rest = db.collection("app_users").whereEqualTo("name", restaurantName.getText().toString()).get();
            final Boolean[] found = {false};

            db.collection("app_users").whereEqualTo("name", restaurantName.getText().toString())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (driverAddress.getText() != null && driverNumber.getText().toString().matches("07./...-...")) {
                            final Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
                            Map<String, Object> app_user = new HashMap<>();
                            app_user.put("type", getResources().getString(R.string.driver_user_name));
                            app_user.put("address", driverAddress.getText().toString());
                            app_user.put("restaurant_name", restaurantName.getText().toString());
                            app_user.put("phone_number", driverNumber.getText().toString());
                            db.collection("app_users").document(user.getUid()).set(app_user).addOnSuccessListener(aVoid -> {
                                Log.d("oti nejke", "nezzz");
                                startActivity(intent);
                            });
                        }
                        break;
                    }
                } else
                    Log.d("nenajden", "u kurac");
            });

            Log.d("nan", found[0].toString());


        }

    };

//    private View.OnClickListener finishRestaurantOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            EditText restaurantName = findViewById(R.id.ime_restoran_tb);
//            EditText restaurantNumber = findViewById(R.id.telefon_restoran_tb);
//            EditText restaurantAddress = findViewById(R.id.adresa_restoran_tb);
//
//            if (restaurantAddress.getText() != null && restaurantName.getText() != null && restaurantNumber.getText().toString().matches("07./...-...")) {
//                final Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
//                Map<String, Object> app_user = new HashMap<>();
//                app_user.put("type", getResources().getString(R.string.restaurant_user_name));
//                app_user.put("address", restaurantAddress.getText().toString());
//                app_user.put("name", restaurantName.getText().toString());
//                app_user.put("phone_number", restaurantNumber.getText().toString());
//                db.collection("app_users").document(user.getUid()).set(app_user).addOnSuccessListener(aVoid -> {
//                    Log.d("creation", "GO STAIVVV");
//                    startActivity(intent);
//                });
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acc_details);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Button nextStep = findViewById(R.id.picker_next);
        db = FirebaseFirestore.getInstance();
        nextStep.setOnClickListener(nextOnClickListener);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK&&data!=null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getTypes()!=null&&place.getTypes().contains(Place.Type.FOOD)) {
                    final Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                    Map<String, Object> app_user = new HashMap<>();
                    app_user.put("type", getResources().getString(R.string.restaurant_user_name));
                    app_user.put("address", place.getAddress());
                    app_user.put("name", place.getName());
                    app_user.put("phone_number", place.getPhoneNumber());
                    app_user.put("places_id", place.getId());
                    app_user.put("website",place.getWebsiteUri().toString());
//                    System.out.println(place.toString());
                    db.collection("app_users").document(user.getUid()).set(app_user).addOnSuccessListener(aVoid -> {
                        finishAffinity();
                        startActivity(intent);
                    });
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TO DO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("STATUS", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d("Canceled", "ffuuu");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
