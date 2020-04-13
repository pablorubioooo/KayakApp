package com.example.pablo.kayakapp.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pablo.kayakapp.dataSession.DataFolder;
import com.example.pablo.kayakapp.planningSession.Planning;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.training.Training;
import java.io.File;

public class MainActivity extends Activity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_READ_STORAGE = 113;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (TextView) findViewById(R.id.TittleApp);

        if (isReadStoragePermissionGranted())
            //Toast.makeText(this, "Permision Read OK", Toast.LENGTH_SHORT).show();

        if (isWriteStoragePermissionGranted())
            //Toast.makeText(this, "Permision Write OK", Toast.LENGTH_SHORT).show();

        //comprobamos si la memoria externa esta disponible
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i("SDCARDINFO", "SD Card se encuentra presente.");
            //Toast.makeText(this, "SD Card se encuentra presente.", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("SDCARDINFO", "No se encuentra SD Card.");
            //Toast.makeText(this, "No se encuentra SD Card.", Toast.LENGTH_SHORT).show();
        }

        // Creo el directoio para guardar el fichero
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir1 = new File(tarjeta.getAbsolutePath() + "/Piragua/");
        File dir2 = new File((tarjeta.getAbsolutePath() + "/Piragua/PreEntrenos/"));
        File dir3 = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"));
        File dir4 = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/Compartidos/"));

        if (!dir1.exists()) {
            Toast.makeText(this, "Creando directorio Piragua", Toast.LENGTH_SHORT).show();
            dir1.mkdir();
        }
        if (!dir2.exists()) {
            Toast.makeText(this, "Creando directorio PreEntrenos", Toast.LENGTH_SHORT).show();
            dir2.mkdir();
        }
        if (!dir3.exists()) {
            Toast.makeText(this, "Creando directorio PostEntrenos", Toast.LENGTH_SHORT).show();
            dir3.mkdir();
        }
        if (!dir4.exists()) {
            Toast.makeText(this, "Creando directorio Compartidos", Toast.LENGTH_SHORT).show();
            dir4.mkdir();
        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Pablo Rubio Machacón\npablorubio@usal.es",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void training(View view) {
        Intent i = new Intent(this, Training.class);
        i.putExtra("datas",0);
        startActivity(i);
    }

    public void data(View view) {
        Intent i = new Intent(this, DataFolder.class);
        startActivity(i);
    }

    public void planning(View view) {
        Intent i = new Intent(this, Planning.class);
        startActivity(i);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                    Toast.makeText(this, "OK Write Permission", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case REQUEST_READ_STORAGE: {
                //premission to read storage
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "OK Read Permission", Toast.LENGTH_SHORT).show();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "We Need READ permission Storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted1");
                return true;
            } else {

                Log.v("Permission", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission", "Permission is granted1");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted2");
                return true;
            } else {

                Log.v("Permission", "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission", "Permission is granted2");
            return true;
        }
    }
}