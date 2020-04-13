package com.example.pablo.kayakapp.dataSession;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.etc.listItem.AdapterCategory;
import com.example.pablo.kayakapp.etc.listItem.Category;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class DataBlock extends AppCompatActivity {

    String fileName, graphic;
    File tarjeta, dir;
    String[] result;
    int nBlocks=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_block);
        ArrayList<Category> items = new ArrayList<Category>();
        Drawable res = getResources().getDrawable(R.drawable.ic_logo);

        fileName = getIntent().getStringExtra("fileName");
        tarjeta = Environment.getExternalStorageDirectory();
        dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PostEntrenos/"+fileName));
        graphic = getIntent().getStringExtra("graph");

        if(graphic.equals("VT")) {
            graphic="Velocidad-Tiempo";
        }
        else if(graphic.equals("VF")){
            graphic = "Velocidad-Frecuencia";
        }
        else if(graphic.equals("DT")){
            graphic = "Distancia-Tiempo";
        }
        else{
            graphic ="Graphics";
        }
        String line,block;
        String typeBlock = "bloque 99";
        File file = new File(String.valueOf(dir));
        BufferedReader inputStream;
        try {

            inputStream = new BufferedReader(new FileReader(dir));
            while((line = inputStream.readLine()) != null) {
                result = line.split("\t");
                block = result[5];

                if(block.equals(typeBlock)){
                    typeBlock = block;
                }
                else{
                    nBlocks++;
                    typeBlock = block;
                }
            }
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int x=0;x<(nBlocks-1);x++){
            items.add(new Category(""+x, "Bloque "+(x+1), graphic, res));  //SPLIT CON LA FECHA
        }

        AdapterCategory adapter = new AdapterCategory(this, items);
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        setContentView(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent(view.getContext(), DataGraphic.class);
                myIntent.putExtra("fileName", fileName);
                myIntent.putExtra("graph",getIntent().getStringExtra("graph"));
                myIntent.putExtra("type","1");
                startActivity(myIntent);
            }
        });

    }
}
