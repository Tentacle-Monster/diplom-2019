package kr.pe.burt.android.dice;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Handler h;
    byte last_btn_inp;
   // OurRender GORE;
    private static final int REQUEST_ENABLE_BT = 1;
    final int RECIEVE_MESSAGE = 1;        // Статус для Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    //private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID сервиса
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-адрес Bluetooth модуля
    private static String address = "00:18:E4:35:F3:CA";


    public static Gamespace curworld = new Gamespace();


    private OGLView oglView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        curworld.newgame();
        last_btn_inp= (byte)0b11111111;
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        oglView = (OGLView) findViewById(R.id.oglView);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        // если приняли сообщение в Handler
                        byte[] readBuf = (byte[]) msg.obj;
                        //packet= concat(readBuf1 , readBuf);
                        if(readBuf[0]==(byte)0xff&&readBuf[7]==(byte)0xff) {
                            if (readBuf[1] == (byte) 0xaa && readBuf[6] == (byte) 0xaa) {
                                byte[] bytes = new byte[4];
                                bytes[0] = readBuf[2];
                                bytes[1] = readBuf[3];
                                bytes[2] = readBuf[4];
                                bytes[3] = readBuf[5];
                                float f = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                oglView.thisrend.setXx(f);
                            }
                            if (readBuf[1] == (byte) 0xbb && readBuf[6] == (byte) 0xbb) {
                                byte[] bytes = new byte[4];
                                bytes[0] = readBuf[2];
                                bytes[1] = readBuf[3];
                                bytes[2] = readBuf[4];
                                bytes[3] = readBuf[5];
                                float f = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                oglView.thisrend.setYy(f);
                            }
                            if (readBuf[1] == (byte) 0xcc && readBuf[6] == (byte) 0xcc) {
                                byte[] bytes = new byte[4];
                                bytes[0] = readBuf[2];
                                bytes[1] = readBuf[3];
                                bytes[2] = readBuf[4];
                                bytes[3] = readBuf[5];
                                float f = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                oglView.thisrend.setZz(f);
                            }
                            if (readBuf[1] == (byte) 0xdd && readBuf[6] == (byte) 0xdd && readBuf[2]==readBuf[3]&&readBuf[4]==readBuf[5]&&readBuf[2]==readBuf[5]) {
                                byte btn_inp = readBuf[2];
                                if((btn_inp & 0b1)!=0 && (last_btn_inp & 1)==0)curworld.turn(0);
                                else if((btn_inp & 0b10)!=0 && (last_btn_inp & 0b10)==0)curworld.turn(2);
                                else if((btn_inp & 0b100)!=0 && (last_btn_inp & 0b100)==0)curworld.turn(4);
                                else if((btn_inp & 0b1000)!=0 && (last_btn_inp & 0b1000)==0)curworld.turn(5);
                                else if((btn_inp & 0b10000)!=0 && (last_btn_inp & 0b10000)==0)curworld.turn(3);
                                else if((btn_inp & 0b100000)!=0 && (last_btn_inp & 0b100000)==0)curworld.turn(1);
                                last_btn_inp = btn_inp;
                            }
                        }
                        break;
                }
            };
        };




        btAdapter = BluetoothAdapter.getDefaultAdapter();       // получаем локальный Bluetooth адаптер



    //    delayFunction({ myDelayedFunction() }, 300);

      /*  curworld.turn(4);
        curworld.turn(3);
        curworld.turn(2);
        curworld.turn(1);
*/
    }


    public void onResume() {
        super.onResume();
        oglView.onResume();
        //Log.d(TAG, "...onResume - попытка соединения...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        // Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            //   Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        //Log.d(TAG, "...Создание Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        oglView.onResume();

        //Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }




    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth не поддерживается");
        } else {
            if (btAdapter.isEnabled()) {
                //      Log.d(TAG, "...Bluetooth включен...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Получаем кол-во байт и само собщение в байтовый массив "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Отправляем в очередь сообщений Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            //Log.d(TAG, "...Данные для отправки: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                //  Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


}
