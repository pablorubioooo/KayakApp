package com.example.pablo.kayakapp.dataSession;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.etc.wifiDirect.WifiDirect;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DataStudy extends AppCompatActivity {

    private TextView mTittle;
    private Button bVelTime, bVelFre, bDisTime;
    String fileName;
    File dir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_study);

        fileName = getIntent().getStringExtra("fileName");
        File tarjeta = Environment.getExternalStorageDirectory();
        dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"+fileName));

        mTittle = (TextView) findViewById(R.id.TittleTraining);
        String [] nameSplit1 = fileName.split(".txt");
        String [] nameSplit2 = nameSplit1[0].split("/");
        mTittle.setText(nameSplit2[1]);

        bVelFre = (Button) findViewById(R.id.BtnV_F);
        bVelTime = (Button) findViewById(R.id.BtnV_T);
        bDisTime = (Button) findViewById(R.id.BtnD_T);

        String type = isFree();
        //Toast.makeText(getApplicationContext(),type,Toast.LENGTH_SHORT).show();

        if (type.compareTo("free") == 0) {
            bVelTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), DataGraphic.class);
                    myIntent.putExtra("fileName", fileName);
                    myIntent.putExtra("graph", "VT");
                    myIntent.putExtra("type","0");
                    startActivity(myIntent);
                }
            });

            bVelFre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), DataGraphic.class);
                    myIntent.putExtra("fileName", fileName);
                    myIntent.putExtra("graph", "VF");
                    myIntent.putExtra("type","0");
                    startActivity(myIntent);
                }
            });

            bDisTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), DataGraphic.class);
                    myIntent.putExtra("fileName", fileName);
                    myIntent.putExtra("graph", "DT");
                    myIntent.putExtra("type","0");
                    startActivity(myIntent);
                }
            });
        }else{
            bVelTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), DataBlock.class);
                    myIntent.putExtra("fileName", fileName);
                    myIntent.putExtra("graph", "VT");
                    startActivity(myIntent);
                }
            });

            bVelFre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), DataBlock.class);
                    myIntent.putExtra("fileName", fileName);
                    myIntent.putExtra("graph", "VF");
                    startActivity(myIntent);
                }
            });

            bDisTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), DataBlock.class);
                    myIntent.putExtra("fileName", fileName);
                    myIntent.putExtra("graph", "DT");
                    startActivity(myIntent);
                }
            });
        }
    }

    private String isFree() {

        BufferedReader inputStream;
        String l;
        String [] data = null;
        File file = new File(String.valueOf(dir));
        try {

            inputStream = new BufferedReader(new FileReader(dir));
            l = inputStream.readLine();
            data=l.split("\t");
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data[4];
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
                //ENVIAR DATOS POR WIFIDIRECT
                alertMessageP2P();
                break;
            default:
                break;
        }
        return true;
    }

    public void alertMessageP2P() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Toast.makeText(getApplicationContext(), "Wifi Direct",
                                Toast.LENGTH_SHORT).show();
                        wifiDirect();
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
        builder.setMessage("Send session to the coach?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void wifiDirect() {
        Intent i = new Intent(getApplicationContext(), WifiDirect.class);
        i.putExtra("path",""+dir);
        i.putExtra("name",fileName);
        startActivity(i);
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
    public void onRestart(){
        super.onRestart();
        //refresh the activity
        finish();
        startActivity(getIntent());
    }

}
