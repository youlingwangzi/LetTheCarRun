package cn.edu.lnu.letthecarrun;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by yuanx on 2016/7/13.
 */

public class ConnectThreadStop extends Thread {
    private BluetoothSocket socket;
    private final BluetoothDevice mmDevice;
    BluetoothAdapter mBluetoothAdapter =null;
    Handler handler = null;

    public ConnectThreadStop(BluetoothDevice device,Handler handler, BluetoothAdapter mBluetoothAdapter,BluetoothSocket socket) {
        mmDevice = device;
        BluetoothSocket tmp = null;
        this.handler = handler;
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.socket =socket;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
/*        try {
            tmp = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
        } catch (IOException e) {
            mkmsg("Client connection failed: "+e.getMessage()+"\n");
        }
        socket = tmp;*/

    }

    public void run() {
        mkmsg("Client running\n");
        // Always cancel discovery because it will slow down a connection
        mBluetoothAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
/*        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            socket.connect();
        } catch (IOException e) {
            mkmsg("Connect failed\n");
            try {
                socket.close();
                socket = null;
            } catch (IOException e2) {
                mkmsg("unable to close() socket during connection failure: "+e2.getMessage()+"\n");
                socket = null;
            }
            // Start the service over to restart listening mode
        }*/
        // If a connection was accepted
        if (socket != null) {
            mkmsg("Connection made\n");
            mkmsg("Remote device address: "+socket.getRemoteDevice().getAddress()+"\n");
            //Note this is copied from the TCPdemo code.
            try {
                PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                mkmsg("Attempting to send message ...\n");
                out.print((char)0x03);
                out.flush();
                mkmsg("Message sent...\n");

                mkmsg("Attempting to receive a message ...\n");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = in.readLine();
                mkmsg("received a message:\n" + str+"\n");



                mkmsg("We are done, closing connection\n");
            } catch(Exception e) {
                mkmsg("Error happened sending/receiving\n");

            } finally {
/*                try {
                    socket.close();
                } catch (IOException e) {
                    mkmsg("Unable to close socket"+e.getMessage()+"\n");
                }*/
            }
        } else {
            mkmsg("Made connection, but socket is null\n");
        }
        mkmsg("Client ending \n");

    }


    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            mkmsg( "close() of connect socket failed: "+e.getMessage() +"\n");
        }
    }


    public void mkmsg(String str) {
        //handler junk, because thread can't update screen!
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}