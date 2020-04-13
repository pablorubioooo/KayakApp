package com.example.pablo.kayakapp.etc.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Pablo on 18/06/2018.
 */

public class SoundManager {

    private Context pContext;
    private SoundPool sndPool;


    public SoundManager(Context appContext){
        sndPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        pContext = appContext;
    }

    public int load(int idSonido){
        int snd = sndPool.load(pContext, idSonido, 1);
        return snd;
    }

    public void play (int idSonido){
        sndPool.play(idSonido, 1, 1, 0, 0, 1);    }
}
