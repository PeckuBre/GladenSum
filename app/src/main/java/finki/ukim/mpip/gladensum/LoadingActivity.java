package finki.ukim.mpip.gladensum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;


public class LoadingActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 442;
    protected FirebaseUser user;
    protected FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        redirect();
    }

    private void goToSignInMenu() {
        FirebaseAuth.getInstance().signOut();
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.AnonymousBuilder().build()))
                .build(), RC_SIGN_IN);
    }

    private void redirect(){
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.d("USER",FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseFirestore.getInstance().collection("app_users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Log.d("success", "found succesfully");
                        if (doc.get("type").toString().equals("Restaurant")) {
                            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        } else if (doc.get("type").toString().equals("Driver")) {
                            Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        }
                        else {
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
                    goToSignInMenu();
                });

            } else {

                goToSignInMenu();
            }
        } catch (Exception e) {
            Log.d("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR",e.toString());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            redirect();
        }
    }

}
