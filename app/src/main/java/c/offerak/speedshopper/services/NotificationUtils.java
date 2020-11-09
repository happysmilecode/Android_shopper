package c.offerak.speedshopper.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import java.util.Arrays;
import java.util.List;

import c.offerak.speedshopper.R;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class NotificationUtils {
    private final String TAG = NotificationUtils.class.getSimpleName();
    private final String separatorString = "<1>";
    private final int appIconAlpha = R.drawable.notification_icon;

    // TODO : Add Alpha and no alpha images for the notifications
    private final int appIconNoAlpha = R.drawable.notification_icon;
    private Context context;

    public NotificationUtils(Context context) {
        this.context = context;
    }

    public void showGroupedNotifications(String messageOrNotification, String sharedPreferenceName, String preferenceKey
            , PendingIntent pendingIntent, boolean showNotification, String subText, int notificationType) {
        if (showNotification) {
            GroupNotificationsManager manager;
            manager = new GroupNotificationsManager(context, sharedPreferenceName, preferenceKey);
            manager.addGroupedNotification(messageOrNotification);

            String oldNotification = manager.getGroupedNotifications();
            List<String> messagesStringList = Arrays.asList(oldNotification.split(separatorString));
            int size = messagesStringList.size();

            if (size > 1) {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                for (int i = size - 1; i >= 0; i--) {
                    inboxStyle.addLine(messagesStringList.get(i));
                }
                showAllKindNotification(messageOrNotification, pendingIntent, subText, inboxStyle, notificationType, notificationType);
            } else {
                NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle().bigText(messageOrNotification);
                showAllKindNotification(messageOrNotification, pendingIntent, subText, inboxStyle, notificationType, notificationType);
            }
        }
    }

    public void showUniqueNotification(String messageOrNotification, PendingIntent pendingIntent, boolean showNotification, String subText, int notificationType) {
        if (showNotification) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(messageOrNotification);
            showAllKindNotification(messageOrNotification, pendingIntent, subText, bigTextStyle, notificationType, (int) (System.currentTimeMillis() / 1000));
        }
    }

    // Send false to remove previous notification on receiving new and true to show every Notification separately
    private void showAllKindNotification(String messageOrNotification, PendingIntent pendingIntent
            , String subText, NotificationCompat.Style inboxStyle, final int notificationType, final int notificationId) {

        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;
        String CHANNEL_ID = context.getString(R.string.app_name);// The id of the channel.
        CharSequence name = context.getString(R.string.app_name);// The user-visible name of the channel.
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(appIconNoAlpha)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentText(messageOrNotification)
                .setSound(getNotificationTone())
                .setStyle(inboxStyle)
                .setContentIntent(pendingIntent)
                .setSubText(subText)
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), appIconAlpha));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.notification_icon);
            mBuilder.setColor(context.getResources().getColor(R.color.white));
        } else {
            mBuilder.setSmallIcon(R.drawable.notification_icon);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(notificationId, mBuilder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert mChannel != null;
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    public void clearNotificationType(int notificationType) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Uri getNotificationTone() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public void clearGroupedNotification(String sharedPreferenceName, String preferenceKey) {
        GroupNotificationsManager manager;
        manager = new GroupNotificationsManager(context, sharedPreferenceName, preferenceKey);
        manager.clearGroupedNotification();
    }

    public void clearAllNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();
    }

    // If you don't need to pass any data to your activity
    public PendingIntent addActivityStacks(Class... classesStackTrace) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        for (Class classesStackTraces : classesStackTrace) {
            stackBuilder.addNextIntent(new Intent(context, classesStackTraces));
        }
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // For Getting StackTrace and Add data in the result Intent
    public TaskStackBuilder getActivityStack(Class... classesStackTrace) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        for (Class classesStackTraces : classesStackTrace) {
            stackBuilder.addNextIntent(new Intent(context, classesStackTraces));
        }
        return stackBuilder;

        /**
         *****  Example for adding a intent *****
         stackBuilder.addNextIntent(new Intent(context,YourFinalActivity.class).putExtra("key","value"));
         PendingIntent pendingIntent= stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
         */

    }

    /*public void showPictureNotification(String url, final PendingIntent pendingIntent)
    {
        final NotificationCompat.BigPictureStyle[] bigPictureStyle = new NotificationCompat.BigPictureStyle[1];
        // TODO : Add Glide Dependency
        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>()
        {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
            {
                bigPictureStyle[0] = new NotificationCompat.BigPictureStyle().bigPicture(resource);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                Notification notification = mBuilder
                        .setStyle(bigPictureStyle[0])
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(appIconNoAlpha)
                        .setSound(getNotificationTone()).build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) (System.currentTimeMillis()/1000), notification);
            }
        });
    }*/

    public class GroupNotificationsManager {
        private final String PREFERENCE_KEY_NAME;
        private SharedPreferences sharedPreferences;

        public GroupNotificationsManager(Context context, String sharedPreferenceName, String preferenceKey) {
            sharedPreferences = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            PREFERENCE_KEY_NAME = preferenceKey;
        }

        public void addGroupedNotification(String MessageOrNotification) {
            String oldMessages = sharedPreferences.getString(PREFERENCE_KEY_NAME, null);
            if (oldMessages == null) {
                oldMessages = MessageOrNotification;
            } else {
                oldMessages += separatorString + MessageOrNotification;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREFERENCE_KEY_NAME, oldMessages);
            editor.apply();
        }

        public String getGroupedNotifications() {
            return sharedPreferences.getString(PREFERENCE_KEY_NAME, null);
        }

        public void clearGroupedNotification() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    /*public class RealmNotificationModel extends RealmObject
    {
        @PrimaryKey
        private int notificationId;
        private int notificationType;

        public RealmNotificationModel()
        {
        }

        public RealmNotificationModel(int notificationId, int notificationType)
        {
            this.notificationId = notificationId;
            this.notificationType = notificationType;
        }

        public int getNotificationId()
        {
            return notificationId;
        }

        public int getNotificationType()
        {
            return notificationType;
        }
    }*/
}