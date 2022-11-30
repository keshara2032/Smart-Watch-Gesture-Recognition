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
    private Sensor sensor_acc;
    private Sensor sensor_mag;
    private double avg = 0;
    private String acc_data;
    private String game_rotation_vector;
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
        sensor_acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_mag = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        Log.d(LOG_TAG, "Sensors" + sensor_acc);
        sensorManager.registerListener(this, sensor_acc, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensor_mag, SensorManager.SENSOR_DELAY_FASTEST);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //TODO: get values

            // Acquire measurement values from event
            double x = sensorEvent.values[0]; // X axis
            double y = sensorEvent.values[1]; // y axis
            double z = sensorEvent.values[2]; // z axis

            // Do something with the values

            // Calculate exponential average
            avg = avg * (1 - P) + x * P;

            acc_data = x +","+ y + ","+ z;
            Log.d(LOG_TAG, "acc_3_axes: " + acc_data);


        }else if (sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            //TODO: get values
            // Acquire measurement values from event
            double x = sensorEvent.values[0]; // X
            double y = sensorEvent.values[1]; // y
            double z = sensorEvent.values[2]; // z

            game_rotation_vector  = x +","+ y + ","+ z;

            Log.d(LOG_TAG, "magnetic_field: "+game_rotation_vector);

        }

        String data_to_send = acc_data+","+game_rotation_vector;
        sendSensorData(data_to_send);



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
