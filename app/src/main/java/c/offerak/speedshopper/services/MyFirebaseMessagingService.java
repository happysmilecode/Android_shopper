package c.offerak.speedshopper.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

import c.offerak.speedshopper.activity.NotificationActivity;
import c.offerak.speedshopper.rest.Constants;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

            JSONObject object = new JSONObject(remoteMessage.getData());
            try {
                if (object.getString("type").equalsIgnoreCase("buy")) {

                    Intent intent = new Intent(this, NotificationActivity.class);
                    Random random = new Random();

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, random.nextInt(1000) * 5, intent,
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0);                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.showUniqueNotification(object.getString(Constants.MESSAGE),
                            pendingIntent, true, "", 0);

                }else if(object.getString("type").equalsIgnoreCase("info")){

                    Intent intent = new Intent(this, NotificationActivity.class);
                    Random random = new Random();

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, random.nextInt(1000) * 5, intent,
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0);
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.showUniqueNotification(object.getString(Constants.MESSAGE),
                            pendingIntent, true, "", 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
