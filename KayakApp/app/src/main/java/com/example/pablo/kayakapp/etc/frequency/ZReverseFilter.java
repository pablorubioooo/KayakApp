package com.example.pablo.kayakapp.etc.frequency;

import android.util.Log;

public class ZReverseFilter implements DataTransformer
 {
 	DataConsumer dataConsumer;
	TFPieceOfData	podData = null;

  public ZReverseFilter(DataConsumer dc)
   {
   	dataConsumer=dc;
   }

  public PieceOfData getData(PieceOfData tPod)
   {
		return podData;
	}


public void onNewData(PieceOfData tPod){

	PieceOfData pod =  tPod;

	if (pod.isNoMoreData()) return;
	if (!pod.isNoData())
	{
		if(pod instanceof TPieceOfData) {
			dataConsumer.onNewData(pod);
			return;
		}

		TV3PieceOfData tv3pod=(TV3PieceOfData)pod;
		TFPieceOfData tfdata = new TFPieceOfData(tv3pod.getTime(),tv3pod.getV(2));
		podData = tfdata;
		dataConsumer.onNewData(tfdata);
		//Log.d("*.*.*.*.*.*.*.*.*.", "----> DATA PATH <----"+"3 - Zfilter");
	}
}
}
