package finki.ukim.mpip.gladensum.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.hash.HashCode;

import java.io.Serializable;
import java.util.Objects;

public class MenuItem implements Serializable{

    public MenuItem(){}
    public String category,name,restaurant_id,id;
    public Float price;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof MenuItem){
            Log.d("isti se",id);
            return id.equals(((MenuItem) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void setId(String iid){id=iid;}

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
