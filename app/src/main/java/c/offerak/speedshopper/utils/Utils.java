package c.offerak.speedshopper.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    private ProgressDialog progressDoalog;

    public void showSnackBar(View view, String snackText) {
        Snackbar.make(view, snackText, Snackbar.LENGTH_SHORT).show();
    }

    public void showDialog(Context context) {
        progressDoalog = new ProgressDialog(context);
        progressDoalog.setMessage("Please wait...");
        progressDoalog.show();
    }

    public boolean isDialogShowing() {
        if (progressDoalog != null && progressDoalog.isShowing()) {
            progressDoalog.dismiss();
            return true;
        }
        return false;
    }

    public void hideDialog(){
        try {
            progressDoalog.dismiss();
        }
        catch (Exception e){
            Log.e("Illegal Exception :", e.toString());
        }
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
