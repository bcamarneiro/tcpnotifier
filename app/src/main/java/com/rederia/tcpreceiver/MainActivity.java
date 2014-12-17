package com.rederia.tcpreceiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MainActivity extends Activity {
    public static final String TAG = "Main Activity";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String IpAddress = "ipAddress";
    public static final String Port = "port";

    SharedPreferences sharedpreferences;
    String SERVER_IP = "127.0.0.1";
    String SERVER_PORT = "6000";
    NotificationManager manager;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    ListView listview;
    TextView textState;
    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // use this to start and trigger a service
        Intent i= new Intent(this.getApplicationContext(), TcpSocketService.class);
        // potentially add data to the intent
        i.putExtra("IPADDRESS", "127.0.0.01");
        i.putExtra("PORT", "12345");
        this.getApplicationContext().startService(i);
        */

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(IpAddress)){
            SERVER_IP = sharedpreferences.getString(IpAddress, "");
        }
        if (sharedpreferences.contains(Port)){
            SERVER_PORT = sharedpreferences.getString(Port, "");
        }

        textState = (TextView) findViewById(R.id.textState);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = listItems.get(position);
                listItems.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        connectToServer();
    }

    public void connectToServer(){
        Log.d(TAG, "Create MyClientTask");
        MyClientTask myClientTask = new MyClientTask(SERVER_IP, Integer.parseInt(SERVER_PORT));
        Log.d(TAG, "Execute MyClientTask");
        myClientTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_connect){
            connectToServer();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyClientTask extends AsyncTask<Void, String, Void> {
        public static final String TAG = "MyClientTask";

        String dstAddress;
        int dstPort;

        MyClientTask(String addr, int port) {
            Log.d(TAG, "Constructor");
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d(TAG, "doInBackground");
            publishProgress("&&connecting");
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                Log.d(TAG, "1");

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                Log.d(TAG, "2");

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                Log.d(TAG, "3");

                publishProgress("&&connected");

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.reset();
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    Log.i(TAG, byteArrayOutputStream.toString("UTF-8"));
                    publishProgress(byteArrayOutputStream.toString("UTF-8"));
                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown Host");
                publishProgress("&&notconnected");
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                publishProgress("&&notconnected");
                e.printStackTrace();
            } catch (IOException e) {
                publishProgress("&&notconnected");
                Log.d(TAG, "Error while creating socket");
                e.printStackTrace();
            } finally {
                publishProgress("&&notconnected");
                if (socket != null) {
                    try {
                        Log.d(TAG, "Close socket");
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d(TAG, values[0]);
            if(values[0] == "&&notconnected"){
                textState.setText("Not connected");
            } else if(values[0] == "&&connecting"){
                textState.setText("Connecting");
            } else if(values[0] == "&&connected"){
                textState.setText("Connected");
            } else {
                adapter.addAll(values);
                Notification notification = new Notification(R.drawable.ic_launcher, "Notification Received", System.currentTimeMillis());
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, getIntent(), 0);
                notification.setLatestEventInfo(getApplicationContext(), "Notification Received", values[0], contentIntent);
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                manager.notify(0, notification);
            }

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
}
