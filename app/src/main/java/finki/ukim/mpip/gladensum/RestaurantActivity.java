package finki.ukim.mpip.gladensum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.ui.restaurant_account.RestaurantAccountViewModel;

public class RestaurantActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    private AppBarConfiguration appBarConfiguration;
    private ArrayList<MenuItem> menuItems;
    private RestaurantAccountViewModel restaurantAccountViewModel;
//    public Bitmap restaurantImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        restaurantAccountViewModel=new ViewModelProvider(this).get(RestaurantAccountViewModel.class);
//        StorageReference storage = FirebaseStorage.getInstance().getReference();
//        StorageReference ref
//                = storage
//                .child(
//                        "restaurant_images/"
//                                + FirebaseAuth.getInstance().getCurrentUser().getUid());
//        ref.getBytes(Integer.MAX_VALUE).addOnSuccessListener(byteArray -> {
//            restaurantImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        });

        //Sakam da ja cuvam slikata na restoranot vo ovoj activity za samo 1 da mora da se simnuva

        //Moze da se kreira baza za slikata + poencinja poveke ke se dobijat

    }


    @Override
    protected void onStart() {
        super.onStart();
        setSupportActionBar(findViewById(R.id.my_toolbar));
        initNavigation();
        }

    private void initNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view_restaurant);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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
