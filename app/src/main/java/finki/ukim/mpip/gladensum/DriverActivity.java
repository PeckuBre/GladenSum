package finki.ukim.mpip.gladensum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import finki.ukim.mpip.gladensum.viewModels.DriverViewModel;

public class DriverActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private DriverViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setSupportActionBar(findViewById(R.id.my_toolbar));
        initNavigation();
        viewModel = new ViewModelProvider(this).get(DriverViewModel.class);
    }

    private void initNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view_driver);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_driver_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_driver_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


}
