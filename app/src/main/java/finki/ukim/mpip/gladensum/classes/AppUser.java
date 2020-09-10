package finki.ukim.mpip.gladensum.classes;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class AppUser {
    public String type,id,name,phone_number;

    public AppUser() {
    }

    public AppUser(String type,String id){
        this.type=type;
        this.id=id;
    }

    public AppUser(String type,String name,String id,String phone_number){
        this.type=type;
        this.phone_number=phone_number;
        this.id=id;
        this.name=name;
    }
    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s id:%s",type,name,id);
    }

    public HashMap<String ,Object> getKeyValuePairs(){
        HashMap<String ,Object> map=new HashMap<>();
        map.put("type",type);
        map.put("id",id);
        map.put("name",name);
        map.put("phone_number",phone_number);
        return map;
    }

}
