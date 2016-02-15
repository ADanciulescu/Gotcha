package com.example.andrei.gotcha;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by andrei on 2016-01-30.
 */
public class HeadsetMonitoringService extends Service {


    MediaSession session;
    private int notification_id = 2;
    private boolean isRecording = false;

    @Override
    public void onCreate() {


        forceSpeakers();
        forceMicrophone();


        Log.d("MonitorService", "Created");
        session = new MediaSession(getBaseContext(), "TAG");
        session.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull final Intent mediaButtonIntent) {
                KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                Log.d("MonitorService", "SessionCallback.onMediaButton()...  event = " + event);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mediaButtonPress();
                }
                Log.i("TAG", "GOT EVENT");
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });
        session.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);


        session.setActive(true);

    }

    public void mediaButtonPress(){
        if (isRecording){
            stopRecording();
            dismissNotification();
        }
        else {
            startRecording();
            presentNotification(Notification.VISIBILITY_PUBLIC, R.drawable.publix, getString(R.string.app_name), "Recording");
        }
        isRecording = !isRecording;
    }

    private void dismissNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_id);
    }

    private void presentNotification(int visibility, int icon, String title, String text) {
        Notification notification = new NotificationCompat.Builder(this)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setVisibility(visibility).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, notification);
    }

    public void forceMicrophone(){
        AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);
    }

    public void forceSpeakers(){
        final int FOR_MEDIA = 1;
        final int FORCE_SPEAKER = 1;

        Class audioSystemClass = null;
        try {
            audioSystemClass = Class.forName("android.media.AudioSystem");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method setForceUse = null;
        try {
            setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            setForceUse.invoke(null, FOR_MEDIA, FORCE_SPEAKER);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MonitorService", "Destroyed");
        session.release();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public void startRecording(){
        Intent serviceIntent = new Intent(getBaseContext(),RecorderService.class);
        startService(serviceIntent);
    }

    public void stopRecording(){
        stopService(new Intent(getBaseContext(), RecorderService.class));
    }
}