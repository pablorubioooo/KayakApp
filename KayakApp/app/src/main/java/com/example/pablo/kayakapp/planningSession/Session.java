package com.example.pablo.kayakapp.planningSession;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.etc.xml.XML;
import com.example.pablo.kayakapp.training.Training;
import java.io.File;

public class Session extends AppCompatActivity {

    TextView tvTitle, tvDate, tvRitmos, tvDesMinBloques, tvDesSegBloques, tvNumBloques;
    TextView  tvDesMinSeries, tvDesSegSeries, tvNumSeries, tvTMinSer, tvTSegSer;
    String fileName;
    String [] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        fileName = getIntent().getStringExtra("fileName");
        XML xml = new XML(fileName);
        result = xml.getInfoXML();

        tvTitle = (TextView) findViewById(R.id.TextViewTitle); tvTitle.setText(result[0]);
        tvDate = (TextView) findViewById(R.id.TextViewDate); tvDate.setText(result[1]);
        tvRitmos = (TextView) findViewById(R.id.TextViewRitmos); tvRitmos.setText(result[2]);
        tvNumBloques = (TextView) findViewById(R.id.TextViewNumBloq); tvNumBloques.setText(result[3]);
        tvDesMinBloques = (TextView) findViewById(R.id.TextViewMinBloq); tvDesMinBloques.setText(result[4]);
        tvDesSegBloques = (TextView) findViewById(R.id.TextViewSegBloq); tvDesSegBloques.setText(result[9]);
        tvNumSeries = (TextView) findViewById(R.id.TextViewNumSer); tvNumSeries.setText(result[5]);
        tvDesMinSeries = (TextView) findViewById(R.id.TextViewMinSer); tvDesMinSeries.setText(result[6]);
        tvDesSegSeries = (TextView) findViewById(R.id.TextViewSegSer); tvDesSegSeries.setText(result[10]);
        tvTMinSer = (TextView) findViewById(R.id.TextViewTMinSer); tvTMinSer.setText(result[7]);
        tvTSegSer = (TextView) findViewById(R.id.TextViewTSegSer); tvTSegSer.setText(result[8]);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menudelete, menu);
        inflater.inflate(R.menu.menustart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                Toast.makeText(this, "Session deleted", Toast.LENGTH_SHORT).show();
                alertMessage();
                break;
            case R.id.start:
                Toast.makeText(this, "Starting session", Toast.LENGTH_SHORT).show();
                startMessage();
                break;
            default:
                break;
        }
        return true;
    }

    private void startMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Toast.makeText(getApplicationContext(), "Session started",
                                Toast.LENGTH_SHORT).show();
                        startSession();
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
        builder.setMessage("Start Session?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void startSession() {
        Intent myIntent = new Intent(this, Training.class);
        myIntent.putExtra("datas", 1);
        myIntent.putExtra("DataFolder", result);
        startActivity(myIntent);
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
        File file = new File((tarjeta.getAbsolutePath() + "/Piragua/PreEntrenos/"+fileName));

        if (file.exists()){
            file.delete();
            finish();
        }
    }

    /*@Override
    public void onPause()
    {
        super.onPause();
        finish();
    }*/

}
