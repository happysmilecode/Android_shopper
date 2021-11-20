package c.offerak.speedshopper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.fragment.ContactUsFragment;
import c.offerak.speedshopper.fragment.CouponFragment;
import c.offerak.speedshopper.fragment.FrequentlyAskedQuestionsFragment;
import c.offerak.speedshopper.fragment.NotificationFragment;
import c.offerak.speedshopper.fragment.PremadeFragment;

import c.offerak.speedshopper.fragment.ProfileFragment;
import c.offerak.speedshopper.fragment.ShoppingListsFragment;
import c.offerak.speedshopper.fragment.SpeedShopperMarketListFragment;
import c.offerak.speedshopper.fragment.WalletFragment;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class LandingScreen extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    MySharedPreference mySharedPreference;
    UserBean bean;
    public static String lat;
    public static String lng;
    public static ImageView txtSync;
    public static TextView txtTitle, history;
    public RelativeLayout headerBar;
    @BindView(R.id.backButton)
    ImageView backButton;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.homeFrame)
    FrameLayout frameLayout;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private Utils utils = new Utils();
    private boolean first = true;
    private boolean doubleBackToExitPressedOnce;
    private int REQUEST_CHECK_SETTINGS = 1000;
    Context context;
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);
        context = this;
        init();
        initializeFragment();
    }

    public void init() {
        ButterKnife.bind(this);
        Log.e(TAG, "FireBase Token: " + MySharedPreference.getSharedPreferences(context, Constants.DEVICE_ID));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        txtTitle = findViewById(R.id.txtTitle);
        txtSync = findViewById(R.id.txtSync);
        history = findViewById(R.id.history);

        history.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildLocationAccess();
        }

        checkLocationPermission();
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        backButton.setOnClickListener(view -> finish());

        mySharedPreference = new MySharedPreference(context);
        bean = mySharedPreference.getLoginDetails();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    private void initializeFragment() {

        String menu_name = getIntent().getStringExtra("MENU_NAME");
        if (menu_name == null) {
            menu_name = "my_list";
        }
        FragmentTransaction transaction;
        FragmentManager fragmentManager;
        fragmentManager = getFragmentManager();
        switch (menu_name) {
            case "my_list":
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new ShoppingListsFragment());
                transaction.commit();
                break;
            case "my_profile":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new ProfileFragment(), "ProfileFragment");

                transaction.commit();
                break;
            case "my_wallet":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new WalletFragment());
                transaction.commit();
                break;
            case "sstx_market":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new SpeedShopperMarketListFragment());
                transaction.commit();
                break;
            case "notifications":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new NotificationFragment());
                transaction.commit();
                break;
            case "pre_made":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new PremadeFragment());
                transaction.commit();
                break;
            case "help":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new FrequentlyAskedQuestionsFragment());
                transaction.commit();
                break;
            case "contact_us":
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFrame, new ContactUsFragment());
                transaction.commit();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + menu_name);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Fragment fragment = getFragmentManager().findFragmentByTag("ProfileFragment");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (NullPointerException e) {
            Log.d("", ":");
        }

        if (requestCode == 200) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                utils.showSnackBar(getWindow().getDecorView().getRootView(), "Location required, Application may miss behave!");
            }
        }
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
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        Log.e(TAG, "mCurrentLocation: " + mCurrentLocation);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        Log.e(TAG, "mCurrentLocation: " + mCurrentLocation);
        if (null != mCurrentLocation) {
            lat = String.valueOf(mCurrentLocation.getLatitude());
            lng = String.valueOf(mCurrentLocation.getLongitude());
            Log.e(TAG, "lat: " + lat + " lng: " + lng);
            Log.d(TAG, "location is not null ...............");
        } else {
            Log.d(TAG, "location is null ...............");
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
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildLocationAccess();

        }

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(LandingScreen.this,
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

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        startLocationUpdates();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    utils.showSnackBar(drawerLayout, "You denied the permission!");
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please click BACK again to exit...");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationAccess() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
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
                        resolvable.startResolutionForResult(LandingScreen.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
}
