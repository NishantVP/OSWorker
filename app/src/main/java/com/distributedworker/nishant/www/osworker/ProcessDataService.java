package com.distributedworker.nishant.www.osworker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProcessDataService extends Service {
    String ip = "nothingYet";
    private Socket client;
    private PrintWriter printwriter;

    private String MyPort;
    private int MyPortInt;
    private SharedPreferences sharedpreferences;
    private String serverIP;
    private String serverPort;
    private String ParseObjID;


    //private DataInputStream in;
    //private DataOutputStream out;
    public static final String ACTION_BROADCAST = SocketService.class.getName() + "Broadcast";

    public ProcessDataService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        Toast.makeText(this, "This is from ProcessDataService", Toast.LENGTH_LONG).show();

        sharedpreferences = getSharedPreferences("OSWorkerMyPREFERENCES", MODE_PRIVATE);
        // Reading from SharedPreferences
        MyPort = sharedpreferences.getString("MY_PORT_FOR_DATA", "");
        serverIP = sharedpreferences.getString("SERVER_IP", "");
        serverPort = sharedpreferences.getString("SERVER_PORT", "");
        ParseObjID = sharedpreferences.getString("MY_PARSE_OBJ_ID", "");

        Log.d("SocketService IP ", MyPort);
        MyPortInt = Integer.parseInt(MyPort);
        //ip = RunSocketClient();
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute();



        return Service.START_NOT_STICKY;
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {

                client = new Socket(serverIP, MyPortInt);  //connect to server
                Log.d("ClientApp", "Started");

                printwriter = new PrintWriter(client.getOutputStream(),true);

                // receiving from server ( receiveRead  object)
                InputStream istream = client.getInputStream();
                BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

                String receiveMessage;
//                String sendMessage = "This is New from Android";
//
//                printwriter.println(sendMessage);       // sending to server
//                printwriter.flush();                    // flush the data


                String filename = "myfile.txt";
                FileOutputStream outputStream;
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE| MODE_APPEND);//| MODE_APPEND

                String ReceivedFile="";
                while(true)
                {
                    if((receiveMessage = receiveRead.readLine()) != null) {
                        System.out.println(receiveMessage);
                        if(receiveMessage.equals("---fileSendingFinishedByServer---")){
                            System.out.println("End");
                            break;
                        }
                        ReceivedFile = ReceivedFile + receiveMessage;
                        //outputStream.write(receiveMessage.getBytes());
                        //outputStream.write("\n Hello from Nishant \n".getBytes());

                        //System.out.println("From PC - " + receiveMessage); // displaying at DOS prompt

                    }
                }
                outputStream.write(ReceivedFile.getBytes());
                outputStream.close();
                System.out.println("Out of while");
                System.out.println("Received File - " +ReceivedFile);

                //Read the gile just saved
                System.out.println("File open");

                // OPENING THE REQUIRED TEXT FILE
//                BufferedReader reader = new BufferedReader(new InputStreamReader(
//                        getAssets().open("myfile.txt")));
//
//                String myLine = reader.readLine();


                double count = 0.0;
                long count2 = ReceivedFile.length();
                System.out.println("String Length " +ReceivedFile.length());
//                FileInputStream fin = openFileInput(filename);
//                int c;
//                String temp="";
//                while( (c = fin.read()) != -1){
//                    temp = temp + Character.toString((char)c);
//                    count = count + 0.001;
//                    //count2++;
//                    //System.out.print(temp);
//
//
//                }
//                //string temp contains all the data of the file.
//                fin.close();

                System.out.println("Ready to send");

                PrintWriter printwriter;
                printwriter = new PrintWriter(client.getOutputStream(),true);
                printwriter.println(count2);       // sending to server
                printwriter.flush();                    // flush the data
                System.out.println("File sent: " + count2);

                String CountToUpdateinView = Integer.toString((int)count2);
                sendBroadcastMessage(CountToUpdateinView);


                return "done";
            } catch (IOException e) {
                return "Not done";
            }
            finally {
//                try {
//                    //client.close();   //closing the connection
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

        }
    }

    private void sendBroadcastMessage(String messageFromPC) {

        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra("Message", messageFromPC);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
