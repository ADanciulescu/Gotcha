package com.example.andrei.gotcha;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by andrei on 2016-01-30.
 */
public class HeadsetMonitoringService extends Service {


    MediaSession session;
    HeadsetStateBroadcastReceiver headsetStateReceiver;

    @Override
    public void onCreate() {

        headsetStateReceiver = new HeadsetStateBroadcastReceiver();

        final IntentFilter filter = new IntentFilter();
        for (String action: HeadsetStateBroadcastReceiver.HEADPHONE_ACTIONS) {
            filter.addAction(action);
        }
        registerReceiver(headsetStateReceiver,filter);

        Log.d("TAG", "MonitorService");
        session = new MediaSession(getBaseContext(), "TAG");
        session.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull final Intent mediaButtonIntent) {
                KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                Log.i("headsetService", "SessionCallback.onMediaButton()...  event = " + event);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent("headsetStateChange"));
                }
                Log.i("TAG", "GOT EVENT");
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });
        session.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .build();
        session.setPlaybackState(state);

        session.setActive(true);

    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        session.release();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

}