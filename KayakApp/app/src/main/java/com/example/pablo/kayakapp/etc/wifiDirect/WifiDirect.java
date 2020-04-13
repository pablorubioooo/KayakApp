package com.example.pablo.kayakapp.etc.wifiDirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pablo.kayakapp.R;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WifiDirect extends AppCompatActivity {

    String path, movilName, fileName;
    File  pathWifiDirect;
    Button btnDiscover;
    ListView listView;
    TextView connectionStatus;
    TextView writeMsg;
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String [] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    ServerClass serverClass;
    ClientClass clientClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        path = getIntent().getStringExtra("path");
        fileName = getIntent().getStringExtra("name");
        File tarjeta = Environment.getExternalStorageDirectory();
        pathWifiDirect =  new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/Compartidos/"));

        if(!pathWifiDirect.exists()){
            pathWifiDirect.mkdir();
        }

        btnDiscover = (Button) findViewById(R.id.discover);
        listView = (ListView) findViewById(R.id.peerListView);
        connectionStatus = (TextView) findViewById(R.id.writeMsg);
        writeMsg = (TextView) findViewById(R.id.writeMsg);

        listView.setBackgroundColor(Color.rgb(29,51,74));

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionStatus.setText("Discovery Starting Failed");
                    }
                });
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);

        mReceiver = new WifiDirectBroadcastReceiver(mManager,mChannel,this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connected to "+device.deviceName,Toast.LENGTH_SHORT).show();
                        movilName=device.deviceName;
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(),"ERROR. Not connected",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray=new String[peerList.getDeviceList().size()];
                deviceArray=new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peerList.getDeviceList()){
                    deviceNameArray[index]=device.deviceName;
                    deviceArray[index]=device;
                    index++;
                }

                ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray);
                listView.setAdapter(adapter);
                if(peers.size()==0){
                    Toast.makeText(getApplicationContext(),"No device found",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress=wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                connectionStatus.setText("Host");
                serverClass=new ServerClass();
                serverClass.start();
                System.out.println("SERVER INICIADO");
            }
            else if(wifiP2pInfo.groupFormed){
                connectionStatus.setText("Client");
                clientClass=new ClientClass(groupOwnerAddress);
                clientClass.start();
                System.out.println("CLIENTE INICIADO");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run(){
            File f = null;
            try {
                serverSocket=new ServerSocket(8888);
                System.out.println("ANTES DE ACEPTAR EL SERVER SOCKET");
                socket=serverSocket.accept();
                System.out.println("DESPUES DE ACEPTAR EL SERVER SOCKET");
                String [] fn = fileName.split("/");
                f = new File(pathWifiDirect+"/"+fn[1]);
                System.out.println("....... "+ f);
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                String inputData = "";
                byte[] buffer = new byte[4096];
                int bytesRead;

                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                while(true)
                {
                    bytesRead = is.read(buffer, 0, buffer.length);
                    System.out.println(bytesRead+" DATACLIENT");
                    if(bytesRead == -1)
                    {
                        break;
                    }
                    bos.write(buffer, 0, bytesRead);
                    bos.flush();

                }

                bos.close();
                socket.close();
                serverSocket.close();


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage()+ "  ********");
            }
            System.out.println("RECIBIDO");
        }
    }


    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public  ClientClass(InetAddress hostAddress){
            hostAdd=hostAddress.getHostAddress();
            socket=new Socket();
            System.out.println("CLIENT CONSTRUCTOR");
        }

        @Override
        public void run(){
            try {
                //socket.bind(null);
                System.out.println("ANTES DE CONECTAR EL CLIENT SOCKET");
                socket.connect(new InetSocketAddress(hostAdd,8888),5000);
                System.out.println("DESPUES DE CONECTAR EL CLIENT SOCKET");
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(outputStream);
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                byte[] buffer = new byte[4096];

                FileInputStream fis = new FileInputStream(path);
                BufferedInputStream bis = new BufferedInputStream(fis);

                while(true)
                {

                    int bytesRead = bis.read(buffer, 0, buffer.length);
                    System.out.println(bytesRead+" SERVERCLEINT");

                    if(bytesRead == -1)
                    {
                        break;
                    }
                    outputStream.write(buffer,0, bytesRead);
                    outputStream.flush();
                }



                fis.close();
                bis.close();

                br.close();
                isr.close();
                is.close();

                pw.close();
                outputStream.close();

                socket.close();

                System.out.println("FILE TRANSFER COMPLETE");
                //Toast.makeText(getApplicationContext(),"Enviado",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }



}

