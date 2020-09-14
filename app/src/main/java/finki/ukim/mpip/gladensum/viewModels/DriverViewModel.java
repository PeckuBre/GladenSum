package finki.ukim.mpip.gladensum.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

        restaurant_orders = new MutableLiveData<>();
        taken_orders = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("app_users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            driver = documentSnapshot.toObject(Driver.class);}).continueWith(task -> {
                loadReadyOrders();
                loadTakenOrders();
                return null;
            });

//        FirebaseFirestore.getInstance().collection("app_users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(documentSnapshot -> {
//            driver = documentSnapshot.toObject(Driver.class);
//            ArrayList<Order> restaurant_orders1 = new ArrayList<>();
//            restaurant_orders = new MutableLiveData<>(restaurant_orders1);
//            FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", driver.restaurant_id)
//                    .whereEqualTo("status", "PREPARED").addSnapshotListener((queryDocumentSnapshots, e) -> {
//                if (e != null) {
//                    e.printStackTrace();
//                } else {
//                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                        Order a = doc.getDocument().toObject(Order.class);
//                        restaurant_orders1.add(a);
//                    }
//                    restaurant_orders.postValue(restaurant_orders1);
//                }
//            });
//            ArrayList<Order> taken_orders1 = new ArrayList<>();
//            taken_orders = new MutableLiveData<>(taken_orders1);
//            FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", driver.restaurant_id)
//                    .whereEqualTo("status", "ON_THE_WAY").addSnapshotListener((queryDocumentSnapshots, e) -> {
//                if (e != null) {
//                    e.printStackTrace();
//                } else {
//                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                        Order a = doc.getDocument().toObject(Order.class);
//                        taken_orders1.add(a);
//                    }
//                    taken_orders.postValue(taken_orders1);
//                }
//            });
//        });
    }

    private void loadReadyOrders() {
        FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", driver.restaurant_id)
                .whereEqualTo("status", "PREPARED").addSnapshotListener((queryDocumentSnapshots, e) -> {
            ArrayList<Order> restaurant_orders1 = new ArrayList<>();
            if (e != null) {
                e.printStackTrace();
            } else {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    Order a = doc.getDocument().toObject(Order.class);
                    restaurant_orders1.add(a);
                }
                restaurant_orders.postValue(restaurant_orders1);
            }
        });
    }

    private void loadTakenOrders() {
        FirebaseFirestore.getInstance().collection("orders").whereEqualTo("restaurant_id", driver.restaurant_id)
                .whereEqualTo("status", "ON_THE_WAY").addSnapshotListener((queryDocumentSnapshots, e) -> {
            ArrayList<Order> taken_orders1 = new ArrayList<>();
            if (e != null) {
                e.printStackTrace();
            } else {
                Log.d("loading","orders");
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    Order a = doc.getDocument().toObject(Order.class);
                    taken_orders1.add(a);
                }
                taken_orders.postValue(taken_orders1);
            }
        });
    }


    public LiveData<List<Order>> getReadyOrders() {
        return restaurant_orders;
    }

    public Driver getDriver() {
        return driver;
    }

    public LiveData<List<Order>> getTakenOrders() {
        return taken_orders;
    }

    public void removeTakenOrder(Order o){
        List<Order> orderss=taken_orders.getValue();


        orderss.remove(o);
        o.proceed();
        driver.takeOrder(o);
        FirebaseFirestore.getInstance().collection("orders").document(o.id).set(o).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                taken_orders.postValue(orderss);
            }
        });

    }

    public void removePreparedOrderAt(int position) {

        List<Order> orderss=restaurant_orders.getValue();


        Order o=orderss.remove(position);
        o.proceed();
        driver.takeOrder(o);
        FirebaseFirestore.getInstance().collection("orders").document(o.id).set(o).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                restaurant_orders.postValue(orderss);
            }
        });
//        loadReadyOrders();
    }
}