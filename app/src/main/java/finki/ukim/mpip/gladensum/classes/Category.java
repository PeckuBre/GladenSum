package finki.ukim.mpip.gladensum.classes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Category {
    public String name;
    public Set<MenuItem> items;

    public Category(String name){
        this.name=name;
        items=new HashSet<>();
    }

    public void addItem(MenuItem item){
        items.remove(item);
        items.add(item);
    }

    public boolean removeItem(MenuItem item){return items.remove(item);}

    public boolean hasItems(){return items.size()>0;}
}
