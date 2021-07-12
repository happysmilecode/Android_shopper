package c.offerak.speedshopper.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.adapter.MessageAdapter;
import c.offerak.speedshopper.adapter.NotificationAdapter;
import c.offerak.speedshopper.modal.Message;
import c.offerak.speedshopper.modal.Notification;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.MessageListResponse;
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

public class ContactUsFragment extends Fragment {

    public static final String TAG = ContactUsFragment.class.getSimpleName();
    Context context;
    private TextView submitButton;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    private EditText inputType;
    private TextView inpText;
    private LinearLayout messageLayout;
    View rootView;

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<MessageListResponse.DataBean> messageList = new ArrayList<>();
    List<Message> bean=new ArrayList<>();
    TextView noDataFound;
    int page = 1;
    Boolean load = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact_us_layout, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        messageLayout = rootView.findViewById(R.id.messageView);
        submitButton = rootView.findViewById(R.id.btnSubmit);
        inputType = rootView.findViewById(R.id.inputType);
        inpText = rootView.findViewById(R.id.inpText);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans_Semibold.ttf");
        inpText.setTypeface(custom_font);

        txtTitle.setText(R.string.contact_us_small);
        txtSync.setVisibility(View.GONE);
        history.setVisibility(View.GONE);

        recyclerView = rootView.findViewById(R.id.msg_recyclerView);
        noDataFound = rootView.findViewById(R.id.no_message);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        messageAdapter = new MessageAdapter(context, bean);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(messageAdapter);
        callMessageList();

        if (inputType.hasFocus()) {
            messageLayout.setVisibility(View.GONE);
        } else {
            messageLayout.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 1 && load) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        callMessageList();
                    }
                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utils.isNetworkConnected(context)) {
                    utils.hideKeyboard(getActivity());
                    String msg = inputType.getText().toString().trim();

                    if (!msg.equals("")) {
                        utils.showDialog(getActivity());
                        Call<GetResponse> call = apiService.contact(userBean.getUserToken(), msg);
                        call.enqueue(new Callback<GetResponse>() {
                            @Override
                            public void onResponse(Call<GetResponse> call,
                                                   retrofit2.Response<GetResponse> response) {
                                try {
                                    GetResponse tokenResponse = response.body();
                                    if (tokenResponse != null) {
                                        try {
                                            if (tokenResponse.getStatus() == 200) {
                                                inputType.setText("");
                                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                                        tokenResponse.getMessage());
                                            } else if (tokenResponse.getStatus() == 401) {
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
                                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                                        tokenResponse.getMessage());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    utils.hideDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<GetResponse> call, Throwable t) {
                                utils.hideDialog();
                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                        "Please check your internet connection!");
                            }
                        });
                    }
                } else {
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
                }
            }
        });
        OneSignal.addTrigger("contact", "loaded");
    }

    public void callMessageList() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Log.e(TAG, "page: " + page);

            Call<MessageListResponse> call = apiService.getMessagesList(userBean.getUserToken(),
                    String.valueOf(page)+"");
            call.enqueue(new Callback<MessageListResponse>() {
                @Override
                public void onResponse(Call<MessageListResponse> call, Response<MessageListResponse> response) {
                    try {
                        MessageListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    messageList.clear();
                                    load = false;
                                    messageList = response.body().getData();
                                    Log.e(TAG, "onResponse: " + messageList);
                                    for (int i = 0; i < messageList.size(); i++) {
                                        bean.add(new Message(messageList.get(i).getCreated_at(),messageList.get(i).getTitle(),messageList.get(i).getMessage()));
                                    }
                                    if (messageList.size() == 5) {
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
                                    messageAdapter.notifyDataSetChanged();
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
                public void onFailure(Call<MessageListResponse> call, Throwable t) {
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

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("contact", "loaded");
    }
}
