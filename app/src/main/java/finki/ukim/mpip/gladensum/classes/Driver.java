package finki.ukim.mpip.gladensum.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Driver extends AppUser {
    public String restaurant_id;
    public List<Order> orders;

    public Driver(String type,String name, String id,String restaurant_id,String phone_number) {
        super(type,name, id,phone_number);
        this.restaurant_id=restaurant_id;
        this.orders= new ArrayList<>();
    }

    @Override
    public HashMap<String, Object> getKeyValuePairs() {
        HashMap<String ,Object>  map=super.getKeyValuePairs();
        map.put("restaurant_id",restaurant_id);
        return map;
    }
}
