package c.offerak.speedshopper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.StoreImageAdapter;
import c.offerak.speedshopper.fragment.ShoppingListsFragment;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.RecyclerItemListener;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

public class StoreImageActivity extends AppCompatActivity {

    Context context;

//    String[] imageNameArray = {"store1", "store2", "store3", "store4", "store5", "store6", "store7", "store8", "store9", "store10", "store11"
//            ,"store12", "store13", "store14", "store15", "store16", "store17", "store18", "store19", "store20", "store21", "store22", "store23", "store24", "store25"
//            ,"store26", "store27", "store28", "store29", "store30"};
    String[] imageCodeArray = {"logo_1", "logo_2", "logo_3", "logo_4", "logo_5", "logo_6", "logo_7", "logo_8", "logo_9", "logo_10", "logo_11", "logo_12", "logo_13", "logo_14","logo_15", "logo_16", "logo_17", "logo_18", "logo_19", "logo_20",
            "logo_21", "logo_22", "logo_23", "logo_24", "logo_25", "logo_26", "logo_27", "logo_28", "logo_29", "logo_30", "logo_31","logo_32","logo_33","logo_34", "logo_35", "logo_36", "logo_37", "logo_38", "logo_39", "logo_40",
            "logo_41", "logo_42", "logo_43", "logo_44", "logo_45", "logo_46", "logo_47", "logo_48", "logo_49", "logo_50", "logo_51", "logo_52", "logo_53", "logo_54", "logo_55", "logo_56", "logo_57", "logo_58", "logo_59", "logo_60",
            "logo_61", "logo_62", "logo_63", "logo_64", "logo_65", "logo_66", "logo_67", "logo_68", "logo_69", "logo_70", "logo_71","logo_72","logo_73","logo_74", "logo_75", "logo_76", "logo_77", "logo_78", "logo_79", "logo_80",
            "logo_81", "logo_82", "logo_83", "logo_84", "logo_85", "logo_86", "logo_87", "logo_88", "logo_89", "logo_90", "logo_91", "logo_92", "logo_93", "logo_94", "logo_95", "logo_96", "logo_97", "logo_98", "logo_99", "logo_100", "logo_101", "logo_102", "logo_103"};

    RecyclerView mRecyclerView;
    public StoreImageAdapter imgItemsAdapter;
    public String listName, storeName, listId, storeId, id;

    private Utils utils = new Utils();
    private ApiInterface apiService;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_image);
        context = this;

        Intent intent = getIntent();
        listId = intent.getStringExtra("listId");
        storeId = intent.getStringExtra("storeId");
        listName = intent.getStringExtra("listName");
        storeName = intent.getStringExtra("storeName");

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        userBean = mySharedPreference.getLoginDetails();

        mRecyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(StoreImageActivity.this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        imgItemsAdapter = new StoreImageAdapter(StoreImageActivity.this,  imageCodeArray);

        mRecyclerView.setAdapter(imgItemsAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), mRecyclerView,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("imageId",imageCodeArray[position]);
                        editor.apply();

                        updateStoreLogo(listId, imageCodeArray[position]);

                    }
                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));
    }

    private void updateStoreLogo(String id, String imageName) {
        if (utils.isNetworkConnected(this)) {
            utils.showDialog(this);
            Call<GetResponse> call = apiService.updateShoppingListImage(userBean.getUserToken(), id, imageName);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    try {
                        GetResponse tokenResponse = response.body();
                        String message = tokenResponse.getMessage();
                        if (tokenResponse != null) {
                            MySharedPreference.setPurchased(context, "STORE_LOGO",true);
                            Intent intent = new Intent(context, LandingScreen.class);
                            intent.putExtra("MENU_NAME", (Bundle) null);
                            startActivity(intent);
                            finish();
                        } else if (message.equals("Session expired")) {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), message);

                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                startActivity(new Intent(StoreImageActivity.this, LoginActivity.class));
                                finish();
                            }, 2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

}