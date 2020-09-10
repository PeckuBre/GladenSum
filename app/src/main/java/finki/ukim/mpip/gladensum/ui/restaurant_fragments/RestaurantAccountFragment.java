package finki.ukim.mpip.gladensum.ui.restaurant_fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Arrays;

import finki.ukim.mpip.gladensum.LoadingActivity;
import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.viewModels.RestaurantViewModel;

import static android.app.Activity.RESULT_OK;

public class RestaurantAccountFragment extends Fragment {

    private static final int PICK_IMAGE = 324;
    private static int RC_SIGN_IN = 123;

    private ImageView profilePicture;
    private StorageReference storage;
    private Bitmap profilePictureBitmap;
    private RestaurantViewModel restaurantViewModel;
    private String restaurantImageLocation;
    public static RestaurantAccountFragment newInstance() {
        return new RestaurantAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantImageLocation=String.format("%s%s/logo", R.string.base_restaurant_images,
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        storage = FirebaseStorage.getInstance().getReference();
        StorageReference ref
                = storage
                .child(restaurantImageLocation);
        ref.getBytes(Integer.MAX_VALUE).addOnSuccessListener(byteArray -> {
            profilePictureBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            showRestaurantImage();
        });
        restaurantViewModel =new ViewModelProvider(this.getActivity()).get(RestaurantViewModel.class);
        restaurantViewModel.getRestaurantPicture().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                profilePictureBitmap=bitmap;
                showRestaurantImage();
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_restaurant_account, container, false);
        final TextView textView = root.findViewById(R.id.text_account);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        Button b = root.findViewById(R.id.sign_out_button);
        b.setOnClickListener(v -> goToSignInMenu());
        Button pickImg = root.findViewById(R.id.choose_restaurant_img_btn);
        profilePicture = root.findViewById(R.id.restaurant_picture);
        showRestaurantImage();
        pickImg.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);


        });
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
            getActivity().finishAffinity();
            Intent intent = new Intent(getContext(), LoadingActivity.class);
            startActivity(intent);
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Uri filePath = data.getData();
                try {

                    // Setting image on image view using Bitmap
                    profilePictureBitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getActivity().getContentResolver(),
                                    filePath);
                    showRestaurantImage();
                    uploadRestaurantImage(filePath);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }

            }
        }
    }

    private void uploadRestaurantImage(Uri filePath) {
        if (filePath != null) {
            StorageReference ref
                    = storage
                    .child(restaurantImageLocation);
            ref.putFile(filePath);
        }
    }

    private void showRestaurantImage() {
        if (profilePictureBitmap != null && profilePicture != null)
            profilePicture.setImageBitmap(profilePictureBitmap);
    }
}


