package com.example.pablo.kayakapp.etc.frequency;

import android.util.Log;


public class RateMeasurerReverseFilter implements DataConsumer
{
     DataConsumer consumer;
     PieceOfData lastStroke;

 public RateMeasurerReverseFilter(DataConsumer dc)
 {
  consumer=dc;
  lastStroke=new PieceOfData();


 }


 public void onNewData(PieceOfData pod)
 {
  if (pod.isNoMoreData())
  {
   consumer.onNewData(pod);
   return;
  }

  if (lastStroke.isNoData())
  {
   lastStroke=pod;
   return;
  }

  TPieceOfData lS=(TPieceOfData)lastStroke;
  TPieceOfData pS=(TPieceOfData)pod;

  if (lS.getTime()>=pS.getTime()) return;

  consumer.onNewData(new TFPieceOfData(pS.getTime(),60000.0f/(pS.getTime()-lS.getTime())));
  Log.d("*.*.*.*.*.*.*.*.*.", "----> DATA PATH <----"+"6 - RateMeasurer ** ");

  lastStroke=pS;}
}



