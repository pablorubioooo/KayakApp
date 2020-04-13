package com.example.pablo.kayakapp.etc.frequency;

import android.util.Log;

public class SamplerSinkAndSource implements DataSink,DataSource
{
 DataConsumer consumer;
 DataProducer producer;
 final PieceOfData requiredPOD;
 final long deltaT;
 long initialTime, idealTime, presentTime;
 boolean relativeTime=true;
 boolean addTimeToData=true;

 public SamplerSinkAndSource(DataConsumer dc, DataProducer dp, PieceOfData.Tag tag, long dT, boolean aTTD, boolean rT)
 {
  consumer=dc;
  producer=dp;
  requiredPOD=new PieceOfData().setTag(tag);
  deltaT=dT;
  relativeTime=rT;
  addTimeToData=aTTD;
 }

 public SamplerSinkAndSource(DataConsumer dc, DataProducer dp, PieceOfData.Tag tag, long dT)
 {
  this(dc,dp,tag,dT,true,true);
 }

 public void go()
 {new Thread(
         new Runnable()
         {@Override
         public void run()
         {
          PieceOfData pod=producer.getData(requiredPOD);

          while (pod.isNoData() && !pod.isNoMoreData())
          {
           try {Thread.sleep(deltaT);
           }
           catch (Exception e) {}

           pod=producer.getData(requiredPOD);
          }

          initialTime=System.currentTimeMillis(); idealTime=0; presentTime=initialTime;
          for (;;)
          {
           if (pod.isNoMoreData()) {
            consumer.onNewData(pod);
            return;
           }

           if (addTimeToData)
            if (pod instanceof TimedPieceOfData) {/* TODO: strip time and update */}
            else
            {
             pod=pod.addTime(idealTime+(relativeTime?0:initialTime));
            }

           consumer.onNewData(pod);
           //Log.d("*.*.*.*.*.*.*.*.*.", "----> DATA PATH <----"+"2 - sass");

           idealTime+=deltaT;
           long timeLapse=initialTime+idealTime-System.currentTimeMillis();
           if (timeLapse>0)
           {
            try {Thread.sleep(timeLapse);
            }
            catch (Exception e) {}
           }
           presentTime=idealTime;
           pod=producer.getData(requiredPOD);}
         }
         }).start();
       return;
 }
}

