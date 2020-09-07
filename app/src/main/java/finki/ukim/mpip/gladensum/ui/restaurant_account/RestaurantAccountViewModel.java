package finki.ukim.mpip.gladensum.ui.restaurant_account;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import finki.ukim.mpip.gladensum.R;

public class RestaurantAccountViewModel extends ViewModel {
    private MutableLiveData<Bitmap> restaurantPictureBitmap;

    public RestaurantAccountViewModel() {
        restaurantPictureBitmap=new MutableLiveData<>();
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference ref
                = storage
                .child(String.format("%s%s/logo", R.string.base_restaurant_images,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        ref.getBytes(Integer.MAX_VALUE).addOnSuccessListener(byteArray -> {
            restaurantPictureBitmap.setValue(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        });
    }

    public LiveData<Bitmap> getRestaurantPicture() {
        return restaurantPictureBitmap;
    }
}
