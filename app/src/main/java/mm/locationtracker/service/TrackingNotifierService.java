package mm.locationtracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import mm.locationtracker.utility.CustomConstants;
import mm.locationtracker.utility.CustomDate;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class TrackingNotifierService extends Service {

    Handler mHandler = new Handler();

    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
        stopRepeatingTask();
        return START_STICKY;
    }

    Runnable mHandlerTask =  new Runnable() {
        @Override
        public void run() {
            long currentTimestampInSeconds = System.currentTimeMillis() / CustomDate.SEC;
            if (currentTimestampInSeconds % 60 == 0) {
                sendBroadCasts();
            }

            mHandler.postDelayed(mHandlerTask, 1000);
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

    private void sendBroadCasts() {
        Intent trackingIntent = new Intent(CustomConstants.SEND_TRACKING_MAIL);
        sendBroadcast(trackingIntent);
    }
}
