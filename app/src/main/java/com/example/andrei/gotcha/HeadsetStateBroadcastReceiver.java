package com.example.andrei.gotcha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by andrei on 2016-01-30.
 */
public class HeadsetStateBroadcastReceiver extends BroadcastReceiver {

    public static final String[] HEADPHONE_ACTIONS = {
            Intent.ACTION_HEADSET_PLUG,
            //Intent.ACTION_MEDIA_BUTTON
    };

    @Override
    public void onReceive(final Context context, final Intent intent) {

        boolean broadcast = false;
        Log.d("MediaButtonReceiver", intent.getAction());

        // Wired headset monitoring
        if (intent.getAction().equals(HEADPHONE_ACTIONS[0])) {
            final int state = intent.getIntExtra("state", 0);
            //AudioPreferences.setWiredHeadphoneState(context, state > 0);
            broadcast = true;
        }


        // Used to inform interested activities that the headset state has changed
        if (broadcast) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("headsetStateChange"));
        }

    }
}