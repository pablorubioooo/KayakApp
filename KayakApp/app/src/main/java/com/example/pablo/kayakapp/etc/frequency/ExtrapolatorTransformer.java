package com.example.pablo.kayakapp.etc.frequency;

import java.util.*;

import java.util.*;

public class ExtrapolatorTransformer implements DataTransformer
{
   final long maxExtrapolationTime;

 // TODO: if one wants to do a linear or quadratic extrapolation, more old
 //       data should be stored in ReceivedData
 private class ReceivedData
 {
  long receptionTime; PieceOfData data;

  ReceivedData(PieceOfData d) {receptionTime=System.currentTimeMillis(); data=d;}

  long getReceptionTime() {return receptionTime;}

  PieceOfData getData()   {return data;}
 }

 Map<PieceOfData.Tag,ReceivedData> receivedDataMap;

 public ExtrapolatorTransformer(long mET)
 {
  maxExtrapolationTime=mET;
  receivedDataMap=new TreeMap<PieceOfData.Tag,ReceivedData>();
 }

 public ExtrapolatorTransformer() {this(0);}

 public void onNewData(PieceOfData pod)
 {
  long presentTime=System.currentTimeMillis();
  if (pod.isNoMoreData())
  {
   ReceivedData rD=receivedDataMap.get(pod.getTag());

   if (rD==null || !rD.getData().isNoMoreData())
    receivedDataMap.put(pod.getTag(),new ReceivedData(pod));
   return;
  }

  if (pod.isNoData()) return;

  receivedDataMap.put(pod.getTag(),new ReceivedData(pod));}

 public PieceOfData getData(PieceOfData pod)
 {
  PieceOfData.Tag tag=pod.getTag();
  ReceivedData rD=receivedDataMap.get(tag);
  long presentTime=System.currentTimeMillis();

  if (rD==null || maxExtrapolationTime>0 && presentTime-rD.getReceptionTime()>maxExtrapolationTime)
   return new PieceOfData().setTag(tag);
   return rD.getData();
 }
}
