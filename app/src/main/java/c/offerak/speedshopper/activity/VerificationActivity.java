package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.utils.Utils;

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

    private Utils utils = new Utils();

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

        Intent intent = getIntent();
        String otp = intent.getStringExtra("OTP");

        if (otp != null && !otp.isEmpty()) {
            edtOne.setText(Character.toString(otp.charAt(0)));
            edtTwo.setText(Character.toString(otp.charAt(1)));
            edtThree.setText(Character.toString(otp.charAt(2)));
            edtFour.setText(Character.toString(otp.charAt(3)));
            edtFive.setText(Character.toString(otp.charAt(4)));
            edtSix.setText(Character.toString(otp.charAt(5)));

            edtOne.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
            edtTwo.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
            edtThree.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
            edtFour.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
            edtFive.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));
            edtSix.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_select_code_bg));

            edtSix.requestFocus();
        }

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

        btnNext.setOnClickListener(v -> {
            startActivity(new Intent(context, HomeScreen.class));
            finish();
        });
    }

    @OnClick(R.id.btnNext)
    public void btnNext() {
        String code = edtOne.getText().toString() + edtTwo.getText().toString() + edtThree.getText().toString() + edtFour.getText().toString();
        utils.showSnackBar(layout, "Your code is " + code);
    }

    @OnClick(R.id.txtResend)
    public void txtResend() {

    }
}
