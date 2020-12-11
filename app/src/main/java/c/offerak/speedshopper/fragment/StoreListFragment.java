package c.offerak.speedshopper.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.HomeScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.adapter.HomeListAdapter;
import c.offerak.speedshopper.modal.HomeListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.AddNewStore;
import c.offerak.speedshopper.response.StoreListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.ProgressHUD;
import c.offerak.speedshopper.utils.RecyclerItemClickListener;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static c.offerak.speedshopper.activity.HomeScreen.footer;
import static c.offerak.speedshopper.activity.HomeScreen.lat;
import static c.offerak.speedshopper.activity.HomeScreen.lng;
import static c.offerak.speedshopper.activity.HomeScreen.screenTitle;
import static c.offerak.speedshopper.activity.HomeScreen.spinner;

public class StoreListFragment extends Fragment {


    public static final String TAG = StoreListFragment.class.getSimpleName();
    Context context;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static List<HomeListBean> listBeans = new ArrayList<>();
    public List<HomeListBean> storeListBeans = new ArrayList<>();
    public List<HomeListBean> filterListBeans = new ArrayList<>();
    private HomeListAdapter adapter;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    public static EditText searchEdit;
    private LinearLayoutManager mLayoutManager;
    View rootView;
    Dialog dialog;
    LinearLayout img_add;
    RecyclerView homeList;
    double longitude = 0, latitude = 0;
    ProgressHUD progressHUD;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_list, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        footer.setVisibility(View.VISIBLE);
        screenTitle.setText(R.string.stores_near_you);
        spinner.setVisibility(View.VISIBLE);

        homeList = rootView.findViewById(R.id.lvList);
        adapter = new HomeListAdapter(getActivity(), storeListBeans);
        searchEdit = rootView.findViewById(R.id.searchEdit);
        img_add = rootView.findViewById(R.id.img_add);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        mLayoutManager = new LinearLayoutManager(getActivity());
        homeList.setLayoutManager(mLayoutManager);
        homeList.setAdapter(adapter);
        homeList.addOnScrollListener(recyclerViewOnScrollListener);
        homeList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), homeList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        storeListBeans = adapter.getList();

                        String storeId = storeListBeans.get(position).getId();
                        String storeName = storeListBeans.get(position).getName();
                        String storeLogo = storeListBeans.get(position).getStoreImage();
                        String storeAddress = storeListBeans.get(position).getAddress();

                        getActivity().setResult(RESULT_OK, new Intent()
                                .putExtra("storeId", storeId)
                                .putExtra("storeLogo", storeLogo)
                                .putExtra("storeAddress", storeAddress)
                                .putExtra("storeName", storeName));
                        showDialogStoreList(storeName, storeId, "logo_0");
//                        getActivity().finish();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        if (utils.isNetworkConnected(getActivity())) {
            listSetup();
        } else {
            Toast.makeText(getActivity(), "You are not connected to internet!", Toast.LENGTH_SHORT).show();
        }

        img_add.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(context);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

