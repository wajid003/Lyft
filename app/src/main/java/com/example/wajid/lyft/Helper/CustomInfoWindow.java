package com.example.wajid.lyft.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.wajid.lyft.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by wajid on 03-Mar-18.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    public CustomInfoWindow() {
    }

    View myView;

    public CustomInfoWindow(Context context){
        myView = LayoutInflater.from(context).inflate(R.layout.custom_rider_info_window,null);
    }

    public View getInfoWindow(Marker marker){
        TextView txtPickupTitle = ((TextView)myView.findViewById(R.id.txtPickupInfo));
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = ((TextView)myView.findViewById(R.id.txtPickupSnipet));
        txtPickupSnippet.setText(marker.getSnippet());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


}
