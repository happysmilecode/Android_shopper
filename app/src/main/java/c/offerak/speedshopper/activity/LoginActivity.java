package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.response.LoginResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utility;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult>, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String EMAIL = "email";
    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.mainConstraint)
    ConstraintLayout constraintLayout;
    @BindView(R.id.edtUserName)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    CallbackManager callbackManager;
    LocationRequest mLocationRequest;
    private Intent intent;
    private Utils utils = new Utils();
    private ApiInterface apiService;
    private Boolean isEmailVerify = false;
    private String APP_ID = "174788573240747";
    private String strfname, strflastname, strfemail, strauthid, strprofileimg;
    TextView terms_condition, privacy_policy;
    private int REQUEST_CHECK_SETTINGS = 1000;
    private static final int RC_SIGN_IN = 007;
    Context context;
    public static String dirpicture = Environment.getExternalStorageDirectory() + "/Bulenttes/";
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    final int PERMISSION_REQUEST_CODE = 101;
    GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        init();
    }

    public void init() {
        FacebookSdk.fullyInitialize();
        ButterKnife.bind(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        terms_condition = findViewById(R.id.terms_condition);
        privacy_policy = findViewById(R.id.privacy_policy);
        terms_condition.setOnClickListener(this);
        privacy_policy.setOnClickListener(this);

        LinearLayout googleLoginBtn = findViewById(R.id.btnGmailLogin);
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isNetworkConnected(context)) {
                    googleSignup();
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(intent, RC_SIGN_IN);
                } else {
                    utils.showSnackBar(constraintLayout, "You are not connected to internet!");
                }

            }
        });

        Log.e(TAG, "init: "+MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN) );
       /* final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        createLocationRequest();

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildLocationAccess();
        }*/

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "c.offerak.speedshopper",                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "NameNotFoundException Error: "+e.toString() );
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException Error: "+e.toString() );
        }
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "init: "+e.toString() );
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "init: "+e.toString() );
        }*/

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, LoginActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            MySharedPreference.setSharedPreference(context, Constants.FIREBASE_TOKEN, newToken);

        });

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, CAMERA) | ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Permission Needed");
            builder.setPositiveButton("grant", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(LoginActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.terms_condition:
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        termsCondition();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    termsCondition();
                }*/
                startActivity(new Intent(context, TermsConditionActivity.class));
                break;

            case R.id.privacy_policy:
                startActivity(new Intent(context, PrivacyPolicyActivity.class));
                break;
        }
    }

    @OnClick(R.id.txtForgotPassword)
    public void forgotPass() {
        startActivity(new Intent(context, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.txtNewUser1)
    public void signup() {
        startActivity(new Intent(context, SignupActivity.class));
    }

    @OnClick(R.id.guest)
    public void guest() {
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android","Android ID : "+androidId);
        login(androidId, "", "guest");
        //        Intent intent = new Intent(context, MenuActivity.class);
//        Bundle b = new Bundle();
//        b.putString("from", "guest"); //Your id
//        intent.putExtras(b);
//        startActivity(intent);
    }

    @OnClick(R.id.btnLogin)
    public void btnLogin() {

        String userName = edtUsername.getText().toString();
        String userPass = edtPassword.getText().toString();

        if (userName.equals("") || userName.isEmpty()) {
            utils.showSnackBar(constraintLayout, "Please enter email id!");
        } else if (!isValidEmail(userName)) {
            utils.showSnackBar(constraintLayout, "Please enter valid email id!");
        } else if (userPass.equals("") || userPass.isEmpty()) {
            utils.showSnackBar(constraintLayout, "Please enter password!");
        } else {
            if (utils.isNetworkConnected(this)) {
                utils.showDialog(this);
                login(userName, userPass, "loggedin");
            } else {
                utils.showSnackBar(constraintLayout, "You are not connected to internet!");
            }
        }
    }

    @OnClick(R.id.btnFbLogin)
    public void fbLogin() {
        if (utils.isNetworkConnected(this)) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        } else {
            utils.showSnackBar(constraintLayout, "You are not connected to internet!");
        }
    }

    private void googleSignup() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage((FragmentActivity) context);
            googleApiClient.disconnect();
        }
        GoogleSignInOptions gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gSignInOptions)
                .build();
    }

    private void login(String userName, String password, String flow) {

        if (utils.isNetworkConnected(context)) {
            Log.e(TAG, "login: " + MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN));

            Call<LoginResponse> call = null;
            if(flow.equals("guest")){
                call = apiService.loginGuest(userName,
                        "android");
            }else{
                call = apiService.login(userName,
                        password,
                        MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN),
                        "android");
            }

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {

                    Log.e(TAG, "onResponse: " + response);
                    try {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            LoginResponse.DataBean dataBean = loginResponse.getData();
                            utils.hideDialog();
                            try {
                                if (loginResponse.getStatus() == 200) {

                                    String name = dataBean.getName();
                                    String email = dataBean.getEmail();
                                    String token = dataBean.getToken();
                                    String id = dataBean.getId();
                                    String emailVerify = dataBean.getEmail_verify();
                                    String proPic = dataBean.getProfile_pic();
                                    String picPath = dataBean.getPath();
                                    String device_Id = dataBean.getDevice_id();
                                    MySharedPreference.setSharedPreference(context, Constants.DEVICE_ID, device_Id);

                                    if (!emailVerify.isEmpty() && emailVerify.equals("1")) {
                                        isEmailVerify = true;
                                    }

                                    MySharedPreference mySharedPreference = new MySharedPreference(context);
                                    mySharedPreference.setSharedPreference(context, Constants.EVENT_CHECK, "1");
                                    mySharedPreference.setLoginDetails(email, name, picPath + "" + proPic, token, id, flow);

                                    if (isEmailVerify) {
                                        if(flow.equals("guest")){
                                            Intent intent = new Intent(context, MenuActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("from", "guest"); //Your id
                                            intent.putExtras(b);
                                            startActivity(intent);
                                        }else{
                                            sendToHome();
                                        }
                                    } else {
                                        utils.showSnackBar(constraintLayout, "Please verify your email first!");
                                    }
                                } else {
                                    utils.showSnackBar(constraintLayout, loginResponse.getMessage());
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
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(constraintLayout, "Failed to perform login!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            public String strfgender;

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    LoginManager.getInstance().logOut();
                    strfname = object.getString("first_name");
                    strflastname = object.getString("last_name");
                    if (object.has("email")) {
                        strfemail = object.getString("email");
                    } else {
                        strfemail = "";
                    }
                    strauthid = object.getString("id");

                    //strprofileimg = "https://graph.facebook.com/" + strauthid + "/picture?type=large";
                    // strprofileimg=isEmpty(strprofileimg);
                    strprofileimg = "";

                    if (strfemail.equals("")) {
                        utils.showSnackBar(getWindow().getDecorView().getRootView(), "This facebook account does not give email address which is mandatory for registration.");
                    } else {
                        fun_social_signin(strfemail, strfname, strflastname, strfgender, strauthid, "1", "facebook", strprofileimg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void fun_social_signin(String strfemail, String strfname, String
            strflastname, String strfgender, String strauthid, String login_type, String facebook, String strprofileimg) {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(this);
            Call<LoginResponse> call = apiService.facebookLogin(strfname + strflastname,
                    strfemail,
                    strauthid,
                    login_type,
                    MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN),
                    "android");
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                    utils.hideDialog();

                    try {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            int status = loginResponse.getStatus();
                            String message = loginResponse.getMessage();
                            LoginResponse.DataBean dataBean = loginResponse.getData();

                            utils.hideDialog();
                            try {
                                if (status == 200) {
                                    String name = dataBean.getName();
                                    String email = dataBean.getEmail();
                                    String token = dataBean.getToken();
                                    String id = dataBean.getId();
                                    String emailVerify = dataBean.getEmail_verify();
                                    String proPic = dataBean.getProfile_pic();
                                    String picPath = dataBean.getPath();
                                    String device_ID = dataBean.getDevice_id();
                                    MySharedPreference.setSharedPreference(context, Constants.DEVICE_ID, device_ID);

                                    MySharedPreference mySharedPreference = new MySharedPreference(LoginActivity.this);
                                    mySharedPreference.setSharedPreference(context, Constants.EVENT_CHECK, "1");
                                    mySharedPreference.setLoginDetails(email, name, picPath + "" + proPic, token, id, "loggedin");
                                    if (message.equals("Session expired")) {
                                        utils.showSnackBar(getWindow().getDecorView().getRootView(), "Your account has been deactivated by the admin!");
                                    } else {
                                        sendToHome();
                                    }
                                } else {
                                    utils.showSnackBar(constraintLayout, message);
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
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(constraintLayout, "Failed to perform login!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void sendToHome() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " + data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

       /* if (requestCode == 200) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                utils.showSnackBar(getWindow().getDecorView().getRootView(), "Location required, Application may miss behave!");
            }
        }*/
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            Log.e(TAG, "handleSignInResult: " + acct.getDisplayName() + "," + acct.getGivenName());
            if (acct != null) {
                strfname = acct.getGivenName();
                strflastname = acct.getFamilyName();
                strauthid = acct.getId();
                strfemail = acct.getEmail();
                if (strfemail.isEmpty())
                    strfemail = "";
                strprofileimg = "";
                mGoogleSignInClient.signOut();
                if (strfemail.equals("")) {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "This gmail account does not give email address which is mandatory for registration.");
                } else {
                    fun_social_signin(strfemail, strfname, strflastname, "", strauthid, "2", "gmail", strprofileimg);
                }
            }
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode() + e.getMessage() + "," + e.toString());
        }
    }

    @Override
    public void onCancel() {
        Utility.ShowToastMessage(context, getString(R.string.facebook_login_cancel));
    }

    @Override
    public void onError(FacebookException error) {
        Utility.ShowToastMessage(context, getString(R.string.error_in_facebook));
        Log.e(TAG, "onError: " + error.getMessage());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationAccess() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LoginActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
}
