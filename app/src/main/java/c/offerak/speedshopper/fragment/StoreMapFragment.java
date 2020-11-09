package c.offerak.speedshopper.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.HomeScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.modal.HomeListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.StoreListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utility;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static c.offerak.speedshopper.activity.HomeScreen.distance;
import static c.offerak.speedshopper.activity.HomeScreen.footer;
import static c.offerak.speedshopper.activity.HomeScreen.lat;
import static c.offerak.speedshopper.activity.HomeScreen.lng;
import static c.offerak.speedshopper.activity.HomeScreen.screenTitle;
import static c.offerak.speedshopper.activity.HomeScreen.spinner;

public class StoreMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = StoreMapFragment.class.getSimpleName();
    Context context;
    MapView mMapView;
    private GoogleMap googleMap;
    View rootView;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    public List<HomeListBean> listBeans = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.map_view_layout, container, false);
        context = getActivity();
        footer.setVisibility(View.VISIBLE);
        screenTitle.setText(R.string.choose_from_map);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

//        mMapView.onResume();
        init();
        return rootView;
    }

    public void init() {

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            googleMap.setMyLocationEnabled(true);

            googleMap.setOnInfoWindowClickListener(marker -> {

                int listId = -1;

                for (int i = 0; i < listBeans.size(); i++) {
//                    if (listBeans.get(i).getName().equals(marker.getTitle())) {
//                        listId = i;
//                    }
                    if (Double.parseDouble(listBeans.get(i).getLatitude()) == marker.getPosition().latitude
                            && Double.parseDouble(listBeans.get(i).getLongitude()) == marker.getPosition().longitude) {
                        listId = i;
                    }
                }

                String storeId = listBeans.get(listId).getId();
                String storeName = listBeans.get(listId).getName();
                String storeLogo = listBeans.get(listId).getStoreImage();
                String storeAddress = listBeans.get(listId).getAddress();

                getActivity().setResult(RESULT_OK, new Intent()
                        .putExtra("storeId", storeId)
                        .putExtra("storeLogo", storeLogo)
                        .putExtra("storeAddress", storeAddress)
                        .putExtra("storeName", storeName));

                showDialogStoreList(storeName, storeId);
//                getActivity().finish();
            });
        });

        utils.showDialog(context);
        listSetup();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            mMapView.onResume();
        } catch (Exception e) {
            Log.e("Crash", "Deny access fine location");
        }
        if (googleMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            googleMap.clear();
            listSetup();// add markers from database to the map
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    //----------------------- Store Dialog List -----------------------
    public void showDialogStoreList(String storeName, String storeId) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.list_add_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        EditText inputE = dialog.findViewById(R.id.edtUserName);
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView ctv_title = dialog.findViewById(R.id.ctv_title);
        Button btnDont = dialog.findViewById(R.id.btnDone);
        ImageView imgCross = dialog.findViewById(R.id.iconCross);

        ctv_title.setText(storeName);
        inputE.setHint("e.g. Grocery Store Near Home");

        btnDont.setOnClickListener(v1 -> {
            String listName = inputE.getText().toString();
            if (!listName.equals("")) {
                dialog.dismiss();
                ((HomeScreen) getActivity()).addListToServer(listName, storeId);
            } else {
                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please enter the list name!");
            }
        });

        imgCross.setOnClickListener(v12 -> dialog.dismiss());
        dialog.show();
    }

    public void listSetup() {
//        utils.showDialog(context);
        if (utils.isNetworkConnected(context)) {
            Call<StoreListResponse> call = apiService.getStores(userBean.getUserToken(), lat, lng, HomeScreen.distance);
            call.enqueue(new Callback<StoreListResponse>() {
                @Override
                public void onResponse(Call<StoreListResponse> call, retrofit2.Response<StoreListResponse> response) {
                    try {
                        StoreListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            List<StoreListResponse.DataBean> list = tokenResponse.getData();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    listBeans.clear();
                                    for (int i = 0; i < list.size(); i++) {
                                        HomeListBean homeListBean = new HomeListBean();
                                        homeListBean.setStoreImage(list.get(i).getPath() + "" + list.get(i).getLogo());
                                        homeListBean.setAddress(list.get(i).getAddress());
                                        homeListBean.setId(list.get(i).getId());
                                        homeListBean.setName(list.get(i).getName());
                                        homeListBean.setLatitude(list.get(i).getLatitude());
                                        homeListBean.setLongitude(list.get(i).getLongitude());

                                        listBeans.add(homeListBean);
//                                        for (int j = 0; j < listBeans.size(); j++) {

                                        Double lat = Double.parseDouble(homeListBean.getLatitude());
                                        Double lng = Double.parseDouble(homeListBean.getLongitude());

                                        LatLng markLoc = new LatLng(lat, lng);
                                        googleMap.addMarker(new MarkerOptions().position(markLoc).title(homeListBean.getName()).snippet(homeListBean.getAddress()));

                                            /*if (j == listBeans.size() - 1) {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(markLoc).zoom(8).build();
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }*/
//                                        }
                                    }

                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(-34, 151)).zoom(10).build()));
                                    if (googleMap != null) {
                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        utils.hideDialog();
                                        googleMap.setMyLocationEnabled(true);
                                        if (lat != null)
                                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(Double.valueOf(lat), Double.valueOf(lng))).zoom(10).build()));
                                        else
                                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(-34, 151)).zoom(10).build()));
                                    }
                                } else if (tokenResponse.getStatus() == 400) {
                                    utils.hideDialog();
                                    Log.e(TAG, "onResponse: " + tokenResponse.getMessage());
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(-34, 151)).zoom(10).build()));
                                    Utility.ShowToastMessage(context, tokenResponse.getMessage());

                                    MapsInitializer.initialize(getActivity().getApplicationContext());
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        return;
                                    }
                                    if (googleMap != null) {
                                        googleMap.setMyLocationEnabled(true);
                                        if (lat != null)
                                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(Double.valueOf(lat), Double.valueOf(lng))).zoom(10).build()));
                                        else
                                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(-34, 151)).zoom(10).build()));
                                    }
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    utils.hideDialog();
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            getActivity().finish();
                                        }
                                    }, 2000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    utils.hideDialog();
                }

                @Override
                public void onFailure(Call<StoreListResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    utils.hideDialog();
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
}
