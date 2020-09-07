package finki.ukim.mpip.gladensum.classes;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;

public class OrderItem implements Serializable {

    public String item_id,name,comment;
    // da sodrzi menuitem?
    public int qty;

    public OrderItem(){}

    public OrderItem(String item_id,String name, String comment, int qty) {
        this.item_id = item_id;
        this.name=name;
        this.comment = comment;
        this.qty = qty;
    }

    public static OrderItem fromJson(String s){
        return new Gson().fromJson(s,OrderItem.class);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
