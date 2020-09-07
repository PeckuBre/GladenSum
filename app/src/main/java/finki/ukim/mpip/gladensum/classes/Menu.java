package finki.ukim.mpip.gladensum.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class Menu {

    public String restaurant_id;
    public HashMap<String ,Category> categories;
    public Menu(){
        categories=new HashMap<>();
    }
    public Menu(HashMap<String ,Category> a){
        categories=a;
    }

    public ArrayList<String > getCategoryNames(){ return new ArrayList<>(categories.keySet());}
    public ArrayList<MenuItem> getItems(){
        ArrayList<MenuItem> items=new ArrayList<>();
        for (Category category:categories.values()){
            items.addAll(category.items);
        }
        return items;
    }

    public void addItem(MenuItem item){
        categories.putIfAbsent(item.category,new Category(item.category));
        categories.get(item.category).addItem(item);
    }

    public int getNumCategories(){
        return categories.size();
    }

}
