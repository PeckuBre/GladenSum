package finki.ukim.mpip.gladensum.ui.user_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.classes.MenuItem;
import finki.ukim.mpip.gladensum.classes.OrderItem;
import finki.ukim.mpip.gladensum.viewModels.UserViewModel;
import finki.ukim.mpip.gladensum.ui.small_fragments.Counter;


public class ShowMenuItemFragment extends Fragment {
    public static final String ARG_ITEM = "item";
    public static final String ARG_ORDER_ITEM = "order_item";
    private OrderItem orderItem;
    private TextView name, price, total_cost, comment;
    private Button add;
    private UserViewModel viewModel;
    private MenuItem item;
    private int quantity;
    private Counter counterFragment;
    private int position;

    public ShowMenuItemFragment() {
        // Required empty public constructor
    }

    public static ShowMenuItemFragment newInstance(String param1) {
        ShowMenuItemFragment fragment = new ShowMenuItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (MenuItem) getArguments().getSerializable(ARG_ITEM);
            if (getArguments().getSerializable("order_item") != null){
                orderItem = (OrderItem) getArguments().getSerializable(ARG_ORDER_ITEM);
                position=getArguments().getInt("position");
            }
        }
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        quantity = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_menu_item, container, false);
        counterFragment = (Counter) getChildFragmentManager().findFragmentById(R.id.counter_fragment);
        add = v.findViewById(R.id.add_order_item);
        comment = v.findViewById(R.id.comments);
        if (orderItem != null) {
            counterFragment.setCount(orderItem.qty);
            comment.setText(orderItem.comment);
            add.setText("Commit changes");
        }
        name = v.findViewById(R.id.item_name);
        name.setText(item.name);
        price = v.findViewById(R.id.item_price);
        price.setText(item.price.toString());
        total_cost = v.findViewById(R.id.total_cost);


        add.setOnClickListener(v1 -> {
            if(orderItem!=null){
                viewModel.changeOrderItemAt(new OrderItem(item.id,item.name, comment.getText().toString(), quantity), position);
            }
            else
            viewModel.addToOrder(new OrderItem(item.id,item.name, comment.getText().toString(), quantity), item.restaurant_id);
            counterFragment.setCount(0);
            Navigation.findNavController(v).popBackStack();
        });
        viewModel.getCount().observe(getViewLifecycleOwner(), integer -> {
            total_cost.setText(String.format("Total: %.2f MKD", integer * item.price));
            quantity = integer;
        });

        return v;
    }


}
