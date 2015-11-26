package com.distributedworker.nishant.www.osworker;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketService extends Service {
    String ip = "nothingYet";
    private Socket client;
    private PrintWriter printwriter;

    private String serverIP;
    private String serverPort;
    private SharedPreferences sharedpreferences;

    //private DataInputStream in;
    //private DataOutputStream out;

    public SocketService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        Toast.makeText(this, "This is from service", Toast.LENGTH_LONG).show();

        sharedpreferences = getSharedPreferences("OSWorkerMyPREFERENCES", MODE_PRIVATE);
        // Reading from SharedPreferences
        serverIP = sharedpreferences.getString("SERVER_IP", "");

        //ip = RunSocketClient();
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute();



        return Service.START_NOT_STICKY;
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {

                client = new Socket("10.0.0.7", 4444);  //connect to server
                Log.d("ClientApp", "Started");

                //in = new DataInputStream(client.getInputStream()); // READ FROM SERVER
                //out = new DataOutputStream(client.getOutputStream()); // WRITE TO SERVER

                //String s= "This is new from Android";
                //out.writeUTF(s);

                //String FromServer = "server says: "+in.readUTF();
                //Log.d("ClientApp:From Server: ", FromServer);



                printwriter = new PrintWriter(client.getOutputStream(),true);

                // receiving from server ( receiveRead  object)
                InputStream istream = client.getInputStream();
                BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

                String receiveMessage;
                String sendMessage = "This is New from Android";

                printwriter.println(sendMessage);       // sending to server
                printwriter.flush();                    // flush the data

                while(true)
                {
                    if((receiveMessage = receiveRead.readLine()) != null) //receive from server
                    {
                        System.out.println("From PC - " +receiveMessage); // displaying at DOS prompt
                    }
                }


                //printwriter.write("This is from Android");  //write the message to output stream
                //printwriter.flush();
                //printwriter.close();


            } catch (IOException e) {
                return "Not done";
            }
            finally {
                try {
                    client.close();   //closing the connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String RunSocketClient() {
        try {
            Socket clnt = new Socket("localhost", 4444);
            BufferedReader in = new BufferedReader(new InputStreamReader(clnt.getInputStream()));

            String fromServer;
            fromServer = in.readLine();

            return fromServer;
        } catch (IOException e) {
            return "nothing";
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
