package c.offerak.speedshopper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;

import com.onesignal.OneSignal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class MobileActivity extends AppCompatActivity {

    public static final String TAG = MobileActivity.class.getSimpleName();
    Context context;
    @BindView(R.id.edtnumber)
    EditText edtnumber;

    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;

    @BindView(R.id.close)
    ImageView closeBtn;

    private ApiInterface apiService;
    private Utils utils = new Utils();
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        context = this;
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @OnClick(R.id.close)
    public void closeView() {
        finish();
    }
    @OnClick(R.id.btnSendVerification)
    public void sendMail() {
        phone = edtnumber.getText().toString();

        if (isValidPhone(phone)) {
            utils.showDialog(MobileActivity.this);
            sendSMSCode(phone);
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please enter valid Phone Number!");
        }
    }

    private void sendSMSCode(String phone) {
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        String token = mySharedPreference.getTempToken();
        if(utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.sendSMS(phone, token);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    try {
                        GetResponse resp = response.body();
                        if (resp != null) {
                            int status = resp.getStatus();
                            String message = resp.getMessage();
                            utils.hideDialog();
                            try {
                                if (status == 200) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), message);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        startActivity(new Intent(MobileActivity.this, VerificationActivity.class));
                                        finish();
                                    }, 3000);

                                } else {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), message);
                                }
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
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to send Verification code!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public boolean isValidPhone(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }
}