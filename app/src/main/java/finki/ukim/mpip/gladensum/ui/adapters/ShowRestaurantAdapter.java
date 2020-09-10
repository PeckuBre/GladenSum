package finki.ukim.mpip.gladensum.ui.adapters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Restaurant;
import finki.ukim.mpip.gladensum.ui.user_fragments.HomeFragment;

public class ShowRestaurantAdapter extends BaseAdapter {

    public List<Restaurant> dataset;
    private HomeFragment parentFragment;

    public ShowRestaurantAdapter(List<Restaurant> data, HomeFragment parent) {
        super();
        this.parentFragment=parent;
        this.dataset=data;
    }

    @Override
        public int getCount() {
            return dataset.size();
        }

        @Override
        public Object getItem(int position) {
            return dataset.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=parentFragment.getLayoutInflater().inflate(R.layout.show_restaurant_to_user,parent,false);
                convertView.setOnClickListener(v -> {
                    Bundle args=new Bundle();
                    args.putString("restaurant_id",dataset.get(position).id);
                    Navigation.findNavController(parentFragment.getView()).navigate(R.id.show_menu_to_user,args);
                });
            }
            Restaurant r=dataset.get(position);
            TextView t=convertView.findViewById(R.id.show_name);
            t.setText(r.name);
            t=convertView.findViewById(R.id.show_address);
            t.setText(r.address);
            Button  b=convertView.findViewById(R.id.call);
            b.setOnClickListener(v->{
                parentFragment.phoneToCall=r.phone_number.trim();
                if (ContextCompat.checkSelfPermission(parentFragment.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    parentFragment.requestPermissions( new String[]{Manifest.permission.CALL_PHONE},HomeFragment.REQUEST_PHONE_CALL);
                }
                else
                {
                    parentFragment.callRestaurant();
                }
            });
            return convertView;
        }
}
