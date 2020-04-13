package com.example.pablo.kayakapp.etc.frequency;

public class TPieceOfData extends TimedPieceOfData
{
 public TPieceOfData() {
  noData=true;
 }


 public TPieceOfData(long time) {
  super(time); noData=false;
 }


 public TPieceOfData(long time,PieceOfData pod) {
  this(time);
 }


 @Override
 public PieceOfData getPieceOfData() {
  return new PieceOfData();
 }


 public String toString()
 {
  return ""+t;
 }
}
