package c.offerak.speedshopper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import c.offerak.speedshopper.activity.LandingScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.MenuActivity;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.android.libraries.places.api.Places;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

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

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        /*int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {

            *//*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             *//*
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i;
                if (isUserLoggedOut) {
                    i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                } else {
                    i = new Intent(SplashScreen.this, LandingScreen.class);
                    startActivity(i);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);*/

        int secondsDelayed = 2;
        new Handler().postDelayed(() -> {
            if (mySharedPreference.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
                startActivity(new Intent(context, MenuActivity.class));
                finish();
            } else {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }
}
