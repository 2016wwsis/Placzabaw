package com.tpanpm.wwsis.placzabaw;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KacperS on 2017-04-22.
 */

public class Playground {
    private String IMAGE;
    private double LAT;
    private double LON;
    private String NAME;
    private String DESC;
    private float RATE;
    String imagePath;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();


    private Playground(){
        //Def constructor
    }

    public Playground(String DESC, String IMAGE, double LAT, double LON, String NAME, float RATE, String imagePath, Context context){
        this.LAT = LAT;
        this.LON = LON;
        this.NAME = NAME;
        this.DESC = DESC;
        this.RATE = RATE;
        this.IMAGE = IMAGE;
        this.imagePath = imagePath;
        this.context = context;
    }


    public String getNAME() {
        return NAME;
    }

    public String getDESC() {
        return DESC;
    }

    public double getLAT() {
        return LAT;
    }

    public double getLON() {
        return LON;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public float getRATE() {
        return RATE;
    }


    public void addPlayGround(Playground playground) throws FileNotFoundException {

     /*Create key for playground (char('.') is invalid for Firebase child)*/
        String keyPlayground = new String();
        String keyPlaygroundImage = new String();
        keyPlayground =  String.valueOf(playground.getLAT()).replace(".","-") + "_" +
                String.valueOf(playground.getLON()).replace(".","-");

        keyPlaygroundImage =  keyPlayground+"_"+ currentDateToString();

        DatabaseReference myRefKey = database.getReference("playgrounds");
        DatabaseReference myRefObject = myRefKey.child(keyPlayground);

        myRefObject.child("NAME").setValue(playground.getNAME());
        myRefObject.child("DESC").setValue(playground.getDESC());
        myRefObject.child("LAT").setValue(playground.getLAT());
        myRefObject.child("LON").setValue(playground.getLON());
        myRefObject.child("RATE").setValue(playground.getRATE());

        addPlayGroundImage(playground.imagePath, keyPlaygroundImage, keyPlayground);

    }

    private void addPlayGroundImage(String imagePath, final String keyPlaygroundImage, final String keyPlayground) throws FileNotFoundException {
        Bitmap imageBitmap=null;
        File f= new File(imagePath+".jpg");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            imageBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (imageBitmap != null) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://placzabaw-625dd.appspot.com/");
        final ProgressDialog progressDialog = new ProgressDialog((context));
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        StorageReference imagesRef = storageRef.child(keyPlaygroundImage);
        InputStream stream = new FileInputStream(new File(imagePath));

        UploadTask uploadTask = imagesRef.putStream(stream);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }) .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //if the upload is successfull
                //hiding the progress dialog
                progressDialog.dismiss();

                //and displaying a success toast
                Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();
                Uri downloadUri = taskSnapshot.getDownloadUrl();  //Ignore This error

                DatabaseReference myRefKey = database.getReference("playgrounds");
                DatabaseReference myRefObject = myRefKey.child(keyPlayground);
                assert downloadUri != null;
                myRefObject.child("IMAGE").setValue(downloadUri.toString());


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //if the upload is not successfull
                //hiding the progress dialog
                progressDialog.dismiss();

                //and displaying error message

                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                @SuppressWarnings("VisibleForTests")   double progress = taskSnapshot.getBytesTransferred()/1024;
                progressDialog.setMessage("Uploaded " + ((int)progress) + " KB...");
            }
        });

    }

    String currentDateToString(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        String dateString = dateFormat.format(currentDate);
        return dateString;
    }


}