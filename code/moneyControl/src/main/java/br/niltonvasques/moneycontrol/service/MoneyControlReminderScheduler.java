package br.niltonvasques.moneycontrol.service;

import br.niltonvasques.moneycontrol.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

public class MoneyControlReminderScheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //NewVideosNotificationService.scheduleService(context);
        ////Log.d("MoneyControlReminderScheduler", "onReceiver");
        NotificationService.schedulerNotification(context);
    }
}
