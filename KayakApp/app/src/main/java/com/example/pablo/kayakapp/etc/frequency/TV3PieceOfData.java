package com.example.pablo.kayakapp.etc.frequency;

public class TV3PieceOfData extends TimedPieceOfData
 {
  float v[];

  public TV3PieceOfData() {
   noData=true;
  }


  public TV3PieceOfData(long time, float vector[]) {
   super(time); v=vector; noData=false;
  }


  public TV3PieceOfData(long time, PieceOfData pod) {
   this(time,((V3PieceOfData)pod).getV());
  }


  @Override
  public PieceOfData getPieceOfData() {
   return new V3PieceOfData(getV());
  }


  float[] getV()      {
   return v.clone();
  }


  float   getV(int i) {
   return v[i];
  }


  public String toString()
   {
    return ""+t+" "+v[0]+" "+v[1]+" "+v[2];
   }
  }
