package finki.ukim.mpip.gladensum.ui.restaurant_orders;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.classes.OrderForRestaurant;
import finki.ukim.mpip.gladensum.classes.Restaurant;

public class RestaurantOrdersViewModel extends ViewModel {
    private MutableLiveData<Set<Order>> orders;
    private MutableLiveData<HashMap<String, Order>> ordersMap;

    public RestaurantOrdersViewModel() {
        Set<Order> orders_set = new HashSet<>();
        HashMap<String, Order> orders_map = new HashMap<>();
        orders = new MutableLiveData<>(orders_set);
        ordersMap = new MutableLiveData<>(orders_map);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("orders").whereEqualTo("restaurant_id", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

}
