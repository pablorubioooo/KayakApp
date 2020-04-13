package com.example.pablo.kayakapp.training;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pablo.kayakapp.etc.music.SoundManager;
import com.example.pablo.kayakapp.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Training extends AppCompatActivity{

    private TextView mTimer, mVelocity, mStrokes, mDistanceT, mInfo, mTimerFin;
    private Button mBtnPlay, mBtnPause, mBtnStop;

    double x0 , y0 , distanceTotal ;
    //double Radio = 6371000;  // EN METROS
    double Radio = 6370; //EN KILOMETROS
    String lat1, lon1;
    Context mContext;
    Background mBackground;
    Thread mThreadBackground;
    private boolean pauseFlag = false;
    long lastUpdate, lastUpdate2, lastUpdateGPS;
    Intent mServiceIntent = null;
    MyService mService;
    boolean mServiceBound = false;
    String [] data;
    MyLocationListener mLL = null;
    String timeResIni;
    int datas;
    int secI, minI, horI, milI; int horR, minR, secR, milR;
    int numSer, numBlo, desSer, desBlo, desSerS, desBloS;
    String entreno;
    SoundManager sound; int beep;
    public OutputStreamWriter fout;
    File file;
    String infoTraining, infoBlock;
    int firstTime=0;

    public Context getCtx() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_training);

        mContext = this;

        mTimer = (TextView) findViewById(R.id.Timer);
        mVelocity = (TextView) findViewById(R.id.Velocity);
        mStrokes = (TextView) findViewById(R.id.Strokes);
        mDistanceT = (TextView) findViewById(R.id.DistanceT);
        mInfo = (TextView) findViewById(R.id.Info);
        mTimerFin = (TextView) findViewById(R.id.TimerFin);
        mBtnPlay = (Button) findViewById(R.id.BtnPlay);
        mBtnPause = (Button) findViewById(R.id.BtnPause);
        mBtnStop = (Button) findViewById(R.id.BtnStop);

        mBtnPause.setEnabled(false);
        mBtnStop.setEnabled(false);
        x0 = y0 = distanceTotal = 0.0;

        sound = new SoundManager(getApplicationContext());
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        beep = sound.load(R.raw.bip);

        datas = getIntent().getIntExtra("datas",0);
        if(datas == 1){
            data = getIntent().getStringArrayExtra("DataFolder");
            minI = Integer.parseInt(data[7]);
            secI = Integer.parseInt(data[8]);
            numBlo = Integer.parseInt(data[3]);
            desBlo = Integer.parseInt(data[4]);
            numSer = Integer.parseInt(data[5]);
            desSer = Integer.parseInt(data[6]);
            desSerS = Integer.parseInt(data[10]);
            desBloS = Integer.parseInt(data[9]);
            horR=horI; minR=minI; secR=secI; milR=milI;
            timeResIni = String.format("%02d:%02d:%02d",0,minI,secI);
            mTimerFin.setText(timeResIni);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }

        locationStart();

        lastUpdateGPS = lastUpdate = System.currentTimeMillis();

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBackground == null) {
                    startTimer(); //cuenta atras
                    mBackground = new Background(mContext);
                    mThreadBackground = new Thread(mBackground);
                    entreno = "serie"; numSer--;
                    mLL = new MyLocationListener(mContext);
                    lastUpdate2 = System.currentTimeMillis();
                    mThreadBackground.start();
                    mBackground.start();
                    mBtnPause.setEnabled(true);
                    mBtnPlay.setEnabled(false);
                    mBtnStop.setEnabled(true);
                    startBackgorundService();
                }
            }
        });


        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mBackground.mIsRunning){
                        if (pauseFlag == false) {
                            pauseFlag = true;
                            mBackground.pauseThread();
                            mBtnPause.setText("Resume");
                        } else {
                            pauseFlag = false;
                            mBackground.resumeThread();
                            mBtnPause.setText("Pause");
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBackground !=null){
                    alertMessage();
                }
            }
        });

        File tarjeta = Environment.getExternalStorageDirectory();
        String date = calcDate();
        File dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"+date));

        if(!dir.exists()){
            dir.mkdir();
        }

        file = new File(dir, "Training "+date+".txt");

        try {
            if(!file.exists()){
                file.createNewFile();
            }
            else{
                int random = (int) (Math.random()*1000+1);
                file = new File(dir, "Training "+date+" "+random+".txt");
                file.createNewFile();
            }

            fout = new OutputStreamWriter(new FileOutputStream(file));

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error al crear el fichero del entrenamiento. No se guardarán los datos.", Toast.LENGTH_SHORT).show();
        }

    }

    private void startTimer() {
        for(int i=3;i>0;i--) {
            feedback("short");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        feedback("triple");
    }

    private String calcDate() {
        Calendar fecha = new GregorianCalendar();

        int annio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH)+1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

        return ""+dia+"-"+mes+"-"+annio;
    }

    private void locationStart() {

        LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        MyLocationListener mLocal = new MyLocationListener(this);

        final boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) mLocal);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mLocal);

        Toast.makeText(this, "GPS OK", Toast.LENGTH_SHORT).show();

    }


    private void startBackgorundService() {
        mServiceIntent = new Intent(getCtx(), MyService.class);
        startService(mServiceIntent);
        bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private void finishBackground() {
        //mBackground.unregisterSensors();
        mBackground.stop();
        mThreadBackground.interrupt();
        mThreadBackground = null;
        mBackground = null;
        mLL = null; //comprobar si ok
        finish();
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Toast.makeText(mContext, "Training saved.",
                                Toast.LENGTH_SHORT).show();
                        try {
                            fout.flush();
                            fout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        finishBackground();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        //Toast.makeText(mContext, "DataFolder not saved.", Toast.LENGTH_SHORT).show();
                        try {
                            fout.flush();
                            fout.close();
                            file.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        finishBackground();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save Training?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }



    public void updateCrono(final int s, final int m, final int h, final int ml){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String timeTexto = String.format("%02d:%02d:%02d:%03d",h,m,s,ml);
                mTimer.setText(timeTexto);

                if(datas==1) {

                    if(entreno=="serie" && numBlo<=0) {
                        try {
                            fout.flush();
                            fout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        feedback("short");feedback("large");feedback("triple");
                        finish();
                    }

                    if(horR == 0 && minR == 0 && secR == 0){
                        if(entreno=="serie" && numSer>0){
                            entreno="descanso";
                            horR=0; minR=desSer; secR=desSerS;
                            String timeResDes = String.format("%02d:%02d:%02d", horR, minR, secR);
                            mTimerFin.setText(timeResDes);
                            feedback("short");feedback("triple");
                        }
                        else if(entreno=="descanso" && numSer>0){
                            entreno = "serie"; numSer--;
                            horR=horI; minR=minI; secR=secI; milR=milI;
                            String timeResIni2 = String.format("%02d:%02d:%02d",0,minI,secI);
                            mTimerFin.setText(timeResIni2);
                            feedback("short");feedback("triple");
                        }
                        else if(entreno=="serie" && numSer<=0){
                            entreno="bloque"; numBlo--;
                            horR=0; minR=desBlo; secR=desBloS;
                            String timeResDes = String.format("%02d:%02d:%02d", horR, minR, secR);
                            mTimerFin.setText(timeResDes);
                            feedback("short");feedback("triple");
                        }
                        else if(entreno=="bloque" &&  numBlo>0){
                            entreno="serie"; numSer= Integer.parseInt(data[5]); numSer--;
                            horR=horI; minR=minI; secR=secI; milR=milI;
                            String timeResIni2 = String.format("%02d:%02d:%02d",0,minI,secI);
                            mTimerFin.setText(timeResIni2);
                            feedback("short");feedback("triple");
                        }
                        else if(entreno=="bloque" && numBlo<=0){
                            try {
                                fout.flush();
                                fout.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finish();
                            feedback("short");feedback("large");feedback("triple");
                        }
                    }

                    long actualTime2 = System.currentTimeMillis();
                    if(actualTime2 - lastUpdate2 >= 1000) {
                        if(secR>0) secR--;
                        else {secR=59; if(minR>0)minR--;}
                        String timeRes = String.format("%02d:%02d:%02d", horR, minR, secR);
                        mTimerFin.setText(timeRes);
                        lastUpdate2 = actualTime2;
                    }
                }

                long actualTime = System.currentTimeMillis();
                if (actualTime - lastUpdate < 2000) {
                    return;
                }
                lastUpdate = actualTime;
                updateStrokes();
            }
        });
    }


    public void updateStrokes(){
        if (mServiceBound) {

            if(datas==1) {
                int s, b;
                if (entreno == "serie") {
                    s = Integer.parseInt(data[5]) - numSer;
                    infoTraining = "serie " + s;
                } else if (entreno == "bloque") {
                /*p = Integer.parseInt(data[3])-numBlo;
                infoTraining="bloque "+p;*/
                    infoTraining = "descanso";
                } else if (entreno == "descanso") {
                    infoTraining = "descanso";
                }
                b = Integer.parseInt(data[3]) - numBlo;
                infoBlock = "bloque " + b;
            }
            else{
                infoTraining = "free";
            }

            try {
                fout.write(String.valueOf(mTimer.getText().toString() + "\t" + mStrokes.getText().toString() + "\t" + mVelocity.getText().toString() + "\t" + mDistanceT.getText().toString() + "\t" + infoTraining + "\t" + infoBlock +"\n"));
                fout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInfo.setText(infoTraining);
            //String str = mService.getData();
            mStrokes.setText(mService.getData());
        }
    }




    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            mService = myBinder.getService();
            mServiceBound = true;
        }
    };


    public void updateGps(String lat, String lon, float vel) {
        if(mLL!=null){
            if(firstTime==0){
                if(lon!="" && lat!=""){
                    lat1=lat; lon1=lon;
                    firstTime=1;
                    return;
                }
            }
            long actualTimeGPS = System.currentTimeMillis();
            if(actualTimeGPS - lastUpdateGPS<10000) {
               return;
            }
            lastUpdateGPS = actualTimeGPS;

            Double lat0, lon0, latNew, lonNew, x1, y1;
            lat0 = Double.parseDouble(lat1);
            lon0 = Double.parseDouble(lon1);
            Double O0 = lon0 * Math.PI / 180;
            Double Y0 = lat0 * Math.PI / 180;
            latNew = Double.parseDouble(lat);
            lonNew = Double.parseDouble(lon);
            Double O1 = lonNew * Math.PI / 180;
            Double Y1 = latNew * Math.PI / 180;

            Double R01 = Radio * Math.cos(Y0+Y1/2);
            Double dx01 = (O1-O0) * R01;
            Double dy01 = (Y1-Y0) * Radio;

            x1 = x0 + dx01;
            y1 = y0 + dy01;

            Double distance = Math.sqrt(Math.pow(x0-x1,2)+Math.pow(y0-y1,2));
            distanceTotal += distance;
            Double velocity = (distance/10000)*3600*1000;
            x0 = x1; lat1 = lat;
            y0 = y1; lon1 = lon;
            mVelocity.setText(""+String.format("%.4s",velocity));
            mDistanceT.setText(""+String.format("%.5s",distanceTotal));
        }
        else{
            mVelocity.setText("*");
            mInfo.setText("*");
            mDistanceT.setText("*");
        }
    }

    @Override
    public void onBackPressed() {
        //no permito que se salga hacia atrás
        if(mBackground != null) {
            Toast.makeText(this, "Use STOP to exit", Toast.LENGTH_SHORT).show();
        }else{
            file.delete();
            finish();
        }
    }

    public void feedback (String type){
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        switch (type){
            case "short":
                v.vibrate(500);
                sound.play(beep);
                break;
            case "large":
                sound.play(beep);
                v.vibrate(1000);
                v.vibrate(1000);
                sound.play(beep);
                break;
            case "triple":
                for(int i=0;i<3;i++) {sound.play(beep);

                    try {
                    Thread.sleep(50);
                } catch (InterruptedException e) { //¿?¿?¿?¿?
                    e.printStackTrace();
                }
                }
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(mServiceIntent!=null) {
            stopService(mServiceIntent);
        }
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }//new
        Log.i("MAINACT", "onDestroy!");
        //mThreadBackground.stop();
        //mBackground.stop();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mServiceIntent!=null) {
            stopService(mServiceIntent);
            mServiceIntent=null;
        }

        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
        x0 = y0 = distanceTotal = 0.0;
    }
}
