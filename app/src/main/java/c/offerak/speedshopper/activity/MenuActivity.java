package c.offerak.speedshopper.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OSPermissionState;
import com.onesignal.OneSignal;

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

    public boolean isUpgraded = false;
    public boolean showed = false;

    Dialog dialog;
    ImageView btn_close, earnedImg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;
        init();
        showInAppMessage();
    }


    public void init() {
        isUpgraded = MySharedPreference.getPurchased(context, "membership");
        initVariables();
        ButterKnife.bind(this);
        initClickEvents();
        if (!isUpgraded) {
            loadAds();
        }
    }

    public void  showInAppMessage()
    {
        OneSignal.addTrigger("menu", "loaded");
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

        int login_num = Integer.parseInt(bean.getLogin_num());
        int balance = Integer.parseInt(bean.getBalance());
        showed = MySharedPreference.getPurchased(context, "sstx_earned");
        if (login_num == 1 && balance != 0 && showed) {
            showDialogReward();
            MySharedPreference.setPurchased(context, "sstx_earned", false);
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
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_btn || view.getId() == R.id.logout_btn) {
            logout();
        } else {
            String menu_name="";
            switch (view.getId()) {
                case R.id.my_list_btn:
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
                    menu_name = "pre_made";
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
            }
            Intent intent = new Intent(context, LandingScreen.class);
            intent.putExtra("MENU_NAME", menu_name);
            startActivity(intent);
        }

    }

    public void logout() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(context);
            String token = bean.getUserToken();
            Call<GetResponse> call = apiService.logout(token);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {

                    try {
                        utils.hideDialog();
                        GetResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            int status = tokenResponse.getStatus();
                            String message = tokenResponse.getMessage();
                            try {
                                if (status == 200) {
                                    MySharedPreference mySharedPreference = new MySharedPreference(context);
                                    mySharedPreference.clearPreference(context);
                                    mySharedPreference.setLoginDetails("", "", "", "", "", "", "", "");
                                    startActivity(new Intent(context, LoginActivity.class));
                                    finish();
                                } else {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), message);

//                                            final Handler handler = new Handler();
//                                            handler.postDelayed(() -> {
//                                                startActivity(new Intent(context, LoginActivity.class));
//                                                finish();
//                                            }, 2000);
                                }

//                                        final Handler handler = new Handler();
//                                        handler.postDelayed(() -> {
//                                            startActivity(new Intent(context, LoginActivity.class));
//                                            finish();
//                                        }, 2000);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("menu", "loaded");

    }

    public void showDialogReward() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_earned);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        earnedImg = dialog.findViewById(R.id.earned_image);
        btn_close = dialog.findViewById(R.id.close_btn);
        Glide.with(this).load(R.drawable.earned).into(earnedImg);
        btn_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}
