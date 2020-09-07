package finki.ukim.mpip.gladensum.ui.adapters;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.Restaurant;
import finki.ukim.mpip.gladensum.ui.home.HomeFragment;

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

//    @Override
//    public View getView(final int position, View convertView, final ViewGroup parent)
//    {
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.gridchild, parent, false);
//            Button btn =  (Button)convertView.findViewById(R.id.btnCountry);
//            btn.setOnClickListener(new Button.OnClickListener(){
//                public void onClick(View v) {
//                    String name = ((Button)v).getTag().toString();
//                    if(name != ""){
//                        Toast.makeText(context, name, Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
//        Country country = items.get(position);
//
//        Button btn = (Button)convertView.findViewById(R.id.btnCountry);
//        btn.setText(country.Name);
//        btn.setTag(country.Name);
//
//        ImageView image = (ImageView)convertView.findViewById(R.id.imgCountry);
//        image.setImageResource(country.Flag);
//
//        return convertView;
//    }


}
