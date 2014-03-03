package com.sacherus.partynow.activities;
 
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sacherus.partynow.R;
 
public class GeoCoderActivity extends Activity {
 
    private Button myLocation;
    private TextView myAddress;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         
        myLocation= (Button) findViewById(R.id.location);
        myAddress = (TextView)findViewById(R.id.address);
     
        myLocation.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getMyLocationAddress();
            }
        });
         
    }
 
    public void getMyLocationAddress() {
         
        Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);
         
        try {
               
              //Place your latitude and longitude
              List<Address> addresses = geocoder.getFromLocation(37.423247,-122.085469, 1);
              
              if(addresses != null) {
               
                  Address fetchedAddress = addresses.get(0);
                  StringBuilder strAddress = new StringBuilder();
                
                  for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                  }
                
                  myAddress.setText("I am at: " +strAddress.toString());
               
              }
               
              else
                  myAddress.setText("No location found..!");
          
        } 
        catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
                 Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
        }
    }
     
}