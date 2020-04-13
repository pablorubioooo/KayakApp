package com.example.pablo.kayakapp.etc.frequency;

public class TFPieceOfData extends TimedPieceOfData
 {
  float value;

  public TFPieceOfData() {
   noData=true;
  }


  public TFPieceOfData(long time, float v) {
   super(time); value=v; noData=false;
  }


  public TFPieceOfData(long time, PieceOfData pod) {
   this(time, ((FPieceOfData)pod).getValue());
  }


  @Override
  public PieceOfData getPieceOfData() {
   return new FPieceOfData(value);
  }


  float   getValue()  {
   return value;
  }



  public String toString()
   {
    return ""+t+" "+value;
   }
  }
