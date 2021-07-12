package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import com.onesignal.OneSignal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.SignupResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = SignupActivity.class.getSimpleName();
    Context context;
    @BindView(R.id.signupRelative)
    ConstraintLayout constraintLayout;
    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;

    @BindView(R.id.edtPassword)
    EditText edtPassword;
    private Utils utils = new Utils();
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        OneSignal.addTrigger("signup", "loaded");
    }

    @OnClick(R.id.txtForgotPassword1)
    public void loginScreen() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.btnSignup)
    public void btnSignup() {

        String userName = edtName.getText().toString();
        String userPass = edtPassword.getText().toString();
        String userEmail = edtEmail.getText().toString();

        if (userName.equals("") || userName.isEmpty()) {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please enter name!");
        } else if (userEmail.equals("") || userEmail.isEmpty()) {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please enter email id!");
        } else if (userPass.equals("") || userPass.isEmpty()) {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please enter password!");
        } else if (!isValidEmail(userEmail)) {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please enter valid email id!");
        } else {
            if (utils.isNetworkConnected(context)) {
                utils.showDialog(context);
                signup(userName, userPass, userEmail);
            } else {
                utils.showSnackBar(getWindow().getDecorView().getRootView(), "You are not connected to internet!");
            }
        }
    }

    private void signup(String userName, String password, String email) {

        if(utils.isNetworkConnected(context)) {
            Call<SignupResponse> call = apiService.register(userName, email, "", password);
            call.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {

                    SignupResponse responseSign = response.body();
                    try {
                        if (responseSign != null) {
                            SignupResponse.DataBean dataBean = responseSign.getData();

                            if (responseSign.getStatus() == 200) {
                                String name = dataBean.getName();
                                String contact = dataBean.getContact();
                                String email = dataBean.getEmail();
                                String token = dataBean.getToken();
                                String id = dataBean.getId();
                                String otp = dataBean.getOtp();
                                String emailVerify = dataBean.getEmail_verify();
                                sendToVerification(responseSign.getMessage());
                            } else {
                                utils.hideDialog();
                                utils.showSnackBar(getWindow().getDecorView().getRootView(), responseSign.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    utils.hideDialog();
                    Log.d("", "onResponse: ");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void sendToVerification(String otp) {
        utils.hideDialog();
        utils.showSnackBar(constraintLayout, otp + "");
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }, 2000);
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

        OneSignal.addTrigger("signup", "loaded");

    }
}
