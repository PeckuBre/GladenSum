package finki.ukim.mpip.gladensum.viewModels;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import finki.ukim.mpip.gladensum.classes.Menu;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.Order;
import finki.ukim.mpip.gladensum.classes.OrderItem;
import finki.ukim.mpip.gladensum.classes.Restaurant;
import finki.ukim.mpip.gladensum.ui.small_fragments.Counter;


public class UserViewModel extends ViewModel implements Counter.OnCounterChangedListner {

    private MutableLiveData<Integer> counterCount;
    private MutableLiveData<HashMap<String, Restaurant>> restaurantsMap;
    private FirebaseFirestore db;
    private MutableLiveData<HashMap<String, Menu>> menus;
    private MutableLiveData<Set<Order>> orders;
    private MutableLiveData<Menu> lastShownMenu;
    private MutableLiveData<Order> currentOrder;
    private FirebaseUser user;

    public UserViewModel() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        counterCount = new MutableLiveData<>(0);
        lastShownMenu = new MutableLiveData<>();
        restaurantsMap = new MutableLiveData<>();
        currentOrder = new MutableLiveData<>();
        TreeSet<Order> orders_set=new TreeSet<>((o1, o2) -> o2.time.compareTo(o1.time));
        orders = new MutableLiveData<>(orders_set);
        menus = new MutableLiveData<>();
        menus.setValue(new HashMap<>());
        db = FirebaseFirestore.getInstance();
        HashMap<String, Restaurant> restaurantss = new HashMap<String, Restaurant>();
        ArrayList<Restaurant> restaurantList = new ArrayList<>();
        db.collection("app_users").whereEqualTo("type", "Restaurant").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Restaurant r = doc.toObject(Restaurant.class);
                    r.id = doc.getId();
                    restaurantss.put(r.id, r);
                }
                restaurantsMap.setValue(restaurantss);
            }
        });

        db.collection("orders").whereEqualTo("user_id",user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    e.printStackTrace();
                }
                else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        String TAG="tip:";
                        Order a=doc.getDocument().toObject(Order.class);
                        switch (doc.getType()) {
                            case ADDED:
                                a.id=doc.getDocument().getId();
                                orders_set.add(a);
                                break;
                            case MODIFIED:
                                Log.d("MODIFIED",a.toString());
                                break;
                            case REMOVED:
//                                categories.get(item.category).removeItem(item);
                                Log.d(TAG, "Removed item: " + doc.getDocument().getData());
                                break;
                        }
                    }
                    orders.setValue(orders_set);
                }
            }
        });

    }

    public void addToOrder(OrderItem orderItem, String restaurant_id) {
        if (orderItem.qty <= 0)
            return;
        Order order = currentOrder.getValue();
        if (order == null) {
            order = new Order(restaurant_id);
            order.addOrderItem(orderItem);
        } else if (!order.restaurant_id.equals(restaurant_id)) {
            Log.d("addToOrder", "DRUG RESTORAN");
            order = new Order(restaurant_id);
            order.addOrderItem(orderItem);
        } else
            order.addOrderItem(orderItem);
        currentOrder.setValue(order);
    }

    public void changeOrderItemAt(OrderItem orderItem, int position) {
        Order order = currentOrder.getValue();
        if (orderItem.qty == 0) {
            order.items.remove(position);
            currentOrder.setValue(order);
            return;
        }
        order.items.set(position, orderItem);
        currentOrder.setValue(order);
    }

    public void removeOrderItemAt(int position){
        Order order=currentOrder.getValue();
        order.items.remove(position);
        currentOrder.setValue(order);
    }

    public LiveData<Order> getCurrentOrder() {
        return currentOrder;
    }

    public void readMenuForRestaurant(String id) {
        HashMap<String, Menu> map = menus.getValue();
        if (map.containsKey(id)) {
            lastShownMenu.setValue(map.get(id));
            menus.setValue(menus.getValue());
            return;
        }
        Menu menu = new Menu();
        db.collection("Items").whereEqualTo("restaurant_id", id).
                addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null)
                        e.printStackTrace();
                    else {
                        Menu menu1 = new Menu();
                        menu1.restaurant_id = id;
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            MenuItem item = doc.toObject(MenuItem.class);
                            item.id = doc.getId();
                            menu1.addItem(item);
                        }
                        map.put(id, menu1);
                        menus.postValue(map);
                        lastShownMenu.setValue(menu1);
                    }
                });
    }

    public ArrayList<Restaurant> getRestaurantsThatStartWith(String str) {
        return restaurantsMap.getValue().values().stream().filter(x -> x.name.toLowerCase().
                startsWith(str.toLowerCase())).collect(Collectors.toCollection(ArrayList::new));
    }

    public LiveData<Integer> getCount() {
        return counterCount;
    }

    public LiveData<Menu> getMenu() {
        return lastShownMenu;
    }

    public LiveData<HashMap<String, Menu>> getMenus() {
        return menus;
    }

    public LiveData<HashMap<String, Restaurant>> getRestaurants() {
        return restaurantsMap;
    }

    public LiveData<Restaurant> getRestaurantById(String id) {
        return new MutableLiveData<>(restaurantsMap.getValue().get(id));//?????????? ne pipaj ako raboti
    }

    public LiveData<Set<Order>> getPreviousOrders(){
        return orders;
    }

    @Override
    public void onChangeCount(Integer newCount) {
        counterCount.setValue(newCount);
    }

    public void deleteOrder() {
        currentOrder.setValue(null);
    }

}