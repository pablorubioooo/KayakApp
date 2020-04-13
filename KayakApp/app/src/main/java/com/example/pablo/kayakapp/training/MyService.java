package com.example.pablo.kayakapp.training;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.example.pablo.kayakapp.etc.frequency.AverageReverseFilter;
import com.example.pablo.kayakapp.etc.frequency.DataConsumer;
import com.example.pablo.kayakapp.etc.frequency.DataMultiSource;
import com.example.pablo.kayakapp.etc.frequency.ExtrapolatorTransformer;
import com.example.pablo.kayakapp.etc.frequency.PieceOfData;
import com.example.pablo.kayakapp.etc.frequency.RateMeasurerReverseFilter;
import com.example.pablo.kayakapp.etc.frequency.SamplerSinkAndSource;
import com.example.pablo.kayakapp.etc.frequency.StandardOutputConsumer;
import com.example.pablo.kayakapp.etc.frequency.StrokeDetectorReverseFilter;
import com.example.pablo.kayakapp.etc.frequency.TFPieceOfData;
import com.example.pablo.kayakapp.etc.frequency.TV3PieceOfData;
import com.example.pablo.kayakapp.etc.frequency.TimedPieceOfData;
import com.example.pablo.kayakapp.etc.frequency.V3PieceOfData;
import com.example.pablo.kayakapp.etc.frequency.ZReverseFilter;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class MyService extends Service implements DataMultiSource{

    Long lastUpdate;
    SensorEventListener listen;
    Context context;
    private IBinder mBinder = new MyBinder();
    private SensorManager sensorManager;
    private Sensor acelerometerSensor;

    ExtrapolatorTransformer et = null;
    SamplerSinkAndSource ssas = null;
    LinkedList<DataConsumer> accelerationConsumers, anglesConsumers;
    float speed = 1.0f; long present;
    V3PieceOfData podAcceleration;
    File dir;
    public String result;
    StandardOutputConsumer sc = new StandardOutputConsumer(this);

    public MyService(){
    }

    public MyService(Context mContext) {
        super();
        Log.i("HERE", "here I am!");
        context=mContext;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        listen = new SensorListen();

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        acelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listen, acelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);

        lastUpdate = System.currentTimeMillis();

        et=new ExtrapolatorTransformer(100);

        ssas=new SamplerSinkAndSource(
                new ZReverseFilter(new AverageReverseFilter(
                        new StrokeDetectorReverseFilter(
                                new RateMeasurerReverseFilter(sc),-0.005,3),33)) //-0.005 //3
                                        , et, PieceOfData.Tag.Acceleration, 10 /* deltaT */);
        ssas.go();

        this.addConsumer(et).go();
        File tarjeta = Environment.getExternalStorageDirectory();
        dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/Accelerometer.txt"));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        sensorManager.unregisterListener(listen);
        ssas=null; et=null; sc=null;
        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("UNBIND-TAG", "in onUnbind");
        return true;
    }

    @Override
    public void go() {
    }

    @Override
    public DataMultiSource addConsumer(DataConsumer c) {
        return addConsumer(c,PieceOfData.Tag.NoTag);
    }

    @Override
    public DataMultiSource addConsumer(DataConsumer c, PieceOfData.Tag tag) {
        if (c==null) return this;
        switch (tag)
        {case Acceleration:
            if (accelerationConsumers==null) accelerationConsumers=new LinkedList<DataConsumer>();
            accelerationConsumers.add(c);
            break;
            case Angles:
                if (anglesConsumers      ==null) anglesConsumers      =new LinkedList<DataConsumer>();
                anglesConsumers.add(c);
                break;
            case NoTag:
                if (accelerationConsumers==null) accelerationConsumers=new LinkedList<DataConsumer>();
                if (anglesConsumers      ==null) anglesConsumers      =new LinkedList<DataConsumer>();
                accelerationConsumers.add(c); anglesConsumers.add(c);
                break;
        }
        return this;
    }


    public void onNewData(TFPieceOfData pod) {

    }


    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    private void getFrequency(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        long actualTime = System.currentTimeMillis();
        PieceOfData pod=null; TimedPieceOfData tDAcceleration=null;
        pod = new TV3PieceOfData(actualTime,values); if (pod.isNoData()) return;
        tDAcceleration=(TimedPieceOfData)pod;
        podAcceleration=(V3PieceOfData)tDAcceleration.getPieceOfData();
        present=tDAcceleration.getTime();

        if (tDAcceleration.getTime()<=present)
        {
            Iterator<DataConsumer> it=accelerationConsumers.iterator();
            while (it.hasNext()) it.next().onNewData(podAcceleration.setTag(PieceOfData.Tag.Acceleration));
        }

        long nextT=0;
        nextT=(long)tDAcceleration.getTime();

        if (speed>0.0f)
            try                 {Thread.sleep((long)((nextT-present)/speed));}
            catch (Exception e) {}

        present=nextT;

    }



    public String getData()
    {
        return result;
    }

    public void onNewFrek(String pod){
        String[] r = pod.split(" ");
        String cadena = r[1];
        result = ""+cadena.charAt(0)+cadena.charAt(1)+cadena.charAt(2);
    }


public class SensorListen implements SensorEventListener{

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getFrequency(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
}


