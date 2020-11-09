package c.offerak.speedshopper.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onNewToken(String s) {
        Log.e(TAG, "Refreshed token: " + s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        MySharedPreference.setSharedPreference(getApplicationContext(), Constants.FIREBASE_TOKEN,token);
    }
}