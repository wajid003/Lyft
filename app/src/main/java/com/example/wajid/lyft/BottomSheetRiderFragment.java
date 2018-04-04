package com.example.wajid.lyft;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.R.attr.key;

/**
 * Created by wajid on 03-Mar-18.
 */

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {

    String mLocation,mDestination;



    public static BottomSheetRiderFragment newInstance(String location,String destination)
    {
        BottomSheetRiderFragment f= new BottomSheetRiderFragment();
        Bundle args = new Bundle();
        args.putString("location",location);
        args.putString("destination",destination);
        f.setArguments(args);
        return f;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLocation = getArguments().getString("location");
        mDestination = getArguments().getString("destination");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle savedInstanceState)

    {
        View view = inflater.inflate(R.layout.button_sheet_rider,container,false);
        TextView txtLocation = (TextView) view.findViewById(R.id.txtLocation);
        TextView txtDestination = (TextView) view.findViewById(R.id.txtDestination);

        //set data
        txtLocation.setText(mLocation);
        txtDestination.setText(mDestination);

        return view;
    }

}
