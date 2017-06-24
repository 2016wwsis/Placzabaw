package com.tpanpm.wwsis.placzabaw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListOfPlaygroundActivity extends AppCompatActivity {

    private ListView mlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_playground);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("playgrounds");

        mlistView = (ListView) findViewById(R.id.listView);
        final ArrayList<Playground> playgroundArrayList = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,  playgroundArrayList);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Playground playground = dataSnapshot.getValue(Playground.class);
                playgroundArrayList.add(playground);
                mlistView.setAdapter(arrayAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Playground playground = dataSnapshot.getValue(Playground.class);
                playgroundArrayList.add(playground);
                mlistView.setAdapter(arrayAdapter);
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
