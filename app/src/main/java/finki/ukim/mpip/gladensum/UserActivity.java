package finki.ukim.mpip.gladensum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;

import finki.ukim.mpip.gladensum.viewModels.UserViewModel;

public class UserActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    protected FirebaseUser user;
    protected FirebaseFirestore db;
    private AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;
    private UserViewModel viewModel;

    // TODO: LOCIRAJ GI SITE RESTORANI VO BLIZINA. ONIE KOI KE IMAAT MENI STAVI GI NAJGORE, OSTANATITE SORTIRAJ GI SPORED REJTING
    // lajkce


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        setLayout();
        initNavigation();
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }


    private void setLayout() {
        setContentView(R.layout.activity_user);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        initNavigation();
    }

    private void initNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view_driver);
        // Passing each bottom_nav_menu_driver ID as a set of Ids because each
        // bottom_nav_menu_driver should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favourites, R.id.navigation_driver_orders, R.id.navigation_driver_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_driver_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_driver_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void goToSignInMenu() {
        FirebaseAuth.getInstance().signOut();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.AnonymousBuilder().build()))
                    .build(), RC_SIGN_IN);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(this, AddAccDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

        }

        if (resultCode==LocationRequest.PRIORITY_HIGH_ACCURACY) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    break;
                case Activity.RESULT_CANCELED:

                    //TODO: The user was asked to change settings, but chose not to
                    break;
                default:
                    break;
            }
        }

    }

    public void goToOrderFragment() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_driver_fragment);
        if (navController.getCurrentDestination().getId() == R.id.showOrderFragment)
            return;
        navController.navigate(R.id.showOrderFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.user_appbar, menu);

        android.view.MenuItem cart = menu.findItem(R.id.action_view_cart);

        cart.setOnMenuItemClickListener(item -> {
            goToOrderFragment();
            return false;
        });

//        android.view.MenuItem searchItem = bottom_nav_menu_driver.findItem(R.id.app_bar_search);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_cart:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
