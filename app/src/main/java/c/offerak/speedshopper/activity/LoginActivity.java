package c.offerak.speedshopper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.SplashScreen;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.LoginResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

public class LoginActivity extends AppCompatActivity implements  FacebookCallback<LoginResult>, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String EMAIL = "email";
    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @BindView(R.id.mainConstraint)
    LinearLayout linearLayout;
    @BindView(R.id.edtUserName)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;

    ImageView imvEyeMainPwd;
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

    GoogleApiClient mGoogleApiClient;

    public Boolean isDeepLink = false;
    private UserBean userBean;
    String shareToken;
    String push_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        init();
        getOnesignalInfo();
    }

    public void  getOnesignalInfo()
    {
        OSDeviceState device = OneSignal.getDeviceState();
        assert device != null;
        MySharedPreference.setSharedPreference(context, "ONESIGNAL_ID", device.getUserId());
        push_id = MySharedPreference.getSharedPreferences(context, "ONESIGNAL_ID");
        Log.e("GET_PUSH_ID", push_id);
        OneSignal.addTrigger("login", "loaded");
    }

    public void init() {

        checkLocationPermission();
        createLocationRequest();

        GoogleSignInOptions gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gSignInOptions)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        FacebookSdk.fullyInitialize();
        ButterKnife.bind(this);
        MySharedPreference mySharedPreference = new MySharedPreference(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        userBean = mySharedPreference.getLoginDetails();
        terms_condition = findViewById(R.id.terms_condition);
        privacy_policy = findViewById(R.id.privacy_policy);
        imvEyeMainPwd = findViewById(R.id.imvEyeMainPwd);
        imvEyeMainPwd.setOnClickListener(this);
        terms_condition.setOnClickListener(this);
        privacy_policy.setOnClickListener(this);
        shareToken = MySharedPreference.getSharedPreferences(context, Constants.SHARE_TOKEN);

        LinearLayout googleLoginBtn = findViewById(R.id.btnGmailLogin);
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isNetworkConnected(context)) {
//                    googleSignup();
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(intent, RC_SIGN_IN);
                } else {
                    utils.showSnackBar(linearLayout, "You are not connected to internet!");
                }

            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                isDeepLink = extras.getBoolean(Constants.SHARE_TOKEN, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e(TAG, "init: "+MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN) );

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
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

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, LoginActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(LoginActivity.this, installationTokenResult -> {
            String newToken = installationTokenResult.getToken();
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

                startActivity(new Intent(context, TermsConditionActivity.class));
                break;

            case R.id.privacy_policy:
                startActivity(new Intent(context, PrivacyPolicyActivity.class));
                break;
            case R.id.imvEyeMainPwd:
                onEyeMainPwd();
                break;
        }
    }

    public void onEyeMainPwd() {
        if(edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            edtPassword.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imvEyeMainPwd.setImageResource(R.drawable.eye_hide);
        } else {
            edtPassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
            imvEyeMainPwd.setImageResource(R.drawable.eye_view);
        }
        edtPassword.setSelection(edtPassword.getText().length());
    }

    @OnClick(R.id.txtForgotPassword)
    public void forgotPass() {
        startActivity(new Intent(context, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.txtNewUser1)
    public void signup() {
        startActivity(new Intent(this, SignupActivity.class));
//        finish();
    }

    @OnClick(R.id.guest)
    public void guest() {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android","Android ID : "+androidId);
        login(androidId, "", "guest");
    }

    @OnClick(R.id.btnLogin)
    public void btnLogin() {

        String userName = edtUsername.getText().toString();
        String userPass = edtPassword.getText().toString();

        if (userName.equals("") || userName.isEmpty()) {
            utils.showSnackBar(linearLayout, "Please enter email id!");
        } else if (!isValidEmail(userName)) {
            utils.showSnackBar(linearLayout, "Please enter valid email id!");
        } else if (userPass.equals("") || userPass.isEmpty()) {
            utils.showSnackBar(linearLayout, "Please enter password!");
        } else {
            if (utils.isNetworkConnected(this)) {
                utils.showDialog(this);
                login(userName, userPass, "loggedin");
            } else {
                utils.showSnackBar(linearLayout, "You are not connected to internet!");
            }
        }
    }

    @OnClick(R.id.btnFbLogin)
    public void fbLogin() {
        if (utils.isNetworkConnected(this)) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        } else {
            utils.showSnackBar(linearLayout, "You are not connected to internet!");
        }
    }

//    private void googleSignup() {
//        if (googleApiClient != null && googleApiClient.isConnected()) {
//            googleApiClient.stopAutoManage((FragmentActivity) context);
//            googleApiClient.disconnect();
//        }
//        GoogleSignInOptions gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        googleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gSignInOptions)
//                .build();
//    }

    public void shareTokenUpdate(String type)
    {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(context);
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
                        sendToHome(type);

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

    private void login(String userName, String password, String flow) {

        if (utils.isNetworkConnected(context)) {
            Log.e(TAG, "login: " + MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN));

            Call<LoginResponse> call = null;
            if(flow.equals("guest")){
                call = apiService.loginGuest(userName,
                        "android", push_id);
            }else{
                call = apiService.login(userName,
                        password,
                        MySharedPreference.getSharedPreferences(context, Constants.FIREBASE_TOKEN),
                        "android", push_id);
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
                                            checkShare("guest");
                                        }else{
                                            checkShare("email");
                                        }
                                    } else {
                                        utils.showSnackBar(linearLayout, "Please verify your email first!");
                                    }
                                } else {
                                    utils.showSnackBar(linearLayout, loginResponse.getMessage());
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
                    utils.showSnackBar(linearLayout, "Failed to perform login!");
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
                    "android", push_id);
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
                                        checkShare("facebook");
                                    }
                                } else {
                                    utils.showSnackBar(linearLayout, message);
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
                    utils.showSnackBar(linearLayout, "Failed to perform login!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void checkShare(String type) {
        if (isDeepLink) {
            shareTokenUpdate(type);
        } else {
            sendToHome(type);
        }

    }

    private void sendToHome(String type) {
        if (type.equals("guest")) {
            Intent intent = new Intent(context, MenuActivity.class);
            Bundle b = new Bundle();
            b.putString("from", "guest"); //Your id
            intent.putExtras(b);
            startActivity(intent);
            finish();
        } else {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }

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
            utils.showSnackBar(getWindow().getDecorView().getRootView(), "Google Signin failed due to some reason. Please try again later");
        }
    }

    @Override
    public void onCancel() {
//        Utility.ShowToastMessage(context, );
        utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.facebook_login_cancel));
    }

    @Override
    public void onError(FacebookException error) {
//        Utility.ShowToastMessage(context, getString(R.string.error_in_facebook));
        utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.error_in_facebook));
        Log.e(TAG, "onError: " + error.getMessage());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("login", "loaded");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status1 = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            //GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        startLocationUpdates();
                    }

                } else {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "You denied the permission!");
                }
            }

        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
