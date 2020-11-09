package c.offerak.speedshopper.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.StoreProduct;
import c.offerak.speedshopper.adapter.MarketListAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.MarketListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.RecyclerItemClickListener;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.GONE;
import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

public class SpeedShopperMarketListFragment extends Fragment /*implements View.OnClickListener*/ {

    public static final String TAG = SpeedShopperMarketListFragment.class.getSimpleName();
    Context context;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private RelativeLayout layout;
    private EditText searchEdit;
    private UserBean userBean;
    private ArrayList<MarketListResponse.DataBean> listBeans = new ArrayList<>();
    private MarketListAdapter adapter;
    private RecyclerView homeList;
    private TextView noDataFound;
    private LinearLayoutManager mLayoutManager;
    View rootView;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.speed_shopper_market_list, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        homeList = rootView.findViewById(R.id.lvList);
        noDataFound = rootView.findViewById(R.id.noDataFound);
        searchEdit = rootView.findViewById(R.id.searchEdit);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        txtTitle.setText("SSTX Market");
        txtSync.setVisibility(GONE);
        history.setVisibility(View.GONE);

        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();
        homeList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), homeList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String storeId = listBeans.get(position).getStore_id();
                        String storeName = listBeans.get(position).getStore_name();
                        String storeAddress = listBeans.get(position).getAddress();
                        Log.e(TAG, "onItemClick: " + storeName);

                        startActivity(new Intent(getActivity(), StoreProduct.class)
                                .putExtra("storeId", storeId)
                                .putExtra("storeAddress", storeAddress)
                                .putExtra("storeName", storeName));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        homeList.addOnScrollListener(recyclerViewOnScrollListener);

        if (utils.isNetworkConnected(getActivity())) {
            listSetup("0");
        } else {
            Toast.makeText(getActivity(), "You are not connected to internet!", Toast.LENGTH_SHORT).show();
        }

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchEdit.getText().toString().isEmpty()) {
                        Toast.makeText(context, "Please Enter Store", Toast.LENGTH_SHORT).show();
                    } else {
                        listSetup("0");
                    }
                    return true;
                }
                return false;
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (utils.isNetworkConnected(getActivity())) {
                    if (searchEdit.getText().toString().isEmpty()) {
                        listSetup("0");
                    }
                } else {
                    Toast.makeText(getActivity(), "You are not connected to internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void listSetup(String page) {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Call<MarketListResponse> call = apiService.getSpeedShopperMarket(userBean.getUserToken(), page, searchEdit.getText().toString());
            call.enqueue(new Callback<MarketListResponse>() {
                @Override
                public void onResponse(Call<MarketListResponse> call, retrofit2.Response<MarketListResponse> response) {
                    try {
                        MarketListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    listBeans.clear();
                                    listBeans = (ArrayList<MarketListResponse.DataBean>) tokenResponse.getData();

                                    if(listBeans.isEmpty()){
                                        homeList.setVisibility(View.GONE);
                                        noDataFound.setVisibility(View.VISIBLE);
                                    }else {
                                        homeList.setVisibility(View.VISIBLE);
                                        noDataFound.setVisibility(View.GONE);
                                    }
                                    adapter = new MarketListAdapter(getActivity(), listBeans);
                                    mLayoutManager = new LinearLayoutManager(getActivity());
                                    homeList.setAdapter(adapter);
                                    homeList.setLayoutManager(mLayoutManager);

                                } else if (tokenResponse.getStatus() == 400) {
                                    homeList.setVisibility(View.GONE);
                                    noDataFound.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "" + tokenResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    Toast.makeText(getActivity(), "" + tokenResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

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
                public void onFailure(Call<MarketListResponse> call, Throwable t) {
                    utils.hideDialog();
                    homeList.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Server Issue!");
                }
            });
        } else {
            homeList.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
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
