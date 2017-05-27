package com.tpanpm.wwsis.placzabaw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

// import static com.tpanpm.wwsis.placzabaw.MainActivity.REQUEST_IMAGE_CAPTURE;

public class AddPlayground extends AppCompatActivity {

    EditText playGroundName, playGroundComment;
    RatingBar rate;
    ImageButton imageButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_playground);
        playGroundName = (EditText) findViewById(R.id.editText_playground_name);
        rate = (RatingBar) findViewById(R.id.ratingBar);
        imageButton = (ImageButton) findViewById(R.id.imageButton_add_playground_image);
        playGroundComment = (EditText) findViewById(R.id.editText_playground_comment);

        Intent intent = getIntent();
        Double message = intent.getDoubleExtra(MainActivity.EXTRA_MESSAGE, 0);

        Toast.makeText(this, String.valueOf(message), Toast.LENGTH_SHORT).show();
    }

    // Obsluga aparatu

        public void addPhoto (View view){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageButton.setImageBitmap(imageBitmap);
            }
        }
    }

    // Obsluga lokalizacji

        public void addNewPlayground (View view){
            Playground playground = new Playground(42.0, 17.0,
                    String.valueOf(playGroundName.getText()),
                    "ghvhg",
                    rate.getRating());
        }
}