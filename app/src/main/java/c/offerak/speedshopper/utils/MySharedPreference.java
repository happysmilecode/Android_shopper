package c.offerak.speedshopper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import c.offerak.speedshopper.modal.UserBean;

public class MySharedPreference {

    private static Context mcontext;
    private Context mContext;
    private String myPref = "myPref";
    public static final String PREFERENCE = "SpeedShopper";

    public MySharedPreference(Context context) {
        this.mContext = context;
    }

    public static void setSharedPreference(Context context, String name, String value) {
        mcontext = context;
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        // editor.clear();
        editor.putString(name, value);
        editor.apply();
    }

    public static String getSharedPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        return settings.getString(name, "");
    }

    public void setLoginDetails(String email, String name, String proPic, String token, String id, String flow) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.putString("userName", name);
        editor.putString("userImage", proPic);
        editor.putString("userToken", token);
        editor.putString("flow", flow);
        editor.putString("userId", id);
        editor.apply();
    }

    public UserBean getLoginDetails() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(myPref, Context.MODE_PRIVATE);

        UserBean bean = new UserBean();
        bean.setUserName(sharedPreferences.getString("userName", null));
        bean.setUserMail(sharedPreferences.getString("userEmail", null));
        bean.setUserImage(sharedPreferences.getString("userImage", null));
        bean.setUserToken(sharedPreferences.getString("userToken", null));
        bean.setUserId(sharedPreferences.getString("userId", null));
        bean.setFlow(sharedPreferences.getString("flow", null));

        return bean;
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", "");
    }

    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", "").isEmpty();
    }

    public int increaseAndGetShowAdsCounter() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int adsCounter = sharedPreferences.getInt("ads_counter", 0);
        adsCounter = (adsCounter + 1) % 5;
        editor.putInt("ads_counter", adsCounter );
        editor.apply();
        return adsCounter;
    }

    public int getAdsCounter() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("ads_counter", 0);
    }

    public void clearPreference(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        settings.edit().clear().apply();
    }
}
