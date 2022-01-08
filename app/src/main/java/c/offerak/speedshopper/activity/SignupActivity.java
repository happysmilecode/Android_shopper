package c.offerak.speedshopper.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onesignal.OneSignal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.SignupResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = SignupActivity.class.getSimpleName();
    Context context;
    @BindView(R.id.signupRelative)
    LinearLayout linearLayout;
    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;

    ImageView imvEyeMainPwd;
    private Utils utils = new Utils();
    private ApiInterface apiService;

    String name, contact, useremail, token, otp, emailVerify, id, smsCode;

    Dialog dialog;
    private TextView btn_text, btn_email, message;

    MySharedPreference mySharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        mySharedPreference = new MySharedPreference(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        imvEyeMainPwd = findViewById(R.id.imvEyeMainPwd);
        imvEyeMainPwd.setOnClickListener(this);

        OneSignal.addTrigger("signup", "loaded");
    }

    void onEyeMainPwd() {
        if(edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            edtPassword.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imvEyeMainPwd.setImageResource(R.drawable.eye_hide);
        } else {
            edtPassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
            imvEyeMainPwd.setImageResource(R.drawable.eye_view);
        }
        edtPassword.setSelection(edtPassword.getText().length());
    }

    @OnClick(R.id.txtForgotPassword1)
    public void loginScreen() {
//        startActivity(new Intent(context, LoginActivity.class));
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
                utils.hideDialog();
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
                                utils.hideDialog();
                                name = dataBean.getName();
                                contact = dataBean.getContact();
                                useremail = dataBean.getEmail();
                                token = dataBean.getTokenUser();
                                id = dataBean.getId();
                                otp = dataBean.getOtp();
                                smsCode = dataBean.getSmsCode();
                                emailVerify = dataBean.getEmail_verify();
                                mySharedPreference.setTempToken(token);
                                showDialogVerification(responseSign.getMessage());
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

    //----------------------- Show Dialog -------------------------------
    public void showDialogVerification(String msg) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_verify);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        btn_text = dialog.findViewById(R.id.btn_text);
        btn_email = dialog.findViewById(R.id.btn_email);
        message = dialog.findViewById(R.id.msg_detail);
        message.setText(msg);
        btn_text.setOnClickListener(this);
        btn_email.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_email:
                dialog.dismiss();
                utils.showDialog(SignupActivity.this);
                callMessageAPI();
                break;
            case R.id.btn_text:
                dialog.dismiss();
                mobileScreen();
                break;
            case R.id.imvEyeMainPwd:
                onEyeMainPwd();
                break;
        }
    }

    public void callMessageAPI() {
        if(utils.isNetworkConnected(context)) {
            Call<SignupResponse> call = apiService.sendEmail(name, useremail, token);
            call.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {

                    SignupResponse responseEmail = response.body();
                    try {
                        if (responseEmail != null) {

                            if (responseEmail.getStatus() == 200) {
                                utils.hideDialog();

                            } else {

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (responseEmail != null) {
                            SignupResponse.DataBean dataBean = responseEmail.getData();

                            if (responseEmail.getStatus() == 200) {
                                utils.hideDialog();
                                smsCode = dataBean.getSmsCode();
                                sendToVerification(responseEmail.getMessage());
                            } else {
                                utils.hideDialog();
                                utils.showSnackBar(getWindow().getDecorView().getRootView(), responseEmail.getMessage());
                                showDialogVerification(responseEmail.getMessage());
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
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void mobileScreen() {
        startActivity(new Intent(context, MobileActivity.class));
//        finish();
    }

    private void sendToVerification(String msg) {
        utils.hideDialog();
        utils.showSnackBar(linearLayout, msg);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent i = new Intent(SignupActivity.this, VerificationActivity.class);
            i.putExtra(Constants.NAME, name);
            i.putExtra(Constants.EMAIL, useremail);
            i.putExtra(Constants.SMSCODE, smsCode);
            i.putExtra(Constants.MOBILE, "");
            startActivity(i);
            finish();
        }, 3000);
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
