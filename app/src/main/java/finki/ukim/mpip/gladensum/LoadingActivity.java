package finki.ukim.mpip.gladensum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import finki.ukim.mpip.gladensum.classes.AppUser;
import finki.ukim.mpip.gladensum.classes.BasicUser;
import finki.ukim.mpip.gladensum.classes.Driver;
import finki.ukim.mpip.gladensum.classes.Restaurant;

public class LoadingActivity extends AppCompatActivity {
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    public static String places_key="AIzaSyDmxnLanryTJtnaEt1LsQm-_hDEOnXenn4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);
        user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.d("userID",user.getUid());
        db = FirebaseFirestore.getInstance();
//        Places.initialize(getApplicationContext(), places_key);
//        PlacesClient placesClient = Places.createClient(this);
        try {
            Log.d("userIDDDDDDDD",user.getUid());
            if (user != null) {
                db.collection("app_users").document(user.getUid()).get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Log.d("success", "found succesfully");
                        AppUser appUser ;
                        appUser = doc.toObject(AppUser.class);
                        if (doc.get("type").toString().equals("Restaurant")) {
                            appUser= doc.toObject(Restaurant.class);
                            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        } else if (doc.get("type").toString().equals("Driver")) {

                            appUser= doc.toObject(Driver.class);
                            Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        }
                        else {
                            appUser= doc.toObject(BasicUser.class);
                            Intent intent=new Intent(getApplicationContext(),UserActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        }
                    }
                    else {
                        Intent intent = new Intent(this, AddAccDetailsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finishAffinity();
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> {
                    Log.d("failed to find info", e.toString());
                    Intent intent=new Intent(getApplicationContext(),UserActivity.class);
                    finishAffinity();
                    startActivity(intent);
                });

            } else {

                Log.d("VO USER==NULL","da");
                Intent intent=new Intent(getApplicationContext(),UserActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.d("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR",e.toString());
            Intent intent=new Intent(getApplicationContext(),UserActivity.class);
            finishAffinity();
            startActivity(intent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }
}
