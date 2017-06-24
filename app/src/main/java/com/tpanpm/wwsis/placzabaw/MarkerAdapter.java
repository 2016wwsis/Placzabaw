package com.tpanpm.wwsis.placzabaw;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class MarkerAdapter  implements InfoWindowAdapter {
    LayoutInflater inflater = null;
    float rate;
    private TextView textViewTitle;
    private TextView textViewDescription;

    public MarkerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public MarkerAdapter(LayoutInflater inflater, float rate) {
        this.inflater = inflater;
        this.rate = rate;
    }



    public void setRate(float rate){
        this.rate = rate;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        View v = inflater.inflate(R.layout.custom_info_contents, null);
        if (marker != null) {

            textViewTitle = (TextView) v.findViewById(R.id.title_test);
            textViewDescription = (TextView) v.findViewById(R.id.snippet);
            final TextView textViewRating = ((TextView) v.findViewById(R.id.rating));
            final RatingBar ratingBar = ((RatingBar) v.findViewById(R.id.rating_bar));

            textViewTitle.setText(marker.getTitle());
            textViewDescription.setText(marker.getSnippet());
            textViewRating.setText(String.format("%.1f", rate));
            ratingBar.setRating(rate);

        }

        return (v);


    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }



}