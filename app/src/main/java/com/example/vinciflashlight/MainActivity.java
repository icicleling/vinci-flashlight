package com.example.vinciflashlight;

import static android.hardware.Sensor.TYPE_LIGHT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvLxNum;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorEventListener;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLxNum = findViewById(R.id.lx_num);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // light sensor
        lightSensor = sensorManager.getDefaultSensor(TYPE_LIGHT);
        if (lightSensor == null) {
            Toast.makeText(this, "No Light Sensor", Toast.LENGTH_LONG).show();
        }

        // camera
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        lightSensorEventListener = new SensorEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lightNum = event.values[0];
                tvLxNum.setText(String.format("%s lx", lightNum));

                if (lightNum > 300) {
                    try {
                        cameraManager.setTorchMode(cameraId, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        cameraManager.setTorchMode(cameraId, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorEventListener);
    }
}