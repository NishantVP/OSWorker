package com.distributedworker.nishant.www.osworker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends AppCompatActivity {

    private String myParseObjID;
    private String myUserName;
    private String serverIP;
    private String serverPort;
    private TextView userName;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("MainActivity ", "Started");

        userName = (TextView)findViewById(R.id.userName);

        sharedpreferences = getSharedPreferences("OSWorkerMyPREFERENCES", MODE_PRIVATE);
        // Reading from SharedPreferences
        myParseObjID = sharedpreferences.getString("MY_PARSE_OBJ_ID", "");
        Log.d("MainActivity ", myParseObjID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ClientUsers");
        query.getInBackground(myParseObjID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    myUserName = object.getString("Name");
                    userName.setText(myUserName);
                    Log.d("MainActivity ", "updatedUserName");
                } else {
                    // something went wrong
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity ", "Floating Button Clicked");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Toast.makeText(MainActivity.this, "Initiating connection with Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startSocketClicked(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ServerIP");
        query.whereEqualTo("Running", true);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("MainActivity ", "The getFirst request failed.");
                } else {
                    Log.d("MainActivity ", "Retrieved the object.");
                    serverIP = object.getString("IP");
                    serverPort = object.getString("PORT");
                    Log.d("MainActivity IP ",serverIP );
                    Log.d("MainActivity Port ",serverPort);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("SERVER_IP", serverIP);
                    editor.putString("SERVER_PORT", serverPort);
                    editor.commit();

                    // use this to start and trigger a service
                    Intent i= new Intent(MainActivity.this, SocketService.class);
                    // potentially add data to the intent
                    i.putExtra("KEY1", "Value to be used by the service");
                    MainActivity.this.startService(i);
                }
            }
        });




    }

}
