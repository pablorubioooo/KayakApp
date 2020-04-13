package com.example.pablo.kayakapp.planningSession;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.pablo.kayakapp.etc.listItem.AdapterCategory;
import com.example.pablo.kayakapp.etc.listItem.Category;
import com.example.pablo.kayakapp.R;
import com.example.pablo.kayakapp.etc.xml.XML;
import java.io.File;
import java.util.ArrayList;

public class Planning extends AppCompatActivity {

    String replaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        ArrayList<Category> items = new ArrayList<Category>();
        Drawable res = getResources().getDrawable(R.drawable.ic_logo);
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PreEntrenos/"));
        final String[] ficheros = dir.list();
        String[] result;

        if (ficheros == null)
            Toast.makeText(this, "No existen entrenamientos", Toast.LENGTH_SHORT).show();
        else {
            for (int x=0;x<ficheros.length;x++){
                XML xml = new XML(ficheros[x]);
                result = xml.getInfoXML();
                replaceName=ficheros[x].replace(".xml", "");
                replaceName=replaceName.replace("%20", " ");
                items.add(new Category(""+x, replaceName, result[1], res));
            }
        }

        AdapterCategory adapter = new AdapterCategory(this, items);
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        setContentView(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent(view.getContext(), Session.class);
                myIntent.putExtra("fileName", ficheros[position]);
                startActivity(myIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuadd, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Toast.makeText(this, "Menu Add selected", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this,  NewSession.class);
                startActivity(i);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRestart(){
        super.onRestart();
        //refresh the activity
        finish();
        startActivity(getIntent());
    }

}
