package mm.locationtracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import mm.locationtracker.database.helper.DatableHandler;
import mm.locationtracker.utility.CustomConstants;
import mm.locationtracker.utility.CustomDate;
import mm.locationtracker.utility.SendMailInvoker;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class TrackingNotifierService extends Service {

    Handler mHandler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRepeatingTask();
        return START_STICKY;
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            try {

                String minutesValue = CustomDate.getCurrentFormattedDateMinutesValue();
                int minutesValueInt = Integer.parseInt(minutesValue);

                String hourValue = CustomDate.getCurrentFormattedDateHourValue();
                int hourValueInt = Integer.parseInt(hourValue);

                //Send the mail before deleting DB
                if (minutesValueInt == 0 || minutesValueInt == 30) {
                    sendMail();
                }

                //Delete the DB
                if (hourValueInt == 0 && minutesValueInt == 0) {
                    DatableHandler datableHandler = new DatableHandler(TrackingNotifierService.this, "LOCATION_COORDINATES");
                    datableHandler.deleteDb();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(mHandlerTask, 30 * 1000);
        }
    };

    public void startRepeatingTask() {
        mHandlerTask.run();
    }

    @Override
    public void onDestroy() {
        stopRepeatingTask();
        super.onDestroy();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMail() {
        SendMailInvoker sendMailInvoker = new SendMailInvoker(this);
        sendMailInvoker.sendMail();
    }

    private void sendBroadCasts() {
        Intent trackingIntent = new Intent(CustomConstants.SEND_TRACKING_MAIL);
        sendBroadcast(trackingIntent);
    }
}
