package finki.ukim.mpip.gladensum.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import finki.ukim.mpip.gladensum.classes.Driver;
import finki.ukim.mpip.gladensum.classes.Order;

public class DriverViewModel extends ViewModel {
    public MutableLiveData<List<Order>> restaurant_orders;
    public MutableLiveData<List<Order>> taken_orders;
    public Driver driver;

    public DriverViewModel() {
        ArrayList<Order> restaurant_orders1 = new ArrayList<>();
        ArrayList<Order> taken_orders1 = new ArrayList<>();
        restaurant_orders = new MutableLiveData<>(restaurant_orders1);
        taken_orders = new MutableLiveData<>(taken_orders1);
        FirebaseFirestore.getInstance().collection("app_users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                driver = documentSnapshot.toObject(Driver.class);
            }
        });

        FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", driver.restaurant_id)
                .whereEqualTo("status", "PREPARED").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    Order a = doc.getDocument().toObject(Order.class);
                    restaurant_orders1.add(a);
                }
                restaurant_orders.setValue(restaurant_orders1);
            }
        });

        FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", driver.restaurant_id)
                .whereEqualTo("status", "ON_THE_WAY").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    Order a = doc.getDocument().toObject(Order.class);
                    taken_orders1.add(a);
                }
                taken_orders.setValue(taken_orders1);
            }
        });

    }

    public LiveData<List<Order>> getReadyOrders(){
        return restaurant_orders;
    }

    public LiveData<List<Order>> getTakenOrders(){
        return taken_orders;
    }

    public void takeOrderItemAt(int position) {
        Order removed=restaurant_orders.getValue().remove(position);
        removed.uploadOrder();
    }
}