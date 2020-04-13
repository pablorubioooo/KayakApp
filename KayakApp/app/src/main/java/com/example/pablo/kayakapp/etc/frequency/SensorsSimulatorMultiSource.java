package com.example.pablo.kayakapp.etc.frequency;

import java.io.*;
import java.util.*;

class SensorsSimulatorMultiSource implements DataMultiSource
 {
  LinkedList<DataConsumer> accelerationConsumers, anglesConsumers;
  float speed; long present;
  V3PieceOfData podAcceleration, podAngles;
  BufferedReader brAcceleration, brAngles;

  SensorsSimulatorMultiSource(float s, BufferedReader brAc, BufferedReader brAn)
   {
    speed=s;
    brAcceleration=brAc;
    brAngles=brAn;
   }

  SensorsSimulatorMultiSource(BufferedReader brAc,BufferedReader brAn)
   {
    this(1.0f,brAc,brAn);
   }

  @Override
  public DataMultiSource addConsumer(DataConsumer c, PieceOfData.Tag tag)
   {
    if (c==null) return this;
    switch (tag)
     {
      case Acceleration:
        if (accelerationConsumers==null) accelerationConsumers=new LinkedList<DataConsumer>();
        accelerationConsumers.add(c);
        break;

      case Angles:
        if (anglesConsumers      ==null) anglesConsumers      =new LinkedList<DataConsumer>();
        anglesConsumers.add(c);
        break;

      case NoTag:
        if (accelerationConsumers==null) accelerationConsumers=new LinkedList<DataConsumer>();
        if (anglesConsumers      ==null) anglesConsumers      =new LinkedList<DataConsumer>();
        accelerationConsumers.add(c); anglesConsumers.add(c);
        break;

      }
    return this;
   }

  @Override
  public DataMultiSource addConsumer(DataConsumer c)
   {
    return addConsumer(c,PieceOfData.Tag.NoTag);
   }

  private PieceOfData parseData(String s)
   {
    if (s==null) return PieceOfData.getNoMoreData();

    Scanner sc=new Scanner(s).useLocale(Locale.US);
    if (!sc.hasNextFloat()) return null;
    long t=(long)sc.nextFloat();
    if (!sc.hasNextFloat()) return null;
    float v[]=new float[3];
    v[0]=sc.nextFloat();
    if (!sc.hasNextFloat()) return null;
    v[1]=sc.nextFloat();
    if (!sc.hasNextFloat()) return null;
    v[2]=sc.nextFloat();
    return new TV3PieceOfData(t,v);
   }

  private PieceOfData readData(BufferedReader br)
   {
    PieceOfData d;

    try
     {
      do d=parseData(br.readLine());
      while (d==null);
     }
    catch (Exception e)
     {
      d=PieceOfData.getNoMoreData();
     }
    return d;}

  @Override
  public void go()
   {boolean hasAcceleration=(accelerationConsumers!=null && brAcceleration!=null);
    boolean hasAngles      =(anglesConsumers      !=null && brAngles      !=null);
    if (!hasAcceleration && !hasAngles) return;

    
    PieceOfData pod; TimedPieceOfData tDAcceleration=null, tDAngles=null;


    if (hasAcceleration)
     {
      pod            =readData(brAcceleration);
      if (pod.isNoData()) return;
      tDAcceleration=(TimedPieceOfData)pod;
      podAcceleration=(V3PieceOfData)tDAcceleration.getPieceOfData();
      present=tDAcceleration.getTime();
     }


    if (hasAngles)
     {
      pod            =readData(brAngles);       if (pod.isNoData()) return;
      tDAngles=(TimedPieceOfData)pod;
      podAngles      =(V3PieceOfData)tDAngles.getPieceOfData();
      if (!hasAcceleration || tDAngles.getTime()<present) present=tDAngles.getTime();
     }

    for (;;)
     {
      if (hasAcceleration && tDAcceleration.getTime()<=present)
       {
        Iterator<DataConsumer> i=accelerationConsumers.iterator();
        while (i.hasNext()) i.next().onNewData(podAcceleration.setTag(PieceOfData.Tag.Acceleration));
        pod=readData(brAcceleration); if (pod.isNoData()) return;
        tDAcceleration=(TimedPieceOfData)pod;
        podAcceleration=(V3PieceOfData)tDAcceleration.getPieceOfData();
       }

      if (hasAngles && tDAngles.getTime()<=present)
       {
        Iterator<DataConsumer> i=anglesConsumers.iterator();
        while (i.hasNext()) i.next().onNewData(podAngles.setTag(PieceOfData.Tag.Angles));
        pod=readData(brAngles); if (pod.isNoData()) return;
        tDAngles=(TimedPieceOfData)pod;
        podAngles=(V3PieceOfData)tDAngles.getPieceOfData();
       }

      long nextT=0;
      if (hasAcceleration)                                               nextT=(long)tDAcceleration.getTime();
      if (hasAngles && (!hasAcceleration || tDAngles.getTime()<present)) nextT=(long)tDAngles.getTime();

      if (speed>0.0f)
        try                 {
       Thread.sleep((long)((nextT-present)/speed));
      }
        catch (Exception e) {}

      present=nextT;}
    }
  }
