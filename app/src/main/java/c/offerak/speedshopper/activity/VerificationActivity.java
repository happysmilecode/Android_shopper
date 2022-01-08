package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onesignal.OneSignal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.LoginResponse;
import c.offerak.speedshopper.response.SignupResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    public static final String TAG = VerificationActivity.class.getSimpleName();
    Context context;
    @BindView(R.id.edtCodeOne)
    EditText edtOne;

    @BindView(R.id.edtCodeTwo)
    EditText edtTwo;

    @BindView(R.id.edtCodeThree)
    EditText edtThree;

    @BindView(R.id.edtCodeFour)
    EditText edtFour;

    @BindView(R.id.edtCodeFive)
    EditText edtFive;

    @BindView(R.id.edtCodeSix)
    EditText edtSix;

    @BindView(R.id.constraintLayout)
    ConstraintLayout layout;

    @BindView(R.id.btnNext)
    ImageView btnNext;

    @BindView(R.id.msgTextView)
    TextView msgTextView;

    private Utils utils = new Utils();
    private ApiInterface apiService;
    String name, email, phone, smsCode;
    MySharedPreference mySharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        context=this;
        init();
    }

    public void init(){
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        mySharedPreference = new MySharedPreference(this);

        Intent intent = getIntent();
        name = intent.getStringExtra(Constants.NAME);
        email = intent.getStringExtra(Constants.EMAIL);
        smsCode = intent.getStringExtra(Constants.SMSCODE);
        phone   = intent.getStringExtra(Constants.MOBILE);

        if (phone.equals("")) {
            msgTextView.setText(context.getString(R.string.we_have_sent_you_an_access_code_via_email));
        }

//        if (otp != null && !otp.isEmpty()) {
//            edtOne.setText(Character.toString(otp.charAt(0)));
//            edtTwo.setText(Character.toString(otp.charAt(1)));
//            edtThree.setText(Character.toString(otp.charAt(2)));
//            edtFour.setText(Character.toString(otp.charAt(3)));
//            edtFive.setText(Character.toString(otp.charAt(4)));
//            edtSix.setText(Character.toString(otp.charAt(5)));
//
//            edtOne.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
//            edtTwo.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
//            edtThree.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
//            edtFour.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
//            edtFive.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
//            edtSix.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
//
//            edtSix.requestFocus();
//        }

        edtOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    edtOne.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
                    if (start != 0 || before != 1) {
                        edtTwo.requestFocus();
                    }
                }

                if (edtOne.getText().toString().length() == 0) {
                    edtOne.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_code_bg));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    edtTwo.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
                    if (start == 0 && before == 1) {
                        edtOne.requestFocus();
                    } else {
                        edtThree.requestFocus();
                    }
                }

                if (edtTwo.getText().toString().length() == 0) {
                    edtOne.requestFocus();
                    edtTwo.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_code_bg));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    edtThree.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
                    if (start == 0 && before == 1) {
                        edtTwo.requestFocus();
                    } else {
                        edtFour.requestFocus();
                    }

                    if (edtThree.getText().toString().length() == 0) {
                        edtTwo.requestFocus();
                        edtThree.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_code_bg));
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.equals("")) {
                    edtFour.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
                    if (start == 0 && before == 1) {
                        edtThree.requestFocus();
                    } else {
                        edtFive.requestFocus();
                    }

                    if (edtFour.getText().toString().length() == 0) {
                        edtThree.requestFocus();
                        edtFour.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_code_bg));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.equals("")) {
                    edtFive.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
                    if (start == 0 && before == 1) {
                        edtFour.requestFocus();
                    } else {
                        edtSix.requestFocus();
                    }

                    if (edtFive.getText().toString().length() == 0) {
                        edtFour.requestFocus();
                        edtFive.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_code_bg));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    edtSix.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
                }

                if (edtSix.getText().toString().length() == 0) {
                    edtFive.requestFocus();
                    edtSix.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_code_bg));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        btnNext.setOnClickListener(v -> {
//            startActivity(new Intent(context, HomeScreen.class));
//            finish();
//        });
        OneSignal.addTrigger("verification", "loaded");
    }

    @OnClick(R.id.btnNext)
    public void btnNext() {

        String code = edtOne.getText().toString() + edtTwo.getText().toString() + edtThree.getText().toString() + edtFour.getText().toString() + edtFive.getText().toString() + edtSix.getText().toString();
//        utils.showSnackBar(layout, "Your code is " + code);
        if (smsCode.equals(code)) {
            utils.showDialog(context);
            callUserUdateAPI();
        } else {
            utils.showSnackBar(layout, "Invalid Code.  Please enter correct code");
            clearText();
        }
    }

    public void clearText() {
        edtOne.setText("");
        edtTwo.setText("");
        edtThree.setText("");
        edtFour.setText("");
        edtFive.setText("");
        edtSix.setText("");
    }

    public void callUserUdateAPI() {
        String token = mySharedPreference.getTempToken();
        if(utils.isNetworkConnected(context)) {
            Call<LoginResponse> call = apiService.verify_mobile(
                    token, MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN),
                    "android", MySharedPreference.getSharedPreferences(context, "ONESIGNAL_ID"));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    LoginResponse resp = response.body();
                    utils.hideDialog();
                    try {
                        if (resp != null) {
                            int status = resp.getStatus();
                            String message = resp.getMessage();
                            LoginResponse.DataBean dataBean = resp.getData();

                            if (status == 200) {
                                String login_num = dataBean.getLogin_num();
                                String balance = dataBean.getBalance();
                                String name = dataBean.getName();
                                String email = dataBean.getEmail();
                                String token = dataBean.getToken();
                                String id = dataBean.getId();
                                String emailVerify = dataBean.getEmail_verify();
                                String proPic = dataBean.getProfile_pic();
                                String picPath = dataBean.getPath();
                                String device_Id = dataBean.getDevice_id();
                                MySharedPreference.setSharedPreference(context, Constants.DEVICE_ID, device_Id);
                                MySharedPreference.setPurchased(context, "sstx_earned", true);
                                mySharedPreference.setSharedPreference(context, Constants.EVENT_CHECK, "1");
                                mySharedPreference.setLoginDetails(login_num, balance, email, name, picPath + "" + proPic, token, id, "loggedin");
                                startActivity(new Intent(VerificationActivity.this, MenuActivity.class));
                                finish();

                            } else {
                                utils.showSnackBar(getWindow().getDecorView().getRootView(), message);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to send Verification code!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
    @OnClick(R.id.txtResend)
    public void txtResend() {
        clearText();
        utils.showDialog(context);
        String token = mySharedPreference.getTempToken();
        if(utils.isNetworkConnected(context)) {
            if (phone.equals("")) {
                emailResend(token);
            } else {
                smsResend(token);
            }
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void smsResend(String token) {
        Call<SignupResponse> call = apiService.sendSMS(phone, token);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {
                SignupResponse resp = response.body();
                try {
                    if (resp != null) {
                        int status = resp.getStatus();
                        String message = resp.getMessage();
                        SignupResponse.DataBean dataBean = resp.getData();
                        utils.hideDialog();

                        if (status == 200) {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), message);
                            smsCode = dataBean.getSmsCode();

                        } else {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), message);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                utils.hideDialog();
                utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to send Verification code!");
            }
        });
    }

    private void emailResend(String token) {
        Call<SignupResponse> call = apiService.sendEmail(name, email, token);
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
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), responseEmail.getMessage());
                            smsCode = dataBean.getSmsCode();
                        } else {
                            utils.hideDialog();
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), responseEmail.getMessage());
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("verification", "loaded");
    }
}