//            try {
//                startActivityForResult(new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                        .setBoundsBias(toBounds(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 80000))
//                        .build(getActivity()), PLACE_AUTOCOMPLETE_REQUEST_CODE);
//
//            } catch (GooglePlayServicesRepairableException e) {
//                // TODO: Handle the error.
//            } catch (GooglePlayServicesNotAvailableException e) {
//                // TODO: Handle the error.
//            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //----------------------- Store Dialog List -----------------------
    public void showDialogStoreList(String storeName, String storeId, String imageName) {
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
                ((HomeScreen) getActivity()).addListToServer(listName, storeId, imageName);
            } else {
                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please enter the list name!");
            }
        });

        imgCross.setOnClickListener(v12 -> dialog.dismiss());
        dialog.show();
    }

    private void addStoreToServer(List<Address> _address, Place place) {

        String[] addressLines = String.valueOf(place.getAddress()).split(",");
        Log.e(TAG, "addressLines: " + addressLines[0]);
        String address = "";
        String name = (String) place.getName();
        String phone = (String) place.getPhoneNumber();
        if (_address.get(0).getThoroughfare() != null) {
            address = _address.get(0).getThoroughfare();
        } else {
            address = addressLines[0];
        }
        String city = _address.get(0).getLocality();
        String country = _address.get(0).getCountryName();
        String postal_code = _address.get(0).getPostalCode();
        String state = _address.get(0).getAdminArea();
        LatLng latLng = place.getLatLng();

        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Log.e(TAG, "Longitude: " + latLng.latitude + " Latitude: " + latLng.longitude);
        if (phone == null) {
            phone = "";
        }

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(Double.parseDouble(lat));
        startPoint.setLongitude(Double.parseDouble(lng));

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(latitude);
        endPoint.setLongitude(longitude);

        double distance = startPoint.distanceTo(endPoint) / 1609.3440057765;
        Log.e(TAG, "addStoreToServer: " + distance);
        Log.e(TAG, "addStoreToServer: " + startPoint.distanceTo(endPoint) / 1000);
        Log.e(TAG, "distance2: " + startPoint.distanceTo(endPoint) / 1609.3440057765);

//        if(HomeScreen.distance>distance){
        Log.e(TAG, "addStoreToServer1: ");

        if (utils.isNetworkConnected(context)) {
            Call<AddNewStore> call = apiService.addNewStore(userBean.getUserToken(), name,
                    "", "", address, String.valueOf(latLng.latitude),
                    String.valueOf(latLng.longitude), lat, lng, city, state, country, postal_code);
            call.enqueue(new Callback<AddNewStore>() {
                @Override
                public void onResponse(Call<AddNewStore> call, retrofit2.Response<AddNewStore> response) {

                    try {
                        AddNewStore tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {

                                   /* if (tokenResponse.getMessage().equals("Store is not in your range.")) {
                                        getActivity().setResult(2000, new Intent());
//                                        getActivity().finish();
                                    } else */
                                    if (tokenResponse.getMessage().equals("Session expired")) {
                                        utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                                getActivity().finish();
                                            }
                                        }, 2000);
                                    } else {
                                        AddNewStore.DataBean dataBean = tokenResponse.getData();
                                        listSetup();
                                        getActivity().setResult(RESULT_OK, new Intent()
                                                .putExtra("storeId", dataBean.getId())
                                                .putExtra("storeLogo", dataBean.getLogo())
                                                .putExtra("storeAddress", dataBean.getAddress())
                                                .putExtra("storeName", dataBean.getName()));
                                        showDialogStoreList(dataBean.getName(), dataBean.getId(), "logo_0");
//                                        getActivity().finish();
                                    }
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
                public void onFailure(Call<AddNewStore> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
        /*}else {
            Log.e(TAG, "addStoreToServer2: " );
            Utility.ShowToastMessage(context,R.string.stores_not_in_range);
        }*/
    }

    public void listSetup() {
//        utils.showDialog(context);
        if (utils.isNetworkConnected(context)) {
            progressHUD=ProgressHUD.show(context,"Please Wait...",true,false,null);
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
                                    if(progressHUD!=null&&progressHUD.isShowing()){
                                        progressHUD.dismiss();
                                    }
                                    listBeans.clear();
                                    storeListBeans.clear();
                                    filterListBeans.clear();
                                    for (int i = 0; i < list.size(); i++) {
                                        HomeListBean homeListBean = new HomeListBean();
                                        homeListBean.setStoreImage(list.get(i).getPath() + "" + list.get(i).getLogo());
                                        homeListBean.setAddress(list.get(i).getAddress());
                                        homeListBean.setId(list.get(i).getId());
                                        homeListBean.setName(list.get(i).getName());
                                        homeListBean.setLatitude(list.get(i).getLatitude());
                                        homeListBean.setLongitude(list.get(i).getLongitude());

                                        listBeans.add(homeListBean);
                                        storeListBeans.add(homeListBean);
                                        filterListBeans.add(homeListBean);
                                    }
                                    Log.e(TAG, "onResponse: "+storeListBeans );
                                    adapter.notifyDataSetChanged();
                                } else if (tokenResponse.getStatus() == 400) {
                                    if(progressHUD!=null&&progressHUD.isShowing()){
                                        progressHUD.dismiss();
                                    }
                                    listBeans.clear();
                                    storeListBeans.clear();
                                    filterListBeans.clear();
                                    adapter.notifyDataSetChanged();
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
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
//                    if (utils.isDialogShowing()) {
//                        utils.hideDialog();
//                    }
                }

                @Override
                public void onFailure(Call<StoreListResponse> call, Throwable t) {
                    if(progressHUD!=null&&progressHUD.isShowing()){
                        progressHUD.dismiss();
                    }
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        footer.setVisibility(View.VISIBLE);
        screenTitle.setText(R.string.stores_near_you);
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (place.getLatLng() == null) {
                return;
            }
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                Log.e(TAG, "onActivityResult: " + addresses);
                addStoreToServer(addresses, place);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            Log.d(TAG, "onScrolled: ");

            /*if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    listSetup();
                }
            }*/
        }
    };
}
