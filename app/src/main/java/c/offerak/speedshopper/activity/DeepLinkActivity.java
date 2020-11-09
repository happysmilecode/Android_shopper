package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import c.offerak.speedshopper.R;

public class DeepLinkActivity extends AppCompatActivity {
    Context mContext;
    String applink;
    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeplink);
        // ATTENTION: This was auto-generated to handle app links.
        mContext = this;
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        if (appLinkIntent.getData()!=null){
            Uri appLinkData = appLinkIntent.getData();
            // link_txt.setText(appLinkData.toString());
            applink = appLinkData.toString();
            uri = Uri.parse(applink);

        }
        /*if (Utility.getSharedPreferences(mContext, Constants.USER_LOGIN).equals("1")) {
            if (applink.contains("offer_detail.php")) {
                offerid = uri.getQueryParameter("offer_id");
                storeid = uri.getQueryParameter("store_id");
                lati = uri.getQueryParameter("latitude");
                longi = uri.getQueryParameter("longitude");
                startActivity(new Intent(DeepLinkActivity.this, DiscountActivity.class)
                        .putExtra("offer_id", offerid)
                        .putExtra("type", offerid)
                        .putExtra("user_lat", offerid)
                        .putExtra("user_long", offerid));
                        *//*.putExtra("store_id", storeid))*//*
            } else {
                storeid = uri.getQueryParameter("store_id");
                lati = uri.getQueryParameter("latitude");
                longi = uri.getQueryParameter("longitude");
                startActivity(new Intent(DeepLinkActivity.this, StoreDetail.class)
                        .putExtra("storeid", storeid)
                        .putExtra("my_lat", lati)
                        .putExtra("my_long", longi));
            }
        } else {
            Utility.alertGuest(mContext);
            //Utility.showToast(mContext, getString(R.string.guest_msg));
        }*/
    }
}