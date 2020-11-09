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

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.adapter.NotificationAdapter;
import c.offerak.speedshopper.modal.Notification;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.NotificationListRespose;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

public class NotificationFragment extends Fragment {

    public static final String TAG = NotificationFragment.class.getSimpleName();
    Context context;
    View rootView;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    TextView noDataFound;
    List<NotificationListRespose.DataBean> notificationList = new ArrayList<>();
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    int page = 1;
    Boolean load = true;
    List<Notification> bean=new ArrayList<>();

    public NotificationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        noDataFound = rootView.findViewById(R.id.noDataFound);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        txtTitle.setText(R.string.notifications);
        txtSync.setVisibility(View.GONE);
        history.setVisibility(View.GONE);
        notificationAdapter = new NotificationAdapter(context, bean);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notificationAdapter);
        callNotificationsList();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 1 && load == true) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        callNotificationsList();
                    }
                }
            }
        });
    }

    //------------------------- Notification List ---------------------------------
    public void callNotificationsList() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Log.e(TAG, "page: " + page);

            Call<NotificationListRespose> call = apiService.getNotificationsList(userBean.getUserToken(),
                    String.valueOf(page)+"");
            call.enqueue(new Callback<NotificationListRespose>() {
                @Override
                public void onResponse(Call<NotificationListRespose> call, Response<NotificationListRespose> response) {
                    try {
                        NotificationListRespose tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    notificationList.clear();
                                    load = false;
                                    notificationList = response.body().getData();
                                    Log.e(TAG, "onResponse: " + notificationList);
                                    for (int i = 0; i < notificationList.size(); i++) {
                                        bean.add(new Notification(notificationList.get(i).getCreated_at(),notificationList.get(i).getTitle(),notificationList.get(i).getType(),notificationList.get(i).getMessage()));
                                    }
                                    if (notificationList.size() == 10) {
                                        page = page+1;
                                        load = true;
                                    }

                                    if(bean.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        noDataFound.setVisibility(View.VISIBLE);
                                    }else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        noDataFound.setVisibility(View.GONE);
                                    }
                                    notificationAdapter.notifyDataSetChanged();
                                } else if (response.body().getStatus() == 401) {
                                    load = false;
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
                                    load = false;
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
                public void onFailure(Call<NotificationListRespose> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    utils.hideDialog();
                    recyclerView.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                            "Please check your internet connection!");
                }
            });
        } else {
            recyclerView.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
}
