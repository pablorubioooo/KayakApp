package com.example.pablo.kayakapp.planningSession;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.etc.xml.XML;

public class NewSession extends AppCompatActivity {

    EditText ETTitulo, ETRitmos, ETDesMinBloques, ETNumBloques, ETDesMinSeries, ETNumSeries, ETTimeMin, ETTimeSec;
    EditText ETDesSegBloques, ETDesSegSeries;
    Button btnPlus, btnMinus, btnDMPlus, btnDMMinus, btnDSPlus, btnDSMinus;
    Button btnSPlus, btnSMinus, btnDMSPlus, btnDMSMinus, btnTMPlus, btnTMMinus, btnDSSPlus, btnDSSMinus, btnTSPlus, btnTSMinus;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        mContext = this;

        ETTitulo = (EditText) findViewById(R.id.EditTextTittle);
        ETRitmos = (EditText) findViewById(R.id.EditTextRitmo);
        ETDesMinBloques = (EditText) findViewById(R.id.EditTextDesBloqueM);
        ETDesSegBloques = (EditText) findViewById(R.id.EditTextDesBloqueS);
        ETNumBloques = (EditText) findViewById(R.id.EditTextBloque);
        ETDesMinSeries = (EditText) findViewById(R.id.EditTextDesSeriesM);
        ETDesSegSeries = (EditText) findViewById(R.id.EditTextDesSeriesS);
        ETNumSeries = (EditText) findViewById(R.id.EditTextSerie);
        ETTimeMin = (EditText) findViewById(R.id.EditTextTimeSeriesM);
        ETTimeSec = (EditText) findViewById(R.id.EditTextTimeSeriesS);

        btnPlus = (Button) findViewById(R.id.BtnPlus);
        btnMinus = (Button)findViewById(R.id.BtnMinus);
        btnDMPlus = (Button) findViewById(R.id.BtnDMPlus);
        btnDMMinus = (Button) findViewById(R.id.BtnDMMinus);
        btnDSMinus = (Button) findViewById(R.id.BtnDSMinus);
        btnDSPlus = (Button) findViewById(R.id.BtnDSPlus);
        btnSPlus = (Button) findViewById(R.id.BtnSPlus);
        btnSMinus = (Button) findViewById(R.id.BtnSMinus);
        btnDMSPlus = (Button) findViewById(R.id.BtnDMSPlus);
        btnDMSMinus = (Button) findViewById(R.id.BtnDMSMinus);
        btnTMMinus = (Button) findViewById(R.id.BtnTMMinus);
        btnTMPlus = (Button) findViewById(R.id.BtnTMPlus);
        btnDSSMinus = (Button) findViewById(R.id.BtnDSSMinus);
        btnDSSPlus = (Button) findViewById(R.id.BtnDSSPlus);
        btnTSPlus = (Button) findViewById(R.id.BtnTSPlus);
        btnTSMinus = (Button) findViewById(R.id.BtnTSMinus);

        btnTSMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETTimeSec);
            }
        });

        btnTSPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETTimeSec);
            }
        });

        btnDSSPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETDesSegSeries);
            }
        });

        btnDSSMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETDesSegSeries);
            }
        });

        btnTMPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETTimeMin);
            }
        });

        btnTMMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETTimeMin);
            }
        });

        btnDMSMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETDesMinSeries);
            }
        });

        btnDMSPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETDesMinSeries);
            }
        });

        btnSMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETNumSeries);
            }
        });

        btnSPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETNumSeries);
            }
        });

        btnDSPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETDesSegBloques);
            }
        });

        btnDSMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETDesSegBloques);
            }
        });

        btnDMMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETDesMinBloques);
            }
        });

        btnDMPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETDesMinBloques);
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus(ETNumBloques);
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus(ETNumBloques);
            }
        });
    }

    private void minus(EditText et) {
        if(et.getText().toString().isEmpty()){
            et.setText("0");
        }
        else{
            String num = et.getText().toString();
            int numm = Integer.parseInt(num);

            if(numm>0) {
                numm--;
            }

            num = String.valueOf(numm);
            et.setText(num);
        }
    }

    private void plus(EditText et) {
        if(et.getText().toString().isEmpty()){
            et.setText("1");
        }
        else{
            String num = et.getText().toString();
            int numm = Integer.parseInt(num);

            if(numm>=0) {
                numm++;
            }

            num = String.valueOf(numm);
            et.setText(num);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusave, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Toast.makeText(this, "Menu Save selected", Toast.LENGTH_SHORT).show();
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
                        saveSession();
                        Toast.makeText(mContext, "Session saved.",
                                Toast.LENGTH_LONG).show();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        //Toast.makeText(mContext, "DataFolder not saved.",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save DataFolder?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void saveSession(){

        String tittle, ritmos, numBloques, desMinBloques, numSeries, desSeries, timeMin, timeSec, desSegBloques, desSegSeries;

        if(!ETTitulo.getText().toString().isEmpty()) tittle = ETTitulo.getText().toString(); else tittle="Entrenamiento";
        if(!ETRitmos.getText().toString().isEmpty()) ritmos = ETRitmos.getText().toString(); else ritmos="Ritmos";
        if(!ETNumBloques.getText().toString().isEmpty()) numBloques = ETNumBloques.getText().toString(); else numBloques=""+0;
        if(!ETDesMinBloques.getText().toString().isEmpty()) desMinBloques = ETDesMinBloques.getText().toString(); else desMinBloques=""+0;
        if(!ETDesSegBloques.getText().toString().isEmpty()) desSegBloques = ETDesSegBloques.getText().toString(); else desSegBloques=""+0;
        if(!ETNumSeries.getText().toString().isEmpty()) numSeries = ETNumSeries.getText().toString(); else numSeries=""+0;
        if(!ETDesMinSeries.getText().toString().isEmpty()) desSeries = ETDesMinSeries.getText().toString(); else desSeries=""+0;
        if(!ETDesSegSeries.getText().toString().isEmpty()) desSegSeries = ETDesSegSeries.getText().toString(); else desSegSeries=""+0;
        if(!ETTimeMin.getText().toString().isEmpty()) timeMin = ETTimeMin.getText().toString(); else timeMin = ""+0;
        if(!ETTimeSec.getText().toString().isEmpty()) timeSec = ETTimeSec.getText().toString(); else timeSec = ""+0;

        XML xml = new XML("");

        if(xml.writeSessionXML(tittle, ritmos, numBloques, desMinBloques, numSeries, desSeries, timeMin, timeSec, desSegBloques, desSegSeries)){
            Toast.makeText(this, "Session saved", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Error (change name of file)", Toast.LENGTH_SHORT).show();
        }


    }

}
