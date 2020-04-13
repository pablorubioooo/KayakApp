package com.example.pablo.kayakapp.dataSession;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.etc.listItem.AdapterCategory;
import com.example.pablo.kayakapp.etc.listItem.Category;
import com.example.pablo.kayakapp.etc.wifiDirect.WifiDirect;
import java.io.File;
import java.util.ArrayList;

public class DataTraining extends AppCompatActivity {

    String dirName;
    public File dir;
    String[] ficheros = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_training);

        dirName = getIntent().getStringExtra("dirName");
        ArrayList<Category> items = new ArrayList<Category>();
        Drawable res = getResources().getDrawable(R.drawable.ic_logo);
        File tarjeta = Environment.getExternalStorageDirectory();
        dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"+dirName));

        ficheros = dir.list();

        if (ficheros != null) {

            if (ficheros.length == 0) {
                Toast.makeText(this, "No existen entrenamientos\nEl directorio se ha borrado", Toast.LENGTH_SHORT).show();
                dir.delete();
                onRestart();
            } else {
                for (int x = 0; x < ficheros.length; x++) {
                    String[] name = ficheros[x].split(".txt");
                    items.add(new Category("" + x, name[0], "" + (x+1), res));  //SPLIT CON LA FECHA
                }
            }
        }

        AdapterCategory adapter = new AdapterCategory(this, items);
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        setContentView(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent(view.getContext(), DataStudy.class);
                myIntent.putExtra("fileName", dirName+"/"+ficheros[position]);
                startActivity(myIntent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                File f = new File(dir+"/"+ficheros[pos]);
                showChangeNameDialog(f, ""+dir, ficheros[pos]);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menudelete, menu);
        if(dirName.equals("Compartidos"))  inflater.inflate(R.menu.menuwifidirect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                alertDelete();
                break;
            case R.id.wifiDirect:
                wifiDirect();
                break;
            default:
                break;
        }
        return true;
    }

    private void wifiDirect() {
        Intent i = new Intent(getApplicationContext(), WifiDirect.class);
        i.putExtra("path",""+dir);
        i.putExtra("name","fileName");
        startActivity(i);
    }

    public void showChangeNameDialog(final File f, final String path, final String name) {

        String [] result = name.split(" ");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.custom_dialog, null);

        final EditText editText = (EditText) dialoglayout.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialoglayout.findViewById(R.id.buttonSubmit);
        Button button2 = (Button) dialoglayout.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                String newName = editText.getText().toString();
                File newFile = new File(path+"/"+newName+".txt");
                f.renameTo(newFile);
                System.out.println("NEW FILENAME -----> "+newFile);
                onRestart();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialoglayout);
        builder.show();


    }

    private void alertDelete() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        delete();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all files?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    private void delete() {

        String[] ficheros = dir.list();

        if(ficheros.length==0){
            Toast.makeText(getApplicationContext(),"No files",Toast.LENGTH_SHORT).show();
        }
        else{
            for(int i=0;i<ficheros.length;i++){
                File f = new File(dir+"/"+ficheros[i]);
                if(f.exists()){
                    f.delete();
                }
            }
            onRestart();
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
