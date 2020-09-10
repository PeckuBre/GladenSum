package finki.ukim.mpip.gladensum.classes;

import java.util.HashMap;

public class Restaurant extends AppUser {
    public String name,address,phone_number,website,id;
    public Menu menu;

    public Restaurant() {
        super();
    }

    public Restaurant(String type, String id) {
        super(type, id);
    }

    public Restaurant(String type, String name, String id, String phone_number,String website) {
        super(type, name, id, phone_number);
        this.website=website;
        menu=new Menu();
    }

    public void setMenu(Menu a){menu=a;}

    @Override
    public HashMap<String, Object> getKeyValuePairs() {
        HashMap<String, Object> map= super.getKeyValuePairs();
        map.put("website",website);
        return map;
    }
}
