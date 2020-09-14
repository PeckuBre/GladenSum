package finki.ukim.mpip.gladensum.classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class Order implements Serializable {


    public void setResponsibleDriver(String  driver_id) {
        this.responsible_driver=driver_id;
    }

    public static enum STATUS {
        CANCELED,POSTED, OPENED, PREPARED, ON_THE_WAY, COMPLETED
    }


    public String id;
    public String user_id;
    public String restaurant_id;
    public Double latitude, longitude;
    public String address;
    public List<OrderItem> items;
    public STATUS status;
    public Timestamp time;
    public String responsible_driver;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String restaurant_id) {
        this.user_id = FirebaseAuth.getInstance().getUid();
        this.restaurant_id = restaurant_id;
        this.longitude = null;
        this.latitude = null;
        this.items = new ArrayList<>();
        this.status = STATUS.POSTED;
        time = Timestamp.now();
    }

    public Order(String restaurant_id, Double lon, Double lat) {
        this.user_id = FirebaseAuth.getInstance().getUid();
        this.restaurant_id = restaurant_id;
        this.longitude = lon;
        this.latitude = lat;
        this.items = new ArrayList<>();
        this.status = STATUS.POSTED;
        time =Timestamp.now();

    }

    public String decodeStatus() {
        switch (status) {
            case OPENED:
                return "OPENED";
            case POSTED:
                return "POSTED";
            case PREPARED:
                return "PREPARED";
            case COMPLETED:
                return "COMPLETED";
            case ON_THE_WAY:
                return "ON_THE_WAY";
            case CANCELED:
                return "CANCELED";
        }
        return null;
    }

    public String cancel(){
        status=STATUS.CANCELED;
        return "CANCELED";
    }


    public static STATUS endodeStatus(String status) {
        switch (status) {
            case "OPENED":
                return STATUS.OPENED;
            case "POSTED":
                return STATUS.POSTED;
            case "PREPARED":
                return STATUS.PREPARED;
            case "COMPLETED":
                return STATUS.COMPLETED;
            case "ON_THE_WAY":
                return STATUS.ON_THE_WAY;
        }
        return null;
    }

    public String proceed() {
        switch (status) {
            case POSTED:
                status = STATUS.OPENED;
                break;
            case OPENED:
                status = STATUS.PREPARED;
                break;
            case PREPARED:
                status = STATUS.ON_THE_WAY;
                break;
            case ON_THE_WAY:
                status = STATUS.COMPLETED;
                break;
        }
        return status.toString();
    }

    public void setUserLocation(LatLng ll) throws IOException {
        longitude = ll.longitude;
        latitude = ll.latitude;
    }

    public void setUserAddress(String address) {
        this.address = address;
    }

    public static Order fromJson(String s) {
        return new Gson().fromJson(s, Order.class);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void addOrderItem(OrderItem a) {
        items.add(a);
    }

    public void removeOrderItem(int i) {
        items.remove(i);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Order))
            return false;
        Order o2 = (Order) obj;
        return o2.id.equals(id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean isEmptyOrder() {
        return items.size() == 0;
    }

    public void changeOrderTime(){
        time=Timestamp.now();
    }

    public void uploadOrder(){
        //TODO: mozebi ke treba da se smeni
        FirebaseFirestore.getInstance().collection("orders").document(id).set(this);
    }

}
