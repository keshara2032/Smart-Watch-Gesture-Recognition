package com.example.gesturerecognition;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SensorData  implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private double avg = 0;
    private String acc_data;
    private static final double P = 0.7;
    protected static final String LOG_TAG = "SensorData";
    private Context context;
    public static UDP_Client udp_client;
    private  boolean isStarted = false;
    private Thread udp_thread;

    public  void startSensor(){
        if(!isStarted){
        udp_client = new UDP_Client();
        Log.d(LOG_TAG, "UDP Client" + udp_client);
         udp_thread = new Thread(udp_client);
        udp_thread.start();
        isStarted = true;
        }
    }

    public  void stopSensor(){
        if(isStarted){
            udp_thread.interrupt();
            isStarted = false;
        }
    }
    public void sendSensorData(String data){
        if(isStarted) {
            udp_client.queue.offer(data);
        }
    }

    public SensorData(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(LOG_TAG, "Sensors" + sensor);
        sensorManager.registerListener(this,
                sensor, SensorManager.SENSOR_DELAY_FASTEST);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Acquire measurement values from event
        double x = sensorEvent.values[0]; // X axis
        double y = sensorEvent.values[1]; // y axis
        double z = sensorEvent.values[2]; // z axis

        // Do something with the values

        // Calculate exponential average
        avg = avg * (1 - P) + x * P;

        acc_data = Double.toString(x) +","+Double.toString(y) + ","+Double.toString(z);
        Log.d(LOG_TAG, "acc_3_axes: " + acc_data);

        sendSensorData(acc_data);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
