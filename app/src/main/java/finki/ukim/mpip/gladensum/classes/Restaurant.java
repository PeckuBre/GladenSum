package finki.ukim.mpip.gladensum.classes;

public class Restaurant extends AppUser {
    public String name,address,phone_number,website,places_id,id;
    public Menu menu;

    public Restaurant() {
        super();
    }

    public Restaurant(String type, String id) {
        super(type, id);
    }

    //    public void setMenu(HashMap<String ,Category> a){menu=new Menu(a);}
    public void setMenu(Menu a){menu=a;}
}
