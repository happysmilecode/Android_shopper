package c.offerak.speedshopper.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.adapter.CouponAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.BuyListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

public class CouponFragment extends Fragment {

    public static final String TAG = CouponFragment.class.getSimpleName();
    Context context;
    View rootView;
    RecyclerView recyclerView;
    CouponAdapter couponAdapter;
    List<BuyListResponse.DataBean> couponCodeList = new ArrayList<>();
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    TextView noDataFound;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_coupon_code, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        noDataFound = rootView.findViewById(R.id.noDataFound);

        Log.e(TAG, "Firbase Token: " + MySharedPreference.getSharedPreferences(context, Constants.DEVICE_ID));
        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        txtTitle.setText(R.string.coupon_code);
        txtSync.setVisibility(View.GONE);
        history.setVisibility(View.GONE);

        couponAdapter = new CouponAdapter(context, couponCodeList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        callCouponCodeListAPI();
        recyclerView.setAdapter(couponAdapter);

        OneSignal.addTrigger("coupon", "loaded");
    }

    //--------------------- List Coupon Code ---------------------
    public void callCouponCodeListAPI() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Log.e(TAG, "TOKEN: " + userBean.getUserToken());
            Call<BuyListResponse> call = apiService.getBuyListAPI(userBean.getUserToken());
            call.enqueue(new Callback<BuyListResponse>() {
                @Override
                public void onResponse(Call<BuyListResponse> call, Response<BuyListResponse> response) {
                    try {
                        BuyListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    couponCodeList.clear();
                                    couponCodeList = response.body().getData();
                                    Log.e(TAG, "onResponse: " + couponCodeList);
                                    List<BuyListResponse.DataBean> bean = tokenResponse.getData();

                                    if (bean.isEmpty()) {
                                        recyclerView.setVisibility(View.GONE);
                                        noDataFound.setVisibility(View.VISIBLE);
                                    } else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        noDataFound.setVisibility(View.GONE);
                                    }

                                    if (bean != null) {
                                        Log.e(TAG, "onResponse: " + bean);
                                        couponCodeList = bean;
                                        couponAdapter = new CouponAdapter(context, bean);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(couponAdapter);
                                    }

                                } else if (response.body().getStatus() == 401) {
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                            tokenResponse.getMessage());

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            getActivity().finish();
                                        }
                                    }, 2000);
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    noDataFound.setVisibility(View.VISIBLE);
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                            tokenResponse.getMessage());
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
                public void onFailure(Call<BuyListResponse> call, Throwable t) {
                    utils.hideDialog();
                    recyclerView.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                    Log.e(TAG, "onFailure: " + t.toString());
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Server Issue!");
                }
            });
        }else {
            recyclerView.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("coupon", "loaded");
    }
}
