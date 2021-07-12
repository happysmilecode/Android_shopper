package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.fragment.ShoppingListsFragment;
import c.offerak.speedshopper.modal.ShoppingListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.GetShoppingListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class DeepLinkActivity extends AppCompatActivity {
    Context mContext;
    String applink;
    String shareToken;
    Uri uri;
    private boolean isUserLoggedOut = false;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeplink);
        // ATTENTION: This was auto-generated to handle app links.
        mContext = this;
        MySharedPreference mySharedPreference = new MySharedPreference(this);
        isUserLoggedOut = mySharedPreference.isUserLogedOut();
        userBean = mySharedPreference.getLoginDetails();
        apiService = ApiClient.getClient().create(ApiInterface.class);

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        if (appLinkIntent.getData()!=null){
            Uri appLinkData = appLinkIntent.getData();
            // link_txt.setText(appLinkData.toString());
            applink = appLinkData.toString();
            uri = Uri.parse(applink);
            shareToken = uri.getHost();
            Log.d("Share Token-->:", shareToken);

            MySharedPreference.setSharedPreference(mContext, Constants.SHARE_TOKEN, shareToken);

        }

        if (MySharedPreference.getSharedPreferences(mContext, Constants.EVENT_CHECK).equals("1")) {
            shareTokenUpdate();
        } else {
            goToLoginActivity();
        }
    }

    public void shareTokenUpdate()
    {
        if (utils.isNetworkConnected(mContext)) {
            utils.showDialog(mContext);
            Call<GetResponse> call = apiService.updateShareStatus(userBean.getUserToken(), shareToken);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    try {
                        GetResponse tokenResponse = response.body();
                        if (tokenResponse.getStatus() == 200) {
                            utils.hideDialog();
                        } else {
                            utils.hideDialog();
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                        }
                        goToMenuActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void goToMenuActivity()
    {
        Intent intent = new Intent(mContext, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToLoginActivity()
    {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(Constants.ISDEEPLINK, true);
        startActivity(intent);
        finish();
    }
}