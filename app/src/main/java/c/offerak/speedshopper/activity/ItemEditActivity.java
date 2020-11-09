package c.offerak.speedshopper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.LocationAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.SpeedAvailableItemResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.ProgressHUD;
import c.offerak.speedshopper.utils.Utility;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static c.offerak.speedshopper.activity.LandingScreen.lat;
import static c.offerak.speedshopper.activity.LandingScreen.lng;

public class ItemEditActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    @BindView(R.id.item_name)
    public TextView itemNameTextView;
    @BindView(R.id.minus_button)
    public Button minus_button;
    @BindView(R.id.plus_button)
    public Button plus_button;
    @BindView(R.id.quantityText)
    public TextView quantityTextView;
    @BindView(R.id.select_aisle)
    public TextView selectAisleTextView;
    @BindView(R.id.done_button)
    public ImageView doneButton;
    EditText cet_search;
    RecyclerView recyclerView;
    ImageView locationDialogCross;

    LocationAdapter locationAdapter;
    int itemQuantity, m_pos;
    ProgressHUD progressHUD;
    public ImageView icBackButton, toggleButton;
    public Utils utils = new Utils();
    public ApiInterface apiService;
    public UserBean userBean;
    MySharedPreference mySharedPreference;
    public String listId, storeId, item_id, m_id, aisleLocation;
    public ArrayList<String> aisleList=new ArrayList<>();
    public ArrayList<String> aislePositionList=new ArrayList<>();
    Dialog locationDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        context = this;
        init();
    }


    public void init() {
        ButterKnife.bind(this);
        initLayout();
        initVariables();
        getDataFromIntent();
        initClickEvents();
    }

    void initVariables() {
        mySharedPreference = new MySharedPreference(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        userBean = mySharedPreference.getLoginDetails();
    }

    void getDataFromIntent() {
        Intent intent = getIntent();
        item_id = intent.getStringExtra("item_id");
        m_id = intent.getStringExtra("m_id");
        listId = intent.getStringExtra("listId");
        storeId = intent.getStringExtra("storeId");
        String itemName = intent.getStringExtra("item_name");
        aisleLocation = intent.getStringExtra("aisle_number");
        itemQuantity = Integer.parseInt(intent.getStringExtra("item_quantity"));
        m_pos = intent.getIntExtra("m_pos", 0);
        aisleList = intent.getStringArrayListExtra("aisle_list");
        aislePositionList = aisleList;

        setEnabledForMinusBtn(itemQuantity);

        // Set Value from Intent

        itemNameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));
        selectAisleTextView.setText(aisleLocation);
    }

    void showAisleList() {

        final CharSequence[] items = aisleList.toArray(new CharSequence[aisleList.size()]);

        try {
            if (aisleList.size() > 0) {
                locationDialog = new Dialog(context);
                locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                locationDialog.setCancelable(false);
                locationDialog.setContentView(R.layout.dialog_location);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = locationDialog.getWindow();

                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);

                locationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
                cet_search = locationDialog.findViewById(R.id.cet_search);
                recyclerView = locationDialog.findViewById(R.id.recyclerView);
                locationDialogCross = locationDialog.findViewById(R.id.cross);

                locationAdapter = new LocationAdapter(context, aisleList,item_id);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(locationAdapter);

                cet_search.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (cet_search.getText().toString().isEmpty()){
                            locationAdapter = new LocationAdapter(context, aisleList,item_id);
                            recyclerView.setAdapter(locationAdapter);
                        }else {
                            locationAdapter.getFilter().filter(cet_search.getText().toString());
                        }
                        return true;
                    }
                    return false;
                });

                cet_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if(s.toString().isEmpty()){
                            aisleList=aislePositionList;
                            locationAdapter = new LocationAdapter(context, aisleList,item_id);
                            recyclerView.setAdapter(locationAdapter);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().isEmpty()){
                            aisleList=aislePositionList;
                            locationAdapter = new LocationAdapter(context, aisleList,item_id);
                            recyclerView.setAdapter(locationAdapter);
                        }
                        locationAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().isEmpty()){
                            aisleList=aislePositionList;
                            locationAdapter = new LocationAdapter(context, aisleList,item_id);
                            recyclerView.setAdapter(locationAdapter);
                        }
