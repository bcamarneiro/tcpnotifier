package com.rederia.beehelp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends Activity {
    public static final String TAG = "Settings Activity";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String IpAddress = "ipAddress";
    public static final String Port = "port";

    SharedPreferences sharedpreferences;
    String SERVER_IP = "192.168.2.139";
    String SERVER_PORT = "666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final TextView textIpAddress = (TextView) findViewById(R.id.textIpAddress);
        final TextView textPort = (TextView) findViewById(R.id.textPort);

        Button btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(IpAddress)){
            textIpAddress.setText(sharedpreferences.getString(IpAddress, ""));
        } else {
            textIpAddress.setText("127.0.0.1");
        }

        if (sharedpreferences.contains(Port)){
            textPort.setText(sharedpreferences.getString(Port, ""));
        } else {
            textPort.setText("6000");
        }

        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Port, textPort.getText().toString());
                editor.putString(IpAddress, textIpAddress.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "Settings saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
