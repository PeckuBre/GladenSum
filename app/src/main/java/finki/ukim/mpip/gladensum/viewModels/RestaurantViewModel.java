package finki.ukim.mpip.gladensum.viewModels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.classes.Restaurant;

public class RestaurantViewModel extends ViewModel {
    private MutableLiveData<Set<Order>> orders;
    private MutableLiveData<HashMap<String, Order>> ordersMap; //ova e visok
    private MutableLiveData<Bitmap> restaurantPictureBitmap;
    private MutableLiveData<HashMap<String, Category>> categoryMap;
    private HashMap<String, Category> categories;
    private Restaurant restaurant;

    public RestaurantViewModel() {
        categories = new HashMap<>();
        categoryMap = new MutableLiveData<>();

        FirebaseFirestore.getInstance().collection("app_users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            restaurant = documentSnapshot.toObject(Restaurant.class);
            loadMenu();
            loadOrders();
        });
        restaurantPictureBitmap = new MutableLiveData<>();
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference ref
                = storage
                .child(String.format("%s%s/logo", R.string.base_restaurant_images,
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
        ref.getBytes(Integer.MAX_VALUE).addOnSuccessListener(byteArray -> restaurantPictureBitmap.setValue(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length)));
    }


    public void loadMenu() {
        FirebaseFirestore.getInstance().collection("items")
                .whereEqualTo("restaurant_id", restaurant.places_id)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("error", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        String TAG = "tip:";
                        MenuItem item = dc.getDocument().toObject(MenuItem.class);
                        item.setId(dc.getDocument().getId());
                        switch (dc.getType()) {
                            case ADDED:
                                categories.putIfAbsent(item.category, new Category(item.category));
                                categories.get(item.category).addItem(item);
                                Log.d(TAG, "New item " + dc.getDocument().getData());
                                break;
                            case MODIFIED:
                                categories.putIfAbsent(item.category, new Category(item.category));
                                categories.get(item.category).addItem(item);
                                Log.d(TAG, "Mod item" + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                categories.get(item.category).removeItem(item);
                                Log.d(TAG, "Removed item: " + dc.getDocument().getData());
                                break;
                        }
                    }
                    for (String cat : categories.keySet()) {
                        if (!categories.get(cat).hasItems())
                            categories.remove(cat);
                    }
                    categoryMap.setValue(categories);
                });
    }

    public void loadOrders() {
        HashMap<String, Order> orders_map = new HashMap<>();
        Set<Order> orders_set = new TreeSet<>((o1, o2) -> o2.time.compareTo(o1.time));
        orders = new MutableLiveData<>(orders_set);
        ordersMap = new MutableLiveData<>(orders_map);
        FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", restaurant.places_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("error", "listen:error", e);
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Order r = doc.toObject(Order.class);
                        r.id = doc.getId();
                        if (r.status != Order.STATUS.COMPLETED) {
                            orders_set.add(r);
                            orders_map.put(r.id, r);
                        }
                        Log.d("ORDER", r.id);
                    }
                    orders.setValue(orders_set);
                    ordersMap.setValue(orders_map);
                }
            }
        });

    }

    public LiveData<Set<Order>> getOrders() {
        return orders;
    }

    public LiveData<HashMap<String, Order>> getOrdersMap() {
        return ordersMap;
    }

    public LiveData<Bitmap> getRestaurantPicture() {
        return restaurantPictureBitmap;
    }

    public LiveData<HashMap<String, Category>> getCategories() {
        return categoryMap;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

}
