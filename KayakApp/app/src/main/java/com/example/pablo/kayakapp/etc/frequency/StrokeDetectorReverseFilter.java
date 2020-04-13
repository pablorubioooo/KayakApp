package com.example.pablo.kayakapp.etc.frequency;


public class StrokeDetectorReverseFilter implements DataTransformer
{
    DataProducer producer; DataConsumer consumer;
    PieceOfData presentValue, lastValue, lastStroke;
    TFPieceOfData minimumDerivative;
    float derivativeThreshold, accelerationThreshold;
    enum State {Idle,Detecting}
    State state;

    public StrokeDetectorReverseFilter(DataConsumer dc,double dT, float aT)
    {
        consumer=dc; derivativeThreshold=(float)dT; accelerationThreshold=aT;
        presentValue=lastValue=lastStroke=new PieceOfData();
        state=State.Idle;
    }

    public void onNewData(PieceOfData tPod)
    {
        PieceOfData pod=tPod;
        if (pod.isNoMoreData()) return;
        if (presentValue.isNoData()) {
            lastValue=presentValue;presentValue=pod;
        }

        if(pod instanceof TPieceOfData) {/*consumer.onNewData(pod);*/ return;}
        TFPieceOfData pV=(TFPieceOfData)pod;
        TFPieceOfData lV=(TFPieceOfData)presentValue;

        presentValue=pV;
        lastValue=lV;
        TFPieceOfData presentDerivative=new TFPieceOfData(pV.getTime(),(pV.getValue()-lV.getValue())/(pV.getTime()-lV.getTime()));

        switch (state)
        {
            case Idle:
                //Log.d("*.*.*.*.*.*.*.*.*.", "preDer - "+presentDerivative.getValue()+" ** derTH - "+derivativeThreshold);
                if (presentDerivative.getValue()<derivativeThreshold) //NUNCA SE METE AQUI
                {
                    minimumDerivative=presentDerivative;
                    state=State.Detecting;
                }
                break;

            case Detecting:
                if (presentDerivative.getValue()<minimumDerivative.getValue())
                    minimumDerivative=presentDerivative;
                else if (presentDerivative.getValue()>0.0f)
                {
                    if (pV.getValue()<accelerationThreshold)
                {lastStroke=presentValue;
                    consumer.onNewData(new TPieceOfData(pV.getTime()));
                   //Log.d("*.*.*.*.*.*.*.*.*.", "----> DATA PATH <----"+"5 - StrokeDetector" + pV.getValue());
                }
                    state=State.Idle;}
                break;
        }
    }


    public PieceOfData getData(PieceOfData tPod)
    {
        return tPod;
    }

}



