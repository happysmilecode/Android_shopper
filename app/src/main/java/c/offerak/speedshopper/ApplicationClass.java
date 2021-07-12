package c.offerak.speedshopper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import c.offerak.speedshopper.utils.MySharedPreference;

public class ApplicationClass extends Application {
    private static final String ONESIGNAL_APP_ID = "88eea516-8527-4bf9-a5a3-717221327c0b";

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OSDeviceState device = OneSignal.getDeviceState();
        assert device != null;
        MySharedPreference.setSharedPreference(context, "ONESIGNAL_ID", device.getUserId());

//        Log.e("PUSH_ID", device.getUserId());

    }
}
