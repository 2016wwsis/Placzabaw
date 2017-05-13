package com.tpanpm.wwsis.placzabaw;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import static com.tpanpm.wwsis.placzabaw.MainActivity.REQUEST_IMAGE_CAPTURE;

public class AddPlayground extends AppCompatActivity {

    EditText playGroundName;
    RatingBar rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_add_playground);
        playGroundName = (EditText)findViewById(R.id.editText_playground_name);
        rate = (RatingBar)findViewById(R.id.ratingBar);

       Double message = intent.getDoubleExtra(MainActivity.EXTRA_MESSAGE, 0);
        Toast.makeText(this, String.valueOf(message), Toast.LENGTH_SHORT).show();
    }

    public void addPhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
}


    public void addNewPlayground(View view) {
        Playground playground = new Playground(42.0, 17.0,
               String.valueOf(playGroundName.getText()),
                "ghvhg",
                rate.getRating());
    }
}