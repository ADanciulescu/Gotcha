package com.example.andrei.gotcha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private int notification_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();
    }

    private void setupButtons(){
        Button btnServiceOn = (Button) findViewById(R.id.btnServiceOn);
        Button btnServiceOff = (Button) findViewById(R.id.btnServiceOff);
        Button btnPlayRec = (Button) findViewById(R.id.btnPlayRec);

        btnServiceOn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startMediaButtonListening();
            }
        });

        btnServiceOff.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                stopMediaButtonListening();

            }
        });

        btnPlayRec.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    playLastRec();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void playLastRec() throws IOException {
        MediaPlayer mp = new MediaPlayer();
        mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gotcha.3gp");
        mp.prepare();
        mp.start();
    }

    public void startMediaButtonListening() {
        Intent serviceIntent = new Intent(getBaseContext(),HeadsetMonitoringService.class);
        startService(serviceIntent);
        presentNotification(Notification.VISIBILITY_PUBLIC, R.drawable.publix, getString(R.string.app_name), "Listening for button");
    }

    public void stopMediaButtonListening() {
        Intent serviceIntent = new Intent(getBaseContext(),HeadsetMonitoringService.class);
        stopService(serviceIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.private_notification) {
            presentNotification(Notification.VISIBILITY_PRIVATE, R.drawable.priv8, getString(R.string.private_title), getString(R.string.private_text));
            return true;
        } else if (id == R.id.public_notification) {
            presentFancyNotification(Notification.VISIBILITY_PUBLIC, R.drawable.publix, getString(R.string.public_title), getString(R.string.public_text));
            return true;
        } else if (id == R.id.secret_notification) {
            presentNotification(Notification.VISIBILITY_SECRET, R.drawable.secret, getString(R.string.secret_title), getString(R.string.secret_text));
            return true;
        } else if (id == R.id.heads_up_notification) {
            presentHeadsUpNotification(Notification.VISIBILITY_PUBLIC, R.drawable.cubs, getString(R.string.heads_up_title), getString(R.string.heads_up_text));
            return true;
        }
        notification_id++;

        return super.onOptionsItemSelected(item);
    }

    private void presentFancyNotification(int visibility, int icon, String title, String text) {

        Intent iLaunchMain = new Intent(this,MainActivity.class);
        PendingIntent piLaunchMain = PendingIntent.getActivity(this.getBaseContext(),0,iLaunchMain,0);
        Notification notification = new NotificationCompat.Builder(this)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(piLaunchMain)
                .setVisibility(visibility).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, notification);
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

    private void presentHeadsUpNotification(int visibility, int icon, String title, String text) {
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse("http://www.wgn.com"));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setCategory(Notification.CATEGORY_PROMO)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setVisibility(visibility)
                .addAction(android.R.drawable.ic_menu_view, getString(R.string.view_details), contentIntent)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}