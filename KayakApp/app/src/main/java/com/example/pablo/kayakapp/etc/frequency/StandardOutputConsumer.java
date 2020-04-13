package com.example.pablo.kayakapp.etc.frequency;

import android.util.Log;

import com.example.pablo.kayakapp.training.MyService;

public class StandardOutputConsumer implements DataConsumer
 {
     MyService service;

     public StandardOutputConsumer(MyService myService) {
         service = myService;
     }


     public void onNewData(PieceOfData pod)
   {
       if (!pod.isNoData())
   {
      System.out.println((pod.getTag()!=PieceOfData.Tag.NoTag?pod.getStringTag()+": ":"") +pod);
      //Log.d("*-*-*-*-*-*-*-*-*-", "O-----> HERE <-----O: "+ (pod.getTag()!=PieceOfData.Tag.NoTag?pod.getStringTag()+": ":"")+pod);
      service.onNewFrek(""+pod);
   }
   }


 }

