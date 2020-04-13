package com.example.pablo.kayakapp.etc.frequency;

public class FPieceOfData extends PieceOfData
{float f;

    public FPieceOfData()         {noData=true;}
    public FPieceOfData(float fv) {f=fv; noData=false;}
    float  getValue()             {return f;}

    public TimedPieceOfData addTime(long time)
    {return new TFPieceOfData(time,this);}

    public String toString()
    {return ""+f;}
}
