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

import com.onesignal.OneSignal;

import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.adapter.FrequentlyAskedQuestionsAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.FAQ_Response;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

public class FrequentlyAskedQuestionsFragment extends Fragment {

    public static final String TAG = FrequentlyAskedQuestionsFragment.class.getSimpleName();
    Context context;
    private RecyclerView faqListView;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    private List<FAQ_Response.DataBean> dataList;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_faq, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        txtTitle.setText(R.string.faq);
        txtSync.setVisibility(View.GONE);
        history.setVisibility(View.GONE);

        faqListView = rootView.findViewById(R.id.cardViewList);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();
        getQuestionsList();

        /*faqListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: "+dataList.get(position).getImage() );
                startActivity(new Intent(getActivity(), FAQ_Details.class)
                        .putExtra("question", dataList.get(position).getQuestion())
                        .putExtra("answer", dataList.get(position).getAnswer())
                        .putExtra("image", dataList.get(position).getImage()));
            }
        });*/
        OneSignal.addTrigger("faq", "loaded");
    }

    private void getQuestionsList() {
        if(utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Call<FAQ_Response> call = apiService.faq(userBean.getUserToken());
            call.enqueue(new Callback<FAQ_Response>() {
                @Override
                public void onResponse(Call<FAQ_Response> call, retrofit2.Response<FAQ_Response> response) {
                    utils.hideDialog();
                    try {
                        FAQ_Response tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {

                                    List<FAQ_Response.DataBean> bean = tokenResponse.getData();
                                    Log.e(TAG, "bean: "+bean );

                                    if (bean != null) {
                                        FrequentlyAskedQuestionsAdapter frequentlyAskedQuestionsAdapter =
                                                new FrequentlyAskedQuestionsAdapter(context, bean);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                                        faqListView.setLayoutManager(layoutManager);
                                        faqListView.setItemAnimator(new DefaultItemAnimator());
                                        faqListView.setAdapter(frequentlyAskedQuestionsAdapter);
                                        dataList = bean;
                                    }
                                } else if (tokenResponse.getStatus() == 401) {
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
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
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
                public void onFailure(Call<FAQ_Response> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        }else {
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
        OneSignal.addTrigger("faq", "loaded");
    }
}
