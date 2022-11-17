package com.example.gesturerecognition;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class UDP_Client implements Runnable
    {

        private String serverIPAddress = "172.27.162.26";
        private String message = "Hello Android!" ;
        public String Message;
        private int port = 7889;
        private static final String DEBUG_TAG = "NetworkStatusExample";
        public final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        private DatagramSocket udpSocket = null;
        private InetAddress serverAddr ;
        protected static final String LOG_TAG = "UDPClient";

        @Override
        public void run() {
            InitializeUDPClient();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    boolean is_interrupted = Thread.currentThread().isInterrupted();
                    Log.d(LOG_TAG, "Thread Interrupted: " + is_interrupted);
                    String data = queue.take();
                    //handle the data
                    // Send data to the running thread
                    byte[] buf = data.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, port);
                    try {
                        udpSocket.send(packet);
                        Log.d(LOG_TAG, "Send Data: " + data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, "Thread interrupted:" + e);
                    Thread.currentThread().interrupt();
                    udpSocket.close();
                    return;
                }
            }
        }

        public void InitializeUDPClient() {
            try {
                udpSocket = new DatagramSocket(port);
                serverAddr = InetAddress.getByName(serverIPAddress);
                Log.d(LOG_TAG, "UDP Server Address: " + serverAddr);
                udpSocket.setBroadcast(true);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }
}
