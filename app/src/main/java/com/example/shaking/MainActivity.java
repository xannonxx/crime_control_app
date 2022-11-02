package com.example.shaking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textView,lati,longi;
    private float accval;
    private float acclast;
    FusedLocationProviderClient fusedLocationProviderClient;
    public float Sensitivity= MainActivity.shake;
    private static float shake;
    List<Address> addresses;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lati = findViewById(R.id.lati);
        longi = findViewById(R.id.longi);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager != null){

            Sensor acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


            if(acclerometer != null){
                sensorManager.registerListener(this, acclerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }

            accval=SensorManager.GRAVITY_EARTH;
            acclast=SensorManager.GRAVITY_EARTH;
            shake=0.00f;

        }else{
            Toast.makeText(this,"SEnsor service not detected",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            textView = findViewById(R.id.textview);

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            if(Sensitivity<=13) Sensitivity=14;
            acclast=accval;
            accval=(float) Math.sqrt((double) (x*x+y*y+z*z));
            float delta=accval-acclast;
            shake=shake*.9f + delta;
            if(shake>Sensitivity)
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("8088157008",null,"Danger..",null,null);
                Toast.makeText(this,"next..",Toast.LENGTH_LONG);
                StringBuffer latilongi = getLastLocation();

                StringBuffer smsBody = new StringBuffer();
                smsBody.append("http://maps.google.com?q=");
                smsBody.append(latilongi);


                String s = String.valueOf(shake);
                String s1 = String.valueOf(delta);
                String s2 = String.valueOf(Sensitivity);
                textView.setText("x : "+sensorEvent.values[0]+",y : "+sensorEvent.values[1]+", z : "+sensorEvent.values[2]);
                Toast.makeText(getApplicationContext(),"value of shake is "+s+" and delta is "+s1+" with sensitivity "+s2,Toast.LENGTH_LONG).show();

//                smsManager.sendTextMessage("8088157008",null,smsBody.toString(),null,null);
                shake = 0;

            }
        }

    }

    private StringBuffer getLastLocation() {
        StringBuffer ans = new StringBuffer();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    ans.append(""+addresses.get(0).getLatitude());
                                    ans.append(',');
                                    ans.append(""+addresses.get(0).getLongitude());
                                    lati.setText(""+addresses.get(0).getLatitude());
                                    longi.setText(""+addresses.get(0).getLongitude());

                                    SmsManager smsManager = SmsManager.getDefault();
                                    StringBuffer smsBody = new StringBuffer();
                                    smsBody.append("http://maps.google.com?q=");
                                    smsBody.append(ans);
                                    smsManager.sendTextMessage("xxxxxxxxxx",null,smsBody.toString(),null,null);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });

        }else{

            askPermission();

        }

        return ans;
    }

    private void askPermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else{
                Toast.makeText(this,"Required Permission",Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}