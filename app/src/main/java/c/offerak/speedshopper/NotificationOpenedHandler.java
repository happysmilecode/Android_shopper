package c.offerak.speedshopper;

import android.content.Context;
import android.content.Intent;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import c.offerak.speedshopper.activity.NotificationActivity;
import c.offerak.speedshopper.modal.Notification;

public class NotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {

    private Context mContext;

    public NotificationOpenedHandler(Context context) {
        mContext = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
        try {
            Intent intent = new Intent(mContext, ApplicationClass.isForeground ? NotificationActivity.class : SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ApplicationClass.fromFCM, true);
            mContext.startActivity(intent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
