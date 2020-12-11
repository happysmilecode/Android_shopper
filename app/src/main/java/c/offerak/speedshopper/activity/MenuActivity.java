package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    @BindView(R.id.my_list_btn)
    public LinearLayout btnList;
    @BindView(R.id.my_list_btn1)
    public LinearLayout btnList1;
    @BindView(R.id.my_profile_btn)
    public LinearLayout btnProfile;
    @BindView(R.id.my_wallet_btn)
    public LinearLayout btnWallet;
    @BindView(R.id.sstx_btn)
    public LinearLayout btnSSTX;
    @BindView(R.id.notification_btn)
    public LinearLayout btnNotifications;
    @BindView(R.id.notification_btn1)
    public LinearLayout btnNotifications1;
    @BindView(R.id.coupon_code_btn)
    public LinearLayout btnCouponCodes;
    @BindView(R.id.help_btn)
    public LinearLayout btnHelp;
    @BindView(R.id.help_btn1)
    public LinearLayout btnHelp1;
    @BindView(R.id.contactus_btn)
    public LinearLayout btnContactUs;
    @BindView(R.id.logout_btn)
    public LinearLayout btnLogout;
    @BindView(R.id.login_btn)
    public LinearLayout loginBtn;
    private Utils utils = new Utils();
    UserBean bean;
    MySharedPreference mySharedPreference;
    private ApiInterface apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;
        init();
    }


    public void init() {
        initVariables();
        ButterKnife.bind(this);
        initClickEvents();
        loadAds();
    }

    private void initVariables() {
        mySharedPreference = new MySharedPreference(context);
        bean = mySharedPreference.getLoginDetails();
        apiService = ApiClient.getClient().create(ApiInterface.class);

        LinearLayout loginLayout = (LinearLayout) findViewById(R.id.login_option);
        LinearLayout guestLayout = (LinearLayout) findViewById(R.id.guest_option);

        Bundle b = getIntent().getExtras();
        String from = "login"; // or other values
        if(bean.getFlow() != null) {
            from = bean.getFlow();
            if(from.equals("guest")) {
                loginLayout.setVisibility(LinearLayout.GONE);
                guestLayout.setVisibility(LinearLayout.VISIBLE);
            }else{
                loginLayout.setVisibility(LinearLayout.VISIBLE);
                guestLayout.setVisibility(LinearLayout.GONE);
            }
        }else{
            loginLayout.setVisibility(LinearLayout.VISIBLE);
            guestLayout.setVisibility(LinearLayout.GONE);
        }
    }

    void initClickEvents() {
        btnList.setOnClickListener(this);
        btnList1.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnWallet.setOnClickListener(this);
        btnSSTX.setOnClickListener(this);
        btnNotifications.setOnClickListener(this);
        btnNotifications1.setOnClickListener(this);
        btnCouponCodes.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnHelp1.setOnClickListener(this);
        btnContactUs.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    void loadAds() {
        AdView adView = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    public void onClick(View view) {
        String menu_name="";
        switch (view.getId()) {
            case R.id.my_list_btn:
                menu_name = "my_list";
                break;
            case R.id.my_list_btn1:
                menu_name = "my_list";
                break;
            case R.id.my_profile_btn:
                menu_name = "my_profile";
                break;
            case R.id.my_wallet_btn:
                menu_name = "my_wallet";
                break;
            case R.id.sstx_btn:
                menu_name = "sstx_market";
                break;
            case R.id.notification_btn:
                menu_name = "notifications";
                break;
            case R.id.notification_btn1:
                menu_name = "notifications";
                break;
            case R.id.coupon_code_btn:
                menu_name = "coupon_code";
                break;
            case R.id.help_btn:
                menu_name = "help";
                break;
            case R.id.help_btn1:
                menu_name = "help";
                break;
            case R.id.contactus_btn:
                menu_name = "contact_us";
                break;
            case R.id.logout_btn:
            case R.id.login_btn:
                if (utils.isNetworkConnected(context)) {
                    String token = bean.getUserToken();
                    Call<GetResponse> call = apiService.logout(token);
                    call.enqueue(new Callback<GetResponse>() {
                        @Override
                        public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {

                            try {
                                GetResponse tokenResponse = response.body();
                                if (tokenResponse != null) {
                                    int status = tokenResponse.getStatus();
                                    String message = tokenResponse.getMessage();
                                    try {
                                        if (status == 200) {
                                            MySharedPreference mySharedPreference = new MySharedPreference(context);
                                            mySharedPreference.clearPreference(context);
                                            mySharedPreference.setLoginDetails("", "", "", "", "", "");
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        } else {
                                            utils.showSnackBar(getWindow().getDecorView().getRootView(), message);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(() -> {
                                                startActivity(new Intent(context, LoginActivity.class));
                                                finish();
                                            }, 2000);
                                        }

                                        final Handler handler = new Handler();
                                        handler.postDelayed(() -> {
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        }, 2000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetResponse> call, Throwable t) {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                        }
                    });
                } else {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "You are not connected to internet!");
                }
                return;
        }
        Intent intent = new Intent(context, LandingScreen.class);
        intent.putExtra("MENU_NAME", menu_name);
        startActivity(intent);
    }
}