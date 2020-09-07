package finki.ukim.mpip.gladensum.ui.restaurant_menu;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.ui.restaurant_account.RestaurantAccountViewModel;

import static android.app.Activity.RESULT_OK;

public class EditRestaurantItemFragment extends Fragment {
    private static final int PICK_IMAGE = 3122;
    private MenuItem item;
    private Bitmap pictureBitmap;
    private ImageView pictureView;
    private EditText name;
    private EditText price;
    private AutoCompleteTextView cat;
    private FirebaseFirestore db;
    private MenuViewModel viewModel;
    private StorageReference storage;
    private RestaurantAccountViewModel restaurantAccountViewModel;
    private String menuItemImageLocation;

    public static EditRestaurantItemFragment newInstance() {
        return new EditRestaurantItemFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        viewModel = new ViewModelProvider(this.getActivity()).get(MenuViewModel.class);
        restaurantAccountViewModel = new ViewModelProvider(this.getActivity()).get(RestaurantAccountViewModel.class);
        restaurantAccountViewModel.getRestaurantPicture().observe(this, bitmap -> {
            if (pictureBitmap == null) {
                pictureBitmap = bitmap;
                showRestaurantImage();
            }
        });
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        item = (MenuItem) getArguments().getSerializable("item");
        menuItemImageLocation =String.format("%s%s/menu_item_images/%s", R.string.base_restaurant_images,
                FirebaseAuth.getInstance().getCurrentUser().getUid(), item.id);
        StorageReference ref
                = storage
                .child(menuItemImageLocation);

        ref.getBytes(Integer.MAX_VALUE).addOnSuccessListener(byteArray -> {
            pictureBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            showRestaurantImage();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_restaurant_item_fragment, container, false);
        String[] cats = new String[viewModel.getCategories().getValue().keySet().size()];
        int i = 0;
        for (String a : viewModel.getCategories().getValue().keySet())
            cats[i++] = a;
        cat = v.findViewById(R.id.editCategory);
        cat.setText(item.category);
        cat.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cats));
        name = v.findViewById(R.id.editName);
        name.setText(item.name);
        pictureView = v.findViewById(R.id.menu_item_img);
        showRestaurantImage();
        price = v.findViewById(R.id.editPrice);
        price.setText(item.price.toString());
        Button submit = v.findViewById(R.id.submit_item_changes);
        Button delete = v.findViewById(R.id.deleteItem);
        delete.setOnClickListener(x -> db.collection("Items").document(item.id).
                delete().addOnCompleteListener(task -> Navigation.findNavController(getView()).popBackStack()));
        Button changeImageButton = v.findViewById(R.id.change_menu_item_image_btn);
        changeImageButton.setOnClickListener(v1 -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

        submit.setOnClickListener(x -> {
            if ((!name.getText().toString().equals("") && !cat.getText().toString().equals("") && Float.parseFloat(price.getText().toString()) > 0)
                    && (!name.getText().toString().equals(item.name) || !cat.getText().toString().equals(item.category) || Float.parseFloat(price.getText().toString()) != item.price)) {
                HashMap<String, Object> smenet = new HashMap<>();
                smenet.put("name", name.getText().toString());
                smenet.put("category", cat.getText().toString());
                smenet.put("price", Float.parseFloat(price.getText().toString()));
                smenet.put("restaurant_id", item.restaurant_id);
                db.collection("Items").document(item.id).set(smenet);
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Uri filePath = data.getData();
                try {
                    // Setting image on image view using Bitmap
                    pictureBitmap = MediaStore
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
                    .child(menuItemImageLocation);
            ref.putFile(filePath);
        }
    }


    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showRestaurantImage() {
        if (pictureBitmap != null && pictureView != null)
            pictureView.setImageBitmap(pictureBitmap);
    }


}
