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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import finki.ukim.mpip.gladensum.classes.Driver;

public class AddAccDetailsActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore db;
    private static int AUTOCOMPLETE_REQUEST_CODE_RESTAURANT = 1;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static int AUTOCOMPLETE_REQUEST_CODE_DRIVER = 3;
    private FusedLocationProviderClient locationProviderClient;
    private String restaurant_id, restaurant_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acc_details);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Button nextStep = findViewById(R.id.picker_next);
        db = FirebaseFirestore.getInstance();
        nextStep.setOnClickListener(nextOnClickListener);
//        Button logout = findViewById(R.id.logout);
//        logout.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
//            startActivity(intent);
//        });
    }

    private View.OnClickListener nextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioGroup group = findViewById(R.id.type_picker);
            if (group.getCheckedRadioButtonId() == R.id.radio_basic) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                Map<String, Object> app_user = new HashMap<>();
                app_user.put("type", getResources().getString(R.string.basic_user_name));
                db.collection("app_users").document(user.getUid()).set(app_user).addOnSuccessListener(a -> Log.d("tag???", "Document added with id " + user.getUid()))
                        .addOnFailureListener(e -> Log.w("drug tag???", "Error", e));
                startActivity(intent);
            } else if (group.getCheckedRadioButtonId() == R.id.radio_driver) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    try {
                        searchRestaurantForDriver(true);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }

            } else {
                //Restoranot bira koe google places
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    try {
                        searchRestaurantForRestaurant(true);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AUTOCOMPLETE_REQUEST_CODE_RESTAURANT);
                }

            }

        }
    };


    @SuppressLint("MissingPermission")
    private void searchRestaurantForRestaurant(boolean permission_granted) throws ExecutionException, InterruptedException {
        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle b = ai.metaData;
        Places.initialize(getApplicationContext(), b.getString("MAPS_API_KEY"));
        List<Place.Field> fields = Arrays.asList(Place.Field.values());
        Intent intent;
        Autocomplete.IntentBuilder ib = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT).setHint("Search for your restaurant");
        if (!permission_granted) {
            intent = ib.build(getApplicationContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_RESTAURANT);
        } else {
            locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            locationProviderClient.getLastLocation().addOnSuccessListener(loc -> {
                Intent intent1;
                if (loc != null) {
                    intent1 = ib
                            .setLocationBias(RectangularBounds.newInstance(
                                    new LatLng(loc.getLatitude() - 0.005, loc.getLongitude() - 0.005),
                                    new LatLng(loc.getLatitude() + 0.005, loc.getLongitude() + 0.005)))
                            .build(getApplicationContext());
                } else {
                    intent1 = ib.build(getApplicationContext());
                }
                startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE_RESTAURANT);
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Intent intent12 = ib.build(getApplicationContext());
                startActivityForResult(intent12, AUTOCOMPLETE_REQUEST_CODE_RESTAURANT);
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void searchRestaurantForDriver(boolean permission_granted) throws ExecutionException, InterruptedException {
        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle b = ai.metaData;
        Places.initialize(getApplicationContext(), b.getString("MAPS_API_KEY"));
        List<Place.Field> fields = Arrays.asList(Place.Field.values());
        Intent intent;
        Autocomplete.IntentBuilder ib = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT).setHint("Search for your restaurant");
        if (!permission_granted) {
            intent = ib.build(getApplicationContext());
            setContentView(R.layout.activity_driver_details);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_RESTAURANT);
        } else {
            locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            locationProviderClient.getLastLocation().addOnSuccessListener(loc -> {
                Intent intent1;
                if (loc != null) {
                    intent1 = ib
                            .setLocationBias(RectangularBounds.newInstance(
                                    new LatLng(loc.getLatitude() - 0.005, loc.getLongitude() - 0.005),
                                    new LatLng(loc.getLatitude() + 0.005, loc.getLongitude() + 0.005)))
                            .build(getApplicationContext());
                } else {
                    intent1 = ib.build(getApplicationContext());
                }
                setContentView(R.layout.activity_driver_details);
                startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE_DRIVER);
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Intent intent12 = ib.build(getApplicationContext());
                setContentView(R.layout.activity_driver_details);
                startActivityForResult(intent12, AUTOCOMPLETE_REQUEST_CODE_DRIVER);
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_RESTAURANT) {
            try {
                searchRestaurantForRestaurant(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DRIVER) {
            try {
                searchRestaurantForRestaurant(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener finishDriverOnClickListener = v -> {
        TextView driverNumber = findViewById(R.id.telefon_vozac_tb);
        if (driverNumber.getText().toString().matches("[0-9]+")) {
            Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
            Driver driver = new Driver("driver", user.getDisplayName(), user.getUid(), restaurant_id, driverNumber.getText().toString());
            db.collection("app_users").document(user.getUid()).set(driver.getKeyValuePairs()).addOnSuccessListener(aVoid -> {
                finishAffinity();
                startActivity(intent);
            });
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_RESTAURANT) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getTypes() != null && place.getTypes().contains(Place.Type.FOOD)) {
                    final Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                    Map<String, Object> app_user = new HashMap<>();
                    app_user.put("type", getResources().getString(R.string.restaurant_user_name));
                    app_user.put("address", place.getAddress());
                    app_user.put("name", place.getName());
                    app_user.put("phone_number", place.getPhoneNumber());
                    app_user.put("places_id", place.getId());
                    app_user.put("website", place.getWebsiteUri().toString());
                    db.collection("app_users").document(user.getUid()).set(app_user).addOnSuccessListener(aVoid -> {
                        finishAffinity();
                        startActivity(intent);
                    });
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("STATUS", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d("Canceled", "ffuuu");
            }
            return;
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DRIVER) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getTypes() != null && place.getTypes().contains(Place.Type.FOOD)) {
                    restaurant_id = place.getId();
                    setContentView(R.layout.activity_driver_details);
                    Button finishDriver = findViewById(R.id.driver_finish);
                    finishDriver.setOnClickListener(finishDriverOnClickListener);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
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
