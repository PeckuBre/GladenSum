package finki.ukim.mpip.gladensum.classes;

import androidx.annotation.NonNull;

public class AppUser {
    public String type,id,name,places_id,website,phone_number;

    public AppUser() {
    }

    public AppUser(String type,String id){
        this.type=type;
        this.id=id;
    }



    @NonNull
    @Override
    public String toString() {

        return "User type: " +type;
    }
}
