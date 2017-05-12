package com.tpanpm.wwsis.placzabaw;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Console;

/**
 * Created by Tomasz Zajc on 2017-04-22.
 */

public class Playground {
    String markerId;
    Double playgroundLat;
    Double playgroundLong;
    String Lat;
    String Long;
    String name;
    String description;
    float rate;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

 public Playground(){
  //Def constructor
 }
 public Playground(Double playgroundLat, Double playgroundLong, String name, String description, float rate){
  this.markerId = markerId;
  this.playgroundLat = playgroundLat;
  this.playgroundLong = playgroundLong;
  this.name = name;
  this.description = description;
  this.rate = rate;

 }

 public void addPlayGround(Playground playground){

     /*Create key for playground (char('.') is invalid for Firebase child)*/
     String keyPlayground = new String();
     keyPlayground =  String.valueOf(playground.playgroundLat).replace(".","-") + "_" +
                      String.valueOf(playground.playgroundLong).replace(".","-");


     DatabaseReference myRefKey = database.getReference("playgrounds");
     DatabaseReference myRefObject = myRefKey.child(keyPlayground);

     myRefObject.child("NAME").setValue(playground.name);
     myRefObject.child("DESC").setValue(playground.description);
     myRefObject.child("LAT").setValue(playground.playgroundLat);
     myRefObject.child("LON").setValue(playground.playgroundLong);
     myRefObject.child("RATE").setValue(playground.rate);


 }


}
