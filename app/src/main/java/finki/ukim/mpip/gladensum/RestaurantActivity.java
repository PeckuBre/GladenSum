package finki.ukim.mpip.gladensum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import finki.ukim.mpip.gladensum.viewModels.RestaurantViewModel;

public class RestaurantActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    private AppBarConfiguration appBarConfiguration;
    private RestaurantViewModel restaurantViewModel;
//    public Bitmap restaurantImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
//        StorageReference storage = FirebaseStorage.getInstance().getReference();
//        StorageReference ref
//                = storage
//                .child(
//                        "restaurant_images/"
//                                + FirebaseAuth.getInstance().getCurrentUser().getUid());
//        ref.getBytes(Integer.MAX_VALUE).addOnSuccessListener(byteArray -> {
//            restaurantImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        setSupportActionBar(findViewById(R.id.my_toolbar));
        initNavigation();
    }

    private void initNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view_restaurant);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_restaurant_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_restaurant_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
