package com.example.pablo.kayakapp.etc.frequency;

public abstract class TimedPieceOfData extends PieceOfData
{
    protected long t;
    TimedPieceOfData(){
        t=0;
    }


    TimedPieceOfData(long time) {
        t=time;
    }


    public abstract PieceOfData getPieceOfData(); // Without time


    public TimedPieceOfData addTime(long time)
    {/* TODO: change time? */
        return this;
    }


    public long getTime() {
        return t;
    }


    void setTIme(long time) {t=time;}
}
