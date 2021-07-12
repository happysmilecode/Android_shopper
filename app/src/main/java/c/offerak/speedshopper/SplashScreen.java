package c.offerak.speedshopper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import c.offerak.speedshopper.activity.LandingScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.MenuActivity;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.libraries.places.api.Places;
import com.onesignal.OSPermissionState;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity  {

    private boolean isUserLoggedOut = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        context=this;
        MySharedPreference mySharedPreference = new MySharedPreference(this);
        isUserLoggedOut = mySharedPreference.isUserLogedOut();
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
//        OneSignal.addSubscriptionObserver(this);


        int secondsDelayed = 2;
        new Handler().postDelayed(() -> {
            if (MySharedPreference.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
                startActivity(new Intent(this, MenuActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, secondsDelayed * 1000);
    }

}
