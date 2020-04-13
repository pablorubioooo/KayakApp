package com.example.pablo.kayakapp.dataSession;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.pablo.kayakapp.etc.listItem.AdapterCategory;
import com.example.pablo.kayakapp.etc.listItem.Category;
import com.example.pablo.kayakapp.R;
import java.io.File;
import java.util.ArrayList;

public class DataFolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_folder);

        ArrayList<Category> items = new ArrayList<Category>();
        Drawable res = getResources().getDrawable(R.drawable.ic_action_name_folder);
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"));
        final String[] ficheros = dir.list();

        if (ficheros.length==0)
            Toast.makeText(this, "No existen entrenamientos\n ¡¡A ENTRENAR!!", Toast.LENGTH_SHORT).show();
        else {
            for (int x=0;x<ficheros.length;x++){
                items.add(new Category(""+x, ficheros[x], "Folder", res));  //SPLIT CON LA FECHA
            }
        }

        AdapterCategory adapter = new AdapterCategory(this, items);
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        setContentView(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                    Intent myIntent = new Intent(view.getContext(), DataTraining.class);
                    myIntent.putExtra("dirName", ficheros[position]);
                    startActivity(myIntent);
            }
        });
    }
    @Override
    public void onRestart(){
        super.onRestart();
        //refresh the activity
        finish();
        startActivity(getIntent());
    }
}
