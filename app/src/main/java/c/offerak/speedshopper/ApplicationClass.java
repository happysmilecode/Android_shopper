package c.offerak.speedshopper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.onesignal.OSDeviceState;
import com.onesignal.OSInAppMessageAction;
import com.onesignal.OneSignal;

import c.offerak.speedshopper.activity.LandingScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;

public class ApplicationClass extends Application implements LifecycleObserver {
    private static final String ONESIGNAL_APP_ID = "88eea516-8527-4bf9-a5a3-717221327c0b";
    MySharedPreference mySharedPreference;//bluedev
    UserBean bean;//bluedev

    Context context;

    public static boolean isForeground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        context = getApplicationContext();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler((OneSignal.OSNotificationOpenedHandler)
                        new NotificationOpenedHandler(this));
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OSDeviceState device = OneSignal.getDeviceState();
        assert device != null;
        MySharedPreference.setSharedPreference(context, "ONESIGNAL_ID", device.getUserId());

//        Log.e("PUSH_ID", device.getUserId());

        OneSignal.setInAppMessageClickHandler(
                new OneSignal.OSInAppMessageClickHandler() {
                    @Override
                    public void inAppMessageClicked(OSInAppMessageAction result) {
                        String clickAction = result.getClickName();
                        if (clickAction == null) {
                            clickAction = "";
                        }
                        final String[] menu_name = {""};
                        if (MySharedPreference.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
                            switch (clickAction) {
                                case "my_list":
                                case "my_profile":
                                case "my_wallet":
                                case "gift_cards":
                                case "notifications":
                                case "coupon_code":
                                case "help":
                                case "contact_us":
                                case "login":
                                    menu_name[0] = clickAction;
                                    break;
                            }
                        } else {
                            switch (clickAction) {
                                case "my_list":
                                case "notifications":
                                case "help":
                                case "login":
                                    menu_name[0] = clickAction;
                                    break;
                            }
                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ((menu_name[0].equals("login"))) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    if (!menu_name[0].equals("")) {
                                        Intent intent = new Intent(context, LandingScreen.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("MENU_NAME", menu_name[0]);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }, 500);
//                        try {
//                            OSInAppMessageTag osInAppMessageTag = result.getTags();
//                            if (osInAppMessageTag != null) {
//                                JSONObject jObj = osInAppMessageTag.getTagsToAdd();
//                                if (jObj.has("flag")) {
//                                    String flag = jObj.getString("flag");
//                                    final String[] menu_name = {""};
//
//
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                });
        //bluedev

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        isForeground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        isForeground = true;
    }
}
