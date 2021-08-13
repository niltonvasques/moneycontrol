package br.niltonvasques.moneycontrol.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MoneyControlReminderScheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //NewVideosNotificationService.scheduleService(context);
        ////Log.d("MoneyControlReminderScheduler", "onReceiver");
        NotificationService.schedulerNotification(context);
    }
}
