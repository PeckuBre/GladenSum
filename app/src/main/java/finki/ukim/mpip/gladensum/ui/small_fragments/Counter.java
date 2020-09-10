package finki.ukim.mpip.gladensum.ui.small_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import finki.ukim.mpip.gladensum.R;
import finki.ukim.mpip.gladensum.viewModels.UserViewModel;


public class Counter extends Fragment {
    private Button bplus;
    private Button bminus;
    private TextView cnt;
    private Integer count;
    private OnCounterChangedListner mListener;

    public Counter() {
        // Required empty public constructor
    }

    public static Counter newInstance() {
        Counter fragment = new Counter();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener= ViewModelProviders.of(getActivity()).get(UserViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_counter, container, false);
        bplus=v.findViewById(R.id.increment);
        bminus=v.findViewById(R.id.decrement);
        cnt=v.findViewById(R.id.counter);
        count=Integer.parseInt(cnt.getText().toString());
        bplus.setOnClickListener(v1 -> {
            count+=1;
            cnt.setText(count.toString());
            updateCount();
        });
        bminus.setOnClickListener(v12 -> {
            count=Math.max(0,--count);
            cnt.setText(count.toString());
            updateCount();
        });
        return v;
    }

    public void updateCount() {
        if (mListener != null) {
            mListener.onChangeCount(count);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            Log.d("Loso","mu fali neso na interaction listenerot");
//        }
    }

    public void setCount(Integer i){
        cnt.setText(i.toString());
        count=i;
        updateCount();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCounterChangedListner {
        void onChangeCount(Integer newCount);
    }
}
