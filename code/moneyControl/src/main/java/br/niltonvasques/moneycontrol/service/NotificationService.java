package br.niltonvasques.moneycontrol.service;

import br.niltonvasques.moneycontrol.MainActivity;
import br.niltonvasques.moneycontrolbeta.R;
import br.niltonvasques.moneycontrol.app.MoneyControlApp;
import br.niltonvasques.moneycontrol.business.ContaAPagarBusiness;
import br.niltonvasques.moneycontrol.database.bean.ContaAPagar;
import br.niltonvasques.moneycontrol.util.DateUtil;

import android.util.Log;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import android.media.RingtoneManager;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;

import java.util.Calendar;
import java.util.Random;
import java.util.Vector;
import java.util.List;
import java.util.GregorianCalendar;

public class NotificationService extends IntentService {

    private MoneyControlApp app;

    public NotificationService() {
        super("NotificationService");
    }

    public NotificationService(String name) {
        super(name);
    }

    private static final int NOTIFICATION_UPDATE_REMINDER   = 0;
    private static final int NOTIFICATION_PAY_BILLS         = 1;
    /*
     * (non-Javadoc)
     * 
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d("MoneyControlNotificationService", "onHandleIntent");
        app = (MoneyControlApp) getApplication();

        boolean delayedContaAPagar = false;

        try {
            GregorianCalendar now = new GregorianCalendar();
            now.add(Calendar.DAY_OF_MONTH, 2);
            GregorianCalendar lastDay = DateUtil.getLastDayFromCurrentMonth(); 
            GregorianCalendar firstDay = DateUtil.getFirstDayFromCurrentMonth();
            firstDay.add(Calendar.MONTH, -12);

            List<ContaAPagar> contas =
                ContaAPagarBusiness.getContaAPagarsOnMonth(app.getDatabase(), firstDay, lastDay);
            //Log.d("NotificationService", "contasAPagar: "+contas.size());
            for(ContaAPagar c : contas){
                //Log.d("NotificationService", "ContaAPagar: "+c.getDescricao()+" "+c.getData());
                GregorianCalendar date = new GregorianCalendar();
                date.setTime(DateUtil.sqlDateFormat().parse(c.getData()));
                boolean isPaga = c.isPaga(app.getDatabase(), date);
                boolean delayed = now.compareTo(date) >= 0;
                //Log.d("NotificationService", " paga ? "+isPaga+" atrasada ? "+delayed+now.compareTo(date)+" days");
                if(!isPaga && delayed) delayedContaAPagar = true;
            }
        } catch (Exception e){
            //Log.d("NotificationService", e.getMessage());
        }

        if(!delayedContaAPagar){
            //Log.d("MoneyControlNotificationService", "lembrete de atualização das finanças!");
            String title = getString(R.string.notification_title);
            String text = getString(R.string.notification_text);
            showNotification(NOTIFICATION_UPDATE_REMINDER, title, text);
        }else{
            //Log.d("MoneyControlNotificationService", "lembrete de pagamento de contas!");
            String title = getString(R.string.notification_pagar_contas_title);
            String text = getString(R.string.notification_pagar_contas);
            showNotification(NOTIFICATION_PAY_BILLS, title, text);
        }
    }

    private void showNotification(int id, String title, String message) {

        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(getSmallIcon())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true)
                .setSound(soundUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(ContextCompat.getColor(this, getBGColor(id)));
        }
        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(0, notification);

        NotificationService.schedulerNotification(this);
    }

    private PendingIntent getPendingIntent(){
        return PendingIntent.getActivity(this, 0, 
                            new Intent(this, MainActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private int getSmallIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.ic_mask;
        } else {
            return R.drawable.ic_launcher;
        }
    }

    private int getBGColor(int notificationId) {
        if(notificationId == NOTIFICATION_UPDATE_REMINDER)
            return R.color.money_green;
        return R.color.red;
    }

    public static void schedulerNotification(Context context){
        //Log.d("MoneyControlNotificationService", "schedulerNotification");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent it = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        // set the triggered time to currentHour:08:00 for testing
        if(br.niltonvasques.moneycontrolbeta.BuildConfig.DEBUG){
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.MINUTE, 1);
        }else{
            calendar.set(Calendar.HOUR, 20);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        ////Log.d("MoneyControlNotificationService", "setAlarmManager "+calendar.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }
}
