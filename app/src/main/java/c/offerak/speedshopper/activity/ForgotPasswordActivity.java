package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.onesignal.OneSignal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    Context context;
    @BindView(R.id.edtEmail)
    EditText edtEmail;

    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;

    private ApiInterface apiService;
    private Utils utils = new Utils();
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = this;
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        OneSignal.addTrigger("forgot", "loaded");
    }

    @OnClick(R.id.btnSend)
    public void sendMail() {
        email = edtEmail.getText().toString();

        if (isValidEmail(email)) {
            utils.showDialog(ForgotPasswordActivity.this);
            forgetPassword(email);
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please enter valid email id!");
        }
    }

    private void forgetPassword(String email) {
        if(utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.forget(email);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    try {
                        GetResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            int status = loginResponse.getStatus();
                            String message = loginResponse.getMessage();
                            utils.hideDialog();
                            try {
                                if (status == 200) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), message);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                        finish();
                                    }, 2000);

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
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to reset password!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("forgot", "loaded");
    }
}
