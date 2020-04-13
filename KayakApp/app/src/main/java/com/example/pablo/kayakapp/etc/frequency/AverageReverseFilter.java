package com.example.pablo.kayakapp.etc.frequency;

import android.util.Log;

public class AverageReverseFilter implements DataTransformer
 {
  DataConsumer dataConsumer;
  int nValues, maxValues;
  TFPieceOfData values[];
  int index; float sum;

  public AverageReverseFilter(DataConsumer dc, int mV)
   {
       dataConsumer=dc;
       maxValues=mV;
       nValues=0;
       values=new TFPieceOfData[maxValues];
       index=0;
       sum=0.0f;
       TFPieceOfData tfpod=new TFPieceOfData();

       for (int i=0; i<mV; ++i) values[i]=tfpod;
   }

  public PieceOfData getData(PieceOfData tPod)
   {
    return tPod;
    }


public void onNewData(PieceOfData tPod){

	PieceOfData pod =  (PieceOfData) tPod;
    TFPieceOfData pod2 = null;

	if (pod.isNoMoreData()) return;
	if (!pod.isNoData())
	{
		if(pod instanceof TPieceOfData)
		{
		    dataConsumer.onNewData(pod);
		    return;
		}

        if (!values[index].isNoData())
        {
            sum-=values[index].getValue();
            nValues--;
        }

        values[index]=(TFPieceOfData) pod;

        if (!values[index].isNoData())
        {
            sum+=values[index].getValue();
            nValues++;
        }

    	index=(index+1)%maxValues;
        pod2=(TFPieceOfData)pod;

        if (nValues==0 /*|| !(pod instanceof TPieceOfData)*/) return ;

    	TFPieceOfData tfPod = new TFPieceOfData(pod2.getTime(),sum/(float)nValues);

	    dataConsumer.onNewData(tfPod);
        //Log.d("*.*.*.*.*.*.*.*.*.", "----> DATA PATH <----"+"4 - AverageRV"+tfPod.getValue());

    }
}
}
