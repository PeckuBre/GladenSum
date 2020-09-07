package finki.ukim.mpip.gladensum.ui.restaurant_menu;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;

import finki.ukim.mpip.gladensum.classes.Category;
import finki.ukim.mpip.gladensum.classes.MenuItem;

public class MenuViewModel extends ViewModel {

    private MutableLiveData<HashMap<String ,Category>> liveData;
    private FirebaseFirestore db;
    private HashMap<String , Category> categories;

    public MenuViewModel() {
        db=FirebaseFirestore.getInstance();
        categories = new HashMap<>();
        liveData= new MutableLiveData<>();
//        liveData.setValue(categories);
//        db.collection("Items");

//        TUKA TREBA DA GI ZIMAM PODATOCITE I DA IM GI SERVIRAM NA DRUGITE KLASI
        db.collection("Items")
                .whereEqualTo("restaurant_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("error", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        String TAG="tip:";
                        MenuItem item=dc.getDocument().toObject(MenuItem.class);
                        item.setId(dc.getDocument().getId());
                        switch (dc.getType()) {
                            case ADDED:
                                categories.putIfAbsent(item.category,new Category(item.category));
                                categories.get(item.category).addItem(item);
                                Log.d(TAG, "New item " + dc.getDocument().getData());
                                break;
                            case MODIFIED:
//                                dc.getDocument().geti
                                categories.putIfAbsent(item.category,new Category(item.category));
                                categories.get(item.category).addItem(item);
                                Log.d(TAG, "Mod item" + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                categories.get(item.category).removeItem(item);
                                Log.d(TAG, "Removed item: " + dc.getDocument().getData());
                                break;
                        }
                    }
                    for (String cat:categories.keySet()) {
                        if(!categories.get(cat).hasItems())
                            categories.remove(cat);
                    }
                    liveData.setValue(categories);
                });
    }


    public LiveData<HashMap<String ,Category>> getCategories(){return liveData;}
}