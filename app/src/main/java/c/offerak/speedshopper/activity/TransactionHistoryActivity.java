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

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.TransactionHistoryAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.TransactionHistoryResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    public static final String TAG = TransactionHistoryActivity.class.getSimpleName();
    Context context;
    RecyclerView recyclerView;
    TransactionHistoryAdapter transactionHistoryAdapter;
    List<TransactionHistoryResponse.DataBean> transactionHistoryList = new ArrayList();
    private ApiInterface apiService;
    private Utils utils;
    private UserBean userBean;
    private TextView txtTitle;
    private ImageView toggleButton, backButton;
    TextView noDataFound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasaction_history);
        context = this;
        init();
    }

    public void init() {
        utils = new Utils();
        recyclerView = findViewById(R.id.recyclerView);
        noDataFound = findViewById(R.id.noDataFound);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        userBean = mySharedPreference.getLoginDetails();

        txtTitle = findViewById(R.id.txtTitle);
        toggleButton = findViewById(R.id.toggleButton);
        backButton = findViewById(R.id.backButton);
        txtTitle.setText("Transaction History");

        backButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);

        transactionHistoryAdapter = new TransactionHistoryAdapter(context, transactionHistoryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        callTransactionHistoryAPI();
        recyclerView.setAdapter(transactionHistoryAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //------------------- Transaction History ---------------------
    public void callTransactionHistoryAPI() {
        if(utils.isNetworkConnected(context)) {
            utils.showDialog(context);
            Log.e(TAG, "TOKEN: " + userBean.getUserToken());
            Call<TransactionHistoryResponse> call = apiService.getTransactionHistoryAPI(userBean.getUserToken());
            call.enqueue(new Callback<TransactionHistoryResponse>() {
                @Override
                public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                    try {
                        TransactionHistoryResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    transactionHistoryList.clear();
                                    transactionHistoryList = response.body().getData();
                                    List<TransactionHistoryResponse.DataBean> bean = tokenResponse.getData();
                                    Log.e(TAG, "onResponse: " + bean);

                                    if(bean.isEmpty()){
                                        recyclerView.setVisibility(View.GONE);
                                        noDataFound.setVisibility(View.VISIBLE);
                                    }else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        noDataFound.setVisibility(View.GONE);
                                    }

                                    if (bean != null) {
                                        Log.e(TAG, "bean: " + bean);
                                        transactionHistoryList = bean;
                                        transactionHistoryAdapter = new TransactionHistoryAdapter(context, bean);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(transactionHistoryAdapter);
                                    }
                                } else if (response.body().getStatus() == 401) {
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
                public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                    utils.hideDialog();
                    recyclerView.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                    Log.e(TAG, "onFailure: " + t.toString());
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        }else {
            recyclerView.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
}