//                        locationAdapter.getFilter().filter(s.toString().trim());
                    }
                });
                locationDialog.show();
                locationDialogCross.setOnClickListener(v -> locationDialog.dismiss());

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Items available");
                builder.setItems(items, (dialog, item) -> {
                    // Do something with the selection
                    //addItem(listItems.get(item));
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("ArrayIndexOutOfBound", "addCategoryItem: ");
        }

    }

    public void locationChanged(String address) {
        if(utils.isNetworkConnected(context)) {
            progressHUD = ProgressHUD.show(context, "Loading", true, false, null);
            Call<SpeedAvailableItemResponse> call = apiService.addItemLocation(userBean.getUserToken(), item_id, lat, storeId, address, lng, listId);
            call.enqueue(new Callback<SpeedAvailableItemResponse>() {
                @Override
                public void onResponse(Call<SpeedAvailableItemResponse> call, retrofit2.Response<SpeedAvailableItemResponse> response) {
                    progressHUD.dismiss();
                    try {
                        SpeedAvailableItemResponse tokenResponse = response.body();
                        try {
                            if (tokenResponse != null) {
                                if ((response.body() != null ? response.body().getStatus() : 0) ==200) {
                                    aisleLocation = address;
                                    selectAisleTextView.setText(address);
                                    locationDialog.dismiss();
                                }
                                else {
                                    Utility.ShowToastMessage(context, response.body() != null ? response.body().getMessage() : null);
                                    if (locationDialog != null)
                                        locationDialog.dismiss();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SpeedAvailableItemResponse> call, Throwable t) {
                    progressHUD.dismiss();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }


    void setEnabledForMinusBtn(int itemQuantity) {
        // Disable/Enable minus button depends on quantity
        if (itemQuantity > 1) {
            minus_button.setEnabled(true);
            minus_button.setAlpha(1.0f);
        } else {
            minus_button.setEnabled(false);
            minus_button.setAlpha(0.5f);
        }

    }


    public void updateQuantity() {
        String quantity = quantityTextView.getText().toString();
        if(utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.updateItemQuantity(userBean.getUserToken(), m_id, quantity);
            progressHUD = ProgressHUD.show(context, "Loading", true, false, null);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    progressHUD.dismiss();
                    try {
                        GetResponse myResponse = response.body();
                        if (myResponse != null && myResponse.getStatus() == 200) {
                            itemQuantity = Integer.parseInt(quantity);
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), myResponse.getMessage());
                            Intent retData = new Intent();
                            retData.putExtra("ITEM_QUANTITY", itemQuantity);
                            retData.putExtra("ITEM_LOCATION", aisleLocation);
                            retData.putExtra("m_pos", m_pos);
                            setResult(Activity.RESULT_OK, retData);
                            finish();
                        } else {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), myResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetResponse> call, Throwable t) {
                    progressHUD.dismiss();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to reset password!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
    void initLayout() {
        // Hide and show menu items
        icBackButton = findViewById(R.id.backButton);
        toggleButton = findViewById(R.id.toggleButton);
        icBackButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);

    }

    void initClickEvents() {
        icBackButton.setOnClickListener(v -> {
            Intent retData = new Intent();
            retData.putExtra("ITEM_QUANTITY", itemQuantity);
            retData.putExtra("ITEM_LOCATION", aisleLocation);
            retData.putExtra("m_pos", m_pos);
            setResult(Activity.RESULT_OK, retData);
            finish();
        });
        minus_button.setOnClickListener(this);
        plus_button.setOnClickListener(this);
        selectAisleTextView.setOnClickListener(this);
        doneButton.setOnClickListener(this);
    }

    void updateQuantityTextfield(Boolean isAdd) {
        int quantity = Integer.parseInt(quantityTextView.getText().toString());
        quantity = isAdd ? quantity + 1 : quantity - 1;
        setEnabledForMinusBtn(quantity);
        quantityTextView.setText(String.valueOf(quantity));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.minus_button:
                updateQuantityTextfield(false);
                break;
            case R.id.plus_button:
                updateQuantityTextfield(true);
                break;
            case R.id.select_aisle:
                showAisleList();
                break;
            case R.id.done_button:
                updateQuantity();
                break;
        }
    }
}
