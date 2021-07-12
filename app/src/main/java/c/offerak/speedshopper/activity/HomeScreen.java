package c.offerak.speedshopper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.onesignal.OneSignal;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.fragment.StoreListFragment;
import c.offerak.speedshopper.fragment.StoreMapFragment;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.reside.ResideMenu;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static c.offerak.speedshopper.R.color.colorAppBlue;
import static c.offerak.speedshopper.R.color.white;

public class HomeScreen extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context context;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int LOCATION_REQUEST_CODE = 9001;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    @SuppressLint("StaticFieldLeak")
    public static LinearLayout footer;
    @SuppressLint("StaticFieldLeak")
    public static TextView screenTitle;
    public static String lat, lng;
    @BindView(R.id.listLinear)
    public LinearLayout btnList;
    @BindView(R.id.mapLinear)
    public LinearLayout btnMap;
    @BindView(R.id.toggleButton)
    ImageView toggleButton;
    @BindView(R.id.backButton)
    ImageView backButton;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private ResideMenu resideMenu;
    private Utils utils = new Utils();
    private boolean first = true;
    private ImageView icList, icMap;
    private TextView txtList, txtMap;
    public static AppCompatSpinner spinner;
    public static String distance = "";
    UserBean userBean;
    private ApiInterface apiService;
    StoreListFragment storeListFragment;
    StoreMapFragment storeMapFragment;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screeen);
        context = this;
        init();
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_REQUEST_CODE);
    }
    protected void requestPermission(String permissionType, int
            requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }


    public void init() {
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        footer = findViewById(R.id.footer);
        screenTitle = findViewById(R.id.txtTitle);
        spinner = findViewById(R.id.spinner);
        txtList = findViewById(R.id.txtList);
        txtMap = findViewById(R.id.txtMap);
        icList = findViewById(R.id.icList);
        icMap = findViewById(R.id.icMap);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        userBean = mySharedPreference.getLoginDetails();

        spinner.setSelection(0);
        distance = "10";

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); /* if you want your item to be white */
                ((TextView) parent.getChildAt(0)).setTextSize(12);

                String[] str = String.valueOf(((TextView) parent.getChildAt(0)).getText()).split(" ");

                distance = str[0];

                if (storeListFragment != null) {
                    StoreListFragment.searchEdit.setText("");
                    storeListFragment.listSetup();
                }
                if (storeMapFragment != null) {
                    storeMapFragment.onResume();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkLocationPermission();
        if (!

                isGooglePlayServicesAvailable())

        {
            finish();
        }

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                        addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this).
                        build();

        toggleButton.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v ->

                finish());

        btnList.setOnClickListener((
                View v) ->

        {
            initializeFragment(1);
            btnList.setBackgroundColor(getResources().getColor(colorAppBlue));
            btnMap.setBackgroundColor(getResources().getColor(white));
            txtList.setTextColor(getResources().getColor(white));
            txtMap.setTextColor(getResources().getColor(colorAppBlue));
            icList.setImageResource(R.mipmap.ic_list);
            icMap.setImageResource(R.drawable.ic_map);
        });

        btnMap.setOnClickListener(v ->

        {
            initializeFragment(2);
            btnMap.setBackgroundColor(getResources().getColor(colorAppBlue));
            btnList.setBackgroundColor(getResources().getColor(white));
            txtMap.setTextColor(getResources().getColor(white));
            txtList.setTextColor(getResources().getColor(colorAppBlue));
            icList.setImageResource(R.mipmap.ic_list_dark);
            icMap.setImageResource(R.mipmap.ic_map_dark);
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        resideMenu = new

                ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_bg);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.attachToActivity(this);

        OneSignal.addTrigger("home", "loaded");

    }

    @SuppressLint("SetTextI18n")
    private void initializeFragment(int frag) {

        if (frag == 0) {
            storeListFragment = new StoreListFragment();
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frameLayout, storeListFragment);
            transaction.commit();

        } else if (frag == 1) {
            storeListFragment = new StoreListFragment();
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frameLayout, storeListFragment);
            transaction.commit();

        } else if (frag == 2) {
            storeMapFragment = new StoreMapFragment();
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frameLayout, storeMapFragment);
            transaction.commit();
        }
    }

    public void addListToServer(String listName, String storeId, String imgName) {
        utils.showDialog(context);
        if (utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.addShoppingList(userBean.getUserToken(), storeId, listName, imgName);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    try {
                        GetResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            utils.hideDialog();
                            if (storeListFragment != null) {
                                storeListFragment.listSetup();
                            }
                            finish();
                        }
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Fragment fragment = getFragmentManager().findFragmentByTag("ProfileFragment");
            fragment.onActivityResult(requestCode, resultCode, data);
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
        //GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
        return ConnectionResult.SUCCESS == status;
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
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
//        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            lat = String.valueOf(mCurrentLocation.getLatitude());
            lng = String.valueOf(mCurrentLocation.getLongitude());

            if (first) {
                initializeFragment(0);
                first = false;
            }
            Log.e(TAG, "location is not null ...............lat " + lat + "lng: " + lng);
        } else {
            Log.e(TAG, "location is null ...............");
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
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        OneSignal.addTrigger("home", "loaded");
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(HomeScreen.this,
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Unable to show location permission required", Toast.LENGTH_LONG).show();
                } return;
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                200);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}

