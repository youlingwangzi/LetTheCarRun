package cn.edu.lnu.letthecarrun;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //HC05蓝牙小车使用的UUID，以确保可以和小车正确匹配链接
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ImageButton upButton, leftButton, rightButton, downButton, leftDownButton, rightDownButton;
    private ImageButton stopButton;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice device;
    String TAG = "client";
    TextView output;

    private BluetoothSocket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upButton = (ImageButton) findViewById(R.id.upButton);
        leftButton = (ImageButton) findViewById(R.id.LeftUpButton);
        rightButton = (ImageButton) findViewById(R.id.RightUpButton);
        downButton = (ImageButton) findViewById(R.id.downButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        leftDownButton = (ImageButton) findViewById(R.id.LeftDownButton);
        rightDownButton = (ImageButton) findViewById(R.id.RightDownButton);

        //output textview
        output = (TextView) findViewById(R.id.textView1);
        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            output.append("No bluetooth device.\n");
        }
        Log.v(TAG, "bluetooth");
    }

    public void onClickConn(View view) {
        querypaired();
    }

    public void onClickUp(View view) {
        output.append("Starting client\n");
        startUp();
    }

    public void onClickDown(View view) {
        output.append("Starting client\n");
        startDown();

    }

    public void onClickLeft(View view) {
        output.append("Starting client\n");
        startLeft();

    }

    public void onClickRightDown(View view) {
        output.append("Starting client\n");
        startRightDown();
    }



    public void onClickLeftDown(View view) {
        output.append("Starting client\n");
        startLeftDown();

    }

    public void onClickRight(View view) {
        output.append("Starting client\n");
        startRight();
    }

    public void onClickStop(View view) {
        output.append("Starting client\n");
        startStop();

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            output.append(msg.getData().getString("msg"));
            return true;
        }

    });

    //寻找附近可配对的所有蓝牙设备
    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            output.append("at least 1 paired device\n");
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String[] items = new String[blueDev.length];
            int i = 0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                items[i] = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                output.append("Device: " + items[i] + "\n");
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                i++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose Bluetooth:");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    if (item >= 0 && item < blueDev.length) {
                        device = blueDev[item];
                        try {
                            socket = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
                        } catch (IOException e) {
                            Log.d("nihao","nihao");
                        }
                        mBluetoothAdapter.cancelDiscovery();
                        // Make a connection to the BluetoothSocket
                        try {
                            // This is a blocking call and will only return on a
                            // successful connection or an exception
                            socket.connect();
                        } catch (IOException e) {
                            try {
                                socket.close();
                                socket = null;
                            } catch (IOException e2) {
                                //mkmsg("unable to close() socket during connection failure: "+e2.getMessage()+"\n");
                                socket = null;
                            }
                            // Start the service over to restart listening mode
                        }
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    public void startUp() {
        if (device != null) {
            new Thread(new ConnectThreadUp(device, handler, mBluetoothAdapter,socket)).start();
        }
    }

    public void startDown() {
        if (device != null) {
            new Thread(new ConnectThreadDown(device, handler, mBluetoothAdapter,socket)).start();
        }
    }

    public void startLeft() {
        if (device != null) {
            new Thread(new ConnectThreadLeft(device, handler, mBluetoothAdapter,socket)).start();
        }
    }

    public void startRight() {
        if (device != null) {
            new Thread(new ConnectThreadRight(device, handler, mBluetoothAdapter,socket)).start();
        }
    }

    public void startStop() {
        if (device != null) {
            new Thread(new ConnectThreadStop(device, handler, mBluetoothAdapter,socket)).start();
        }
    }


    public void startLeftDown() {
        if (device != null) {
            new Thread(new ConnectThreadLeftDown(device, handler, mBluetoothAdapter,socket)).start();
        }
    }

    public void startRightDown() {
        if (device != null) {
            new Thread(new ConnectThreadRightDown(device, handler, mBluetoothAdapter,socket)).start();
        }
    }
}
