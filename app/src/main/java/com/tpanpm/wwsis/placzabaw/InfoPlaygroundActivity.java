package com.tpanpm.wwsis.placzabaw;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class InfoPlaygroundActivity extends AppCompatActivity {

    TextView textViewTitle, textViewDesc;
    RatingBar rate;
    ImageView img;
    LatLng latLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_playground);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("playgrounds");

        Intent intent = getIntent();
        double locLat = intent.getDoubleExtra(MainActivity.EXTRA_MESSAGE1, 0);
        double locLong = intent.getDoubleExtra(MainActivity.EXTRA_MESSAGE2, 0);

        latLog = new LatLng(locLat, locLong);


        myRef.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Playground playground = dataSnapshot.getValue(Playground.class);

                if (!Objects.equals(latLog, new LatLng(playground.getLAT(), playground.getLON()))) {
                    return;
                }

                textViewTitle = (TextView) findViewById(R.id.editText_playground_name);
                textViewTitle.setText(playground.getNAME());
                textViewDesc = (TextView) findViewById(R.id.editText_playground_comment);
                textViewDesc.setText(playground.getDESC());
                rate = (RatingBar) findViewById(R.id.ratingBar);
                rate.setRating(playground.getRATE());

                img = (ImageView) findViewById(R.id.backdropImage);


                String url = playground.getIMAGE();

                StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressInfo);
                Glide.with(InfoPlaygroundActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(httpsReference)
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(img);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}