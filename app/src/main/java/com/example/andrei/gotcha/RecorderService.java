package com.example.andrei.gotcha;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.security.Timestamp;

/**
 * Created by andrei on 2016-01-31.
 */
public class RecorderService extends Service {
    private MediaRecorder recorder;
    private boolean isRecording = false;

    @Override
    public void onCreate() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gotcha_" + System.currentTimeMillis() + ".3gp");

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("recorder", Environment.getExternalStorageDirectory().getAbsolutePath() + "/myRecording.3gp");
        Log.d("recorder", Environment.getDataDirectory().getAbsolutePath() + "/gotcha_" + Long.toString(System.currentTimeMillis()) + ".3gp");

        if(!isRecording) {
            recorder.start();   // Recording is now started
            isRecording = true;
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release(); // Now the object cannot be reused
        isRecording = false;

        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
