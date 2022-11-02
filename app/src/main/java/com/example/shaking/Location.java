package com.example.shaking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Location extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

