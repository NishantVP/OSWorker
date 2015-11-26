package com.distributedworker.nishant.www.osworker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.ParseException;

public class StartActivity extends AppCompatActivity {

    private EditText userName;
    private String UserNameString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Parse Test
        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("foo", "bar2");
        //testObject.saveInBackground();

        userName = (EditText) findViewById(R.id.userNameEditText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Manually Save Server IP
        //final ParseObject ClientUsers = new ParseObject("ServerIP");
        //ClientUsers.put("IP", "192.168.2.13");
        //ClientUsers.put("PORT", "4444");
        //ClientUsers.put("Running", true);
        //ClientUsers.saveInBackground();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    public void enterAppClicked(View view)
    {
        Log.d("StartActivity ", "Enter App Clicked");
        UserNameString = userName.getText().toString();

        final ParseObject ClientUsers = new ParseObject("ClientUsers");
        ClientUsers.put("Name", UserNameString);
        ClientUsers.put("Ready", false);
        ClientUsers.put("Running", false);

        ClientUsers.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d("StartActivity ", "User update saved!");
                    String id = ClientUsers.getObjectId();
                    Log.d("StartActivity ", "The object id is: " + id);

                    SharedPreferences sharedpreferences = getSharedPreferences(
                            "OSWorkerMyPREFERENCES", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("MY_PARSE_OBJ_ID", id);
                    editor.commit();

                    Log.d("StartActivity ", "Parse Saved");

                    Toast.makeText(StartActivity.this, "Welcome " + UserNameString, Toast.LENGTH_SHORT).show();

                    // use this to start and trigger a service
                    Intent i = new Intent(StartActivity.this, MainActivity.class);
                    // potentially add data to the intent
                    i.putExtra("KEY1", UserNameString);
                    StartActivity.this.startActivity(i);

                } else {
                    // The save failed.
                    Log.d("StartActivity ", "User update error: " + e);
                }
            }

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
