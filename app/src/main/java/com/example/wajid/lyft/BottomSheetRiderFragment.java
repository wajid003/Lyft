package com.example.wajid.lyft;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.R.attr.key;

/**
 * Created by wajid on 03-Mar-18.
 */

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {

    String mTag;


    public static BottomSheetRiderFragment newInstance(String tag)
    {
        BottomSheetRiderFragment f= new BottomSheetRiderFragment();
        Bundle args = new Bundle();
        args.putString("TAG",tag);
        f.setArguments(args);
        return f;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString("TAG");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle savedInstanceState)

    {
        View view = inflater.inflate(R.layout.button_sheet_rider,container,false);
        return view;
    }
}
