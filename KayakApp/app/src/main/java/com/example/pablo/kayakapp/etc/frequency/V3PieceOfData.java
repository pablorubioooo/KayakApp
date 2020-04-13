package com.example.pablo.kayakapp.etc.frequency;

public class V3PieceOfData extends PieceOfData
{
    float v[];

    public V3PieceOfData() {
        noData=true;
    }


    public V3PieceOfData(float vector[]) {
        v=vector; noData=false;
    }


    float[] getV()      {
        return v.clone();
    }


    float   getV(int i) {
        return v[i];
    }


    public TimedPieceOfData addTime(long time)
    {
        return new TV3PieceOfData(time,this);
    }


    public String toString()
    {
        return ""+v[0]+" "+v[1]+" "+v[2];
    }
}
