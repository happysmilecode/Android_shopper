package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
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

public class NotificationActivity extends AppCompatActivity {

    public static final String TAG = NotificationActivity.class.getSimpleName();
    Context context;
    RecyclerView recyclerView;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    ImageView toggleButton, backButton,txtSync;
    TextView noDataFound;
    TextView txtTitle,history;
    NotificationAdapter notificationAdapter;
    List<Notification> bean = new ArrayList<>();
    List<NotificationListRespose.DataBean> notificationList = new ArrayList<>();
    int page = 1;
    Boolean load = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_code);
        context = this;
        init();
    }

    public void init() {
        recyclerView = findViewById(R.id.recyclerView);
        noDataFound = findViewById(R.id.noDataFound);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        userBean = mySharedPreference.getLoginDetails();
        txtTitle=findViewById(R.id.txtTitle);
        txtSync=findViewById(R.id.txtSync);
        history=findViewById(R.id.history);
        toggleButton=findViewById(R.id.toggleButton);
        backButton=findViewById(R.id.backButton);

        txtTitle.setText(R.string.notifications);
        backButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.GONE);
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        OneSignal.addTrigger("notification", "loaded");
    }

    //------------------------- Notification List ---------------------------------
    public void callNotificationsList() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(context);
            Log.e(TAG, "page: " + page);

            Call<NotificationListRespose> call = apiService.getNotificationsList(userBean.getUserToken(),
                    String.valueOf(page) + "");
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
                                        bean.add(new Notification(notificationList.get(i).getCreated_at(), notificationList.get(i).getTitle(), notificationList.get(i).getType(), notificationList.get(i).getMessage()));
                                    }
                                    if (notificationList.size() == 10) {
                                        page = page + 1;
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
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(),
                                            tokenResponse.getMessage());

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        }
                                    }, 2000);
                                } else {
                                    load = false;
                                    recyclerView.setVisibility(View.GONE);
                                    noDataFound.setVisibility(View.VISIBLE);
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(),
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
                    utils.showSnackBar(getWindow().getDecorView().getRootView(),
                            "Server Issue!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
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
        OneSignal.addTrigger("notification", "loaded");
    }
}
