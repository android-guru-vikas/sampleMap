package com.dev.mapdemo.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.dev.mapdemo.MapDemoApp;
import com.dev.mapdemo.R;
import com.dev.mapdemo.activity.MapsActivity;
import com.dev.mapdemo.network.controllers.RetrofitService;
import com.dev.mapdemo.network.interfaces.Api;
import com.dev.mapdemo.network.models.LocationResponseModel;
import com.dev.mapdemo.utils.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GetLocationDataService extends Service {
    private static final String TAG = "GetLocationDataService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callLocationData();
        return START_STICKY;
    }

    @SuppressLint("CheckResult")
    private void callLocationData() {
        Observable<LocationResponseModel> observable = RetrofitService.getInstance().builder().create(Api.class).getLocationUpdates();
       observable.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
               .delay(Constants.KEY_DELAY_SECONDS,TimeUnit.SECONDS)
               .repeat()
               .subscribe(this::handleResults, this::handleError);
    }

    private void handleResults(LocationResponseModel model) {
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentIntent(pendingIntent)
                        .setOnlyAlertOnce(true)
                        .setChannelId(MapDemoApp.getAppContext().getString(R.string.default_notification_channel_id))
                        .setContentText("Lat : " + model.getLatitude() + ", Long : " + model.getLongitude())
                        .setSmallIcon(android.R.drawable.ic_btn_speak_now);
        Notification notification = mBuilder.build();
        CharSequence name = MapDemoApp.getAppContext().getString(R.string.app_name);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        if (mNotificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(MapDemoApp.getAppContext().
                        getString(R.string.default_notification_channel_id), name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }
            mNotificationManager.notify(Constants.FOREGROUND_SERVICE, notification);
            startForeground(Constants.FOREGROUND_SERVICE,notification);
        }
        Intent intent = new Intent(Constants.KEY_NEW_LOCATION);
        intent.putExtra(Constants.KEY_NEW_LAT, model.getLatitude());
        intent.putExtra(Constants.KEY_NEW_LNG, model.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleError(Throwable throwable) {
        Toast.makeText(MapDemoApp.getAppContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}