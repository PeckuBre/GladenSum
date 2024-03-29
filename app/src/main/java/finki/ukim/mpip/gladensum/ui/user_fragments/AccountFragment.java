package finki.ukim.mpip.gladensum.ui.user_fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import finki.ukim.mpip.gladensum.AddAccDetailsActivity;
import finki.ukim.mpip.gladensum.DriverActivity;
import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.RestaurantActivity;
import finki.ukim.mpip.gladensum.UserActivity;

public class AccountFragment extends Fragment {

    private static int RC_SIGN_IN = 123;
    private FirebaseUser user;

    //TODO: stavi primarna adresa. Koga korisnikot naracuva prasaj go dali da ja iskoristi taa


    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);
        final TextView textView = root.findViewById(R.id.text_account);
        Button b = root.findViewById(R.id.sign_out_button);

        b.setOnClickListener(v -> goToSignInMenu());


        Button pickDefaultAddress=root.findViewById(R.id.pick_default_address_btn);
        pickDefaultAddress.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_pick_default_address));

        return root;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                getActivity().setContentView(R.layout.loading_screen);
                user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                try {
                    Log.d("userIDDDDDDDD",user.getUid());
                    if (user != null) {
                        Log.d("VO USER!=NULL","da");
                        db.collection("app_users").document(user.getUid()).get().addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                Log.d("success", "found succesfully");
                                if (doc.get("type").toString().equals("Restaurant")) {
                                    Intent intent = new Intent(getContext(), RestaurantActivity.class);
                                    getActivity().finishAffinity();
                                    startActivity(intent);
                                } else if (doc.get("type").toString().equals("Driver")) {
                                    Intent intent = new Intent(getContext(), DriverActivity.class);
                                    getActivity().finishAffinity();
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent=new Intent(getContext(),UserActivity.class);
                                    getActivity().finishAffinity();
                                    startActivity(intent);
                                }
                            }
                            else {
                                Intent intent = new Intent(getContext(), AddAccDetailsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getActivity().finishAffinity();
                                startActivity(intent);
                            }
                        }).addOnFailureListener(e -> {
                            e.printStackTrace();
                            Intent intent=new Intent(getContext(),UserActivity.class);
                        getActivity().finishAffinity();
                        startActivity(intent);
                        });
                    } else {

                        Log.d("VO USER==NULL","da");
                        Intent intent=new Intent(getContext(),UserActivity.class);
                        getActivity().finishAffinity();
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.d("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR",e.toString());
                    Intent intent=new Intent(getContext(),UserActivity.class);
                    getActivity().finishAffinity();
                    startActivity(intent);
                }
            }

        }
    }
}
