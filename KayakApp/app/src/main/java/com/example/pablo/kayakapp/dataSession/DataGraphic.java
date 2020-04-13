package com.example.pablo.kayakapp.dataSession;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.pablo.kayakapp.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class DataGraphic extends AppCompatActivity {

    String fileName, type, typeGraph;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    PointsGraphSeries<DataPoint> seriesP;
    ArrayList<String[]> listaFree=new ArrayList<>();
    ArrayList<String[]> listaBlock=new ArrayList<>();
    ArrayList<DataPoint> listaDP = new ArrayList<>();
    String data[];
    BufferedReader inputStream = null;
    File file, tarjeta, dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_graphic);

        fileName = getIntent().getStringExtra("fileName");
        type = getIntent().getStringExtra("graph");
        typeGraph = getIntent().getStringExtra("type"); //0 is free
        tarjeta = Environment.getExternalStorageDirectory();
        dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"+fileName));


        graphView=(GraphView) findViewById(R.id.graphView);

        getData();

        String [] result;
        if(typeGraph.equals("0")) {
            for (int i = 0; i < listaFree.size(); i++) {
                result = listaFree.get(i);
                float x,y;

                String crono = result[0];
                String [] cronoresult = crono.split(":");
                int horas = Integer.parseInt(cronoresult[0]);
                int minutos = Integer.parseInt(cronoresult[1]);
                int segundos = Integer.parseInt(cronoresult[2]);
                int milis = Integer.parseInt(cronoresult[3]);
                int time = segundos + minutos*60 + horas*60*60;


                if(type.equals("VT")){
                    if(!result[2].equals("*")) {
                        String vel = result[2];
                        x = Float.valueOf(vel);
                        y = time;
                        listaDP.add(new DataPoint(x, y));
                    }
                }else if(type.equals("VF")){
                    if(!result[2].equals("*") && !result[1].equals("*")) {
                        String vel = result[2];
                        x = Float.valueOf(vel);
                        String frek = result[1];
                        y = Float.parseFloat(frek);
                        listaDP.add(new DataPoint(x, y));
                    }
                }else if(type.equals("DT")){
                    if(!result[3].equals("*")) {
                        String dis = result[3];
                        x = Float.parseFloat(dis);
                        y = time;
                        listaDP.add(new DataPoint(x, y));
                    }
                }else return;

            }
        }
        else{
            for (int i = 0; i < listaBlock.size(); i++) {
                result = listaBlock.get(i);

                float x,y;

                String crono = result[0];
                String [] cronoresult = crono.split(":");
                int horas = Integer.parseInt(cronoresult[0]);
                int minutos = Integer.parseInt(cronoresult[1]);
                int segundos = Integer.parseInt(cronoresult[2]);
                int milis = Integer.parseInt(cronoresult[3]);
                int time = segundos + minutos*60 + horas*60*60;

                if(type.equals("VT")){
                    if(!result[2].equals("*")) {
                        String vel = result[2];
                        x = Float.valueOf(vel);
                        y = time;
                        listaDP.add(new DataPoint(x, y));
                    }
                }else if(type.equals("VF")){
                    if(!result[2].equals("*") && !result[1].equals("*")) {
                        String vel = result[2];
                        x = Float.valueOf(vel);
                        String frek = result[1];
                        y = Float.parseFloat(frek);
                        listaDP.add(new DataPoint(x, y));
                    }
                }else if(type.equals("DT")){
                    if(!result[3].equals("*")) {
                        String dis = result[3];
                        x = Float.parseFloat(dis);
                        y = time;
                        listaDP.add(new DataPoint(x, y));
                    }
                }else return;

            }
        }

        DataPoint[] dP = new DataPoint[listaDP.size()];
        for(int p=0;p<listaDP.size();p++) {
            dP[p] = listaDP.get(p);
        }

        series = new LineGraphSeries<>(dP);
        seriesP = new PointsGraphSeries<>(dP);

        drawGraphics();

    }

    private void drawGraphics() {
        graphView.addSeries(series);
        graphView.addSeries(seriesP);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setScrollable(true);

        graphView.getViewport().setScalable(true);

        if(type.equals("VT")) {
            graphView.setTitle("Velocidad-Tiempo");
        }
        else if(type.equals("VF")){
            graphView.setTitle("Velocidad-Frecuencia");
        }
        else if(type.equals("DT")){
            graphView.setTitle("Distancia-Tiempo");
        }
        else{
            graphView.setTitle("Graphics");
        }

        graphView.setTitleTextSize(60);
        graphView.getViewport().setScalable(true);

        seriesP.setColor(Color.GREEN);
        seriesP.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                canvas.drawLine(x-15, y-15, x+15, y+15, paint);
                canvas.drawLine(x+15, y-15, x-15, y+15, paint);
            }
        });

        seriesP.setOnDataPointTapListener(new OnDataPointTapListener() {
            //@Override
            public void onTap(Series series, DataPointInterface dataPoint) {

                String msg = "X: "+dataPoint.getX()+"\nY: "+ dataPoint.getY();
                String fin, fint;
                if(type.equals("VT")) {
                    String v = ""+dataPoint.getX();
                    int l = v.length();
                    if (l<=3) fin = v.substring(0,3);
                    else fin = v.substring(0,4);
                    String t = ""+dataPoint.getY();
                    msg = "Time: "+t.substring(0,4)+"\nVel: "+ fin;
                }
                else if(type.equals("VF")){
                    String v = ""+dataPoint.getX();
                    int l = v.length();
                    if (l<=3) fin = v.substring(0,3);
                    else fin = v.substring(0,4);
                    String f = ""+dataPoint.getY();
                    msg = "Frek: "+f.substring(0,4)+"\nVel: "+ fin;
                }
                else if(type.equals("DT")){
                    String d = dataPoint.getX()+"";
                    String t = ""+dataPoint.getY();
                    msg = "Dist: "+d.substring(0,5)+"\nTime: "+ t.substring(0,4);
                }

                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        String l="";
        file = new File(String.valueOf(dir));

        try {

            inputStream = new BufferedReader(new FileReader(dir));
            while ((l = inputStream.readLine()) != null) {
                data=l.split("\t");

                if (data[4].equals("free")){
                    listaFree.add(data);
                }
                else{
                    listaBlock.add(data);
                }

            }
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                Toast.makeText(this, "Session deleted", Toast.LENGTH_SHORT).show();
                alertMessage();
                break;
            default:
                break;
        }
        return true;
    }



    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Toast.makeText(getApplicationContext(), "Session deleted",
                                Toast.LENGTH_SHORT).show();
                        deleteSession();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        Toast.makeText(getApplicationContext(), "Nothing",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Session?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteSession() {

        File tarjeta = Environment.getExternalStorageDirectory();
        File file = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"+fileName));

        if (file.exists()){
            file.delete();
            finish();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        finish();
    }
}
