package c.offerak.speedshopper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.HomeListAdapter;
import c.offerak.speedshopper.modal.HomeListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.StoreListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.RecyclerItemClickListener;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static c.offerak.speedshopper.activity.HomeScreen.footer;
import static c.offerak.speedshopper.activity.HomeScreen.lat;
import static c.offerak.speedshopper.activity.HomeScreen.lng;
import static c.offerak.speedshopper.activity.HomeScreen.screenTitle;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = SearchActivity.class.getSimpleName();
    Context context;
    public static List<HomeListBean> listBeans = new ArrayList<>();
    private HomeListAdapter adapter;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    private String searchedText = "";
    private TextView emptyView;
    private RecyclerView homeList;
    private ImageView icBackButton;
    private ImageView toggleButton;
    private TextView txtTitle;
    private EditText searchEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        init();
    }

    public void init() {
        footer.setVisibility(View.VISIBLE);
        screenTitle.setText(R.string.home);

        searchEdit = findViewById(R.id.searchEdit);
        homeList = findViewById(R.id.lvList);
        emptyView = findViewById(R.id.empty_view);
        icBackButton = findViewById(R.id.backButton);
        toggleButton = findViewById(R.id.toggleButton);
        txtTitle = findViewById(R.id.txtTitle);

        txtTitle.setText(R.string.search_store);
        icBackButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);

        adapter = new HomeListAdapter(this, listBeans);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        userBean = mySharedPreference.getLoginDetails();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        homeList.setLayoutManager(mLayoutManager);
        homeList.setAdapter(adapter);
        homeList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, homeList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String s = listBeans.get(position).getId();
                        String storeName = listBeans.get(position).getName();
                        String storeLogo = listBeans.get(position).getStoreImage();
                        String storeAddress = listBeans.get(position).getAddress();

                        setResult(RESULT_OK, new Intent()
                                .putExtra("storeId", s)
                                .putExtra("storeLogo", storeLogo)
                                .putExtra("storeAddress", storeAddress)
                                .putExtra("storeName", storeName));
                        finish();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        if (utils.isNetworkConnected(this)) {
            listSetup("");
        } else {
            Toast.makeText(context, "You are not connected to internet!", Toast.LENGTH_SHORT).show();
        }
        icBackButton.setOnClickListener(v -> finish());

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchEdit.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Enter Product", Toast.LENGTH_SHORT).show();
                    }else {
                        listSetup("");
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
                if (utils.isNetworkConnected(context) ) {

                    if (searchEdit.getText().toString().isEmpty()) {
                        listSetup("");
                    }
                } else {
                    Toast.makeText(context, "You are not connected to internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void listSetup(String storeName) {
        if(utils.isNetworkConnected(context)) {
            Call<StoreListResponse> call = apiService.getStores(userBean.getUserToken(), lat, lng,String.valueOf(HomeScreen.distance));
            call.enqueue(new Callback<StoreListResponse>() {
                @Override
                public void onResponse(Call<StoreListResponse> call, retrofit2.Response<StoreListResponse> response) {

                    try {
                        StoreListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            List<StoreListResponse.DataBean> list = tokenResponse.getData();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    emptyView.setVisibility(View.INVISIBLE);
                                    homeList.setVisibility(View.VISIBLE);
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
                                    }
                                    adapter.notifyDataSetChanged();
                                } else if (tokenResponse.getStatus() == 400) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                    if (tokenResponse.getMessage().equals("No store found")) {
                                        emptyView.setVisibility(View.VISIBLE);
                                        homeList.setVisibility(View.INVISIBLE);
                                    }
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
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
                }

                @Override
                public void onFailure(Call<StoreListResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
}
