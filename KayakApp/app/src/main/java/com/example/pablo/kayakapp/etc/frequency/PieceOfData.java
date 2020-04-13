package com.example.pablo.kayakapp.etc.frequency;

public class PieceOfData
{
 protected boolean noData=true,noMoreData=false;

 public enum Tag {
  NoTag,Temperature,Acceleration,AngularVelocity,Angles
 }


 Tag tag;
 public PieceOfData() {
  noData=true; noMoreData=false;tag=Tag.NoTag;
 }


 public static PieceOfData getNoMoreData()
 {
  PieceOfData pod=new PieceOfData();
  pod.noMoreData=true;
  return pod;
 }


 public boolean isNoData()     {
  return noData || noMoreData;
 } // noMoreData => noData


 public boolean isNoMoreData() {
  return noMoreData;
 }


 public TimedPieceOfData addTime(long time)
 {
  return new TPieceOfData(time);
 }


 public String toString() {
  return noMoreData?"NOMOREDATA":"NODATA";
 }


 public PieceOfData setTag(Tag t) {
  tag=t;return this;
 }


 public Tag  getTag(){
  return tag;
 }


 public String getStringTag()
 {
  switch (tag)
 {
   case NoTag:           return "";
   case Temperature:     return "Temperature:degC";
   case Acceleration:    return "Acceleration:m/s2";
   case AngularVelocity: return "AngularVelocity:deg/s";
   case Angles:          return "Roll/Pitch/Yaw:deg";}
   return "?";
 }
}