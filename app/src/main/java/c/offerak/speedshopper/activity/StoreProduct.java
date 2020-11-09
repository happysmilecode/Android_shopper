package c.offerak.speedshopper.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.ProductListAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.ProductListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.RecyclerItemClickListener;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class StoreProduct extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = StoreProduct.class.getSimpleName();
    Context context;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private RelativeLayout layout;
    private TextView txtTitle;
    private EditText searchEdit;
    private UserBean userBean;
    private ArrayList<ProductListResponse.DataBean> listBeans = new ArrayList<>();
    private ProductListAdapter adapter;
    private String storeId = "";
    private RecyclerView homeList;
    private ImageView toggleButton, backButton;
    TextView noDataFound;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product);
        context = this;
        init();
    }

    public void init() {
        searchEdit = findViewById(R.id.searchEdit);
        homeList = findViewById(R.id.lvList);
        txtTitle = findViewById(R.id.txtTitle);
        toggleButton = findViewById(R.id.toggleButton);
        backButton = findViewById(R.id.backButton);
        noDataFound = findViewById(R.id.noDataFound);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(StoreProduct.this);
        userBean = mySharedPreference.getLoginDetails();
        Intent intent = getIntent();
        if (intent.hasExtra("storeId")) {
            storeId = intent.getStringExtra("storeId");
        }

        if (intent.hasExtra("storeName")) {
            txtTitle.setText(intent.getStringExtra("storeName"));
        }

        backButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEdit.setOnClickListener(this);

        homeList.addOnItemTouchListener(
                new RecyclerItemClickListener(StoreProduct.this, homeList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String productName = listBeans.get(position).getProduct_name();
                        String productPrice = listBeans.get(position).getPrice();
                        String productDescription = listBeans.get(position).getDescription();
                        String productImage = listBeans.get(position).getPath() + listBeans.get(position).getProduct_image();

                        startActivity(new Intent(context, ProductDetailsActivity.class)
                                .putExtra("productName", productName)
                                .putExtra("productPrice", productPrice)
                                .putExtra("productDescription", productDescription)
                                .putExtra("productImage", productImage)
                                .putExtra("store_id", listBeans.get(position).getStore_id())
                                .putExtra("productDiscount", listBeans.get(position).getDiscount())
                                .putExtra("productWebsite", listBeans.get(position).getWebsite())
                                .putExtra("product_id", listBeans.get(position).getProduct_id()));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        if (utils.isNetworkConnected(context)) {
            listSetup("0", storeId);
        } else {
            Toast.makeText(context, "You are not connected to internet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_cancel:
                dialog.dismiss();
                break;

            case R.id.searchEdit:
                if (searchEdit.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please Enter Product", Toast.LENGTH_SHORT).show();
                } else {
                    if (utils.isNetworkConnected(context)) {
                        listSetup("0", storeId);
                    } else {
                        Toast.makeText(context, "You are not connected to internet!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void listSetup(String page, String storeID) {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(context);
            Call<ProductListResponse> call = apiService.getProductByMerchantID(userBean.getUserToken(), page, storeID, searchEdit.getText().toString());
            call.enqueue(new Callback<ProductListResponse>() {
                @Override
                public void onResponse(Call<ProductListResponse> call, retrofit2.Response<ProductListResponse> response) {
                    try {
                        ProductListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    listBeans.clear();
                                    listBeans = (ArrayList<ProductListResponse.DataBean>) tokenResponse.getData();

                                    if(listBeans.isEmpty()){
                                        homeList.setVisibility(View.GONE);
                                        noDataFound.setVisibility(View.VISIBLE);
                                    }else {
                                        homeList.setVisibility(View.VISIBLE);
                                        noDataFound.setVisibility(View.GONE);
                                    }

                                    adapter = new ProductListAdapter(context, listBeans);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                                    homeList.setAdapter(adapter);
                                    homeList.setLayoutManager(mLayoutManager);
                                } else if (tokenResponse.getStatus() == 400) {
                                    homeList.setVisibility(View.GONE);
                                    noDataFound.setVisibility(View.VISIBLE);
                                    Toast.makeText(context, "" + tokenResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            utils.showSnackBar(StoreProduct.this.getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    Toast.makeText(context, "" + tokenResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            utils.showSnackBar(StoreProduct.this.getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

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
                    utils.hideDialog();
                }

                @Override
                public void onFailure(Call<ProductListResponse> call, Throwable t) {
                    homeList.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            homeList.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
}
