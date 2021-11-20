package c.offerak.speedshopper.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.util.ArrayUtils;
import com.onesignal.OneSignal;
import com.xujiaji.library.RippleCheckBox;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.SlidingImageAdapter;
import c.offerak.speedshopper.modal.ShoppingListBean;
import c.offerak.speedshopper.modal.SpeedShoppingListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.AdvertisementListByMerchantIdResponse;
import c.offerak.speedshopper.response.AisleLocationResponse;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.GetShoppingListResponse;
import c.offerak.speedshopper.response.ListItemResponse;
import c.offerak.speedshopper.response.ShoppingListAddItemResponse;
import c.offerak.speedshopper.response.SpeedAvailableItemResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.ProgressHUD;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpeedShoppingActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    public static final String TAG = SpeedShoppingActivity.class.getSimpleName();
    public final int REQ_CODE_SPEECH_INPUT = 100;
    private final int ITEM_EDIT_INTEGER_VALUE = 34023;
    Context context;
    public RecyclerView lvList;
    public List<ShoppingListBean> storeListBeans = new ArrayList<>();
    public List<SpeedShoppingListBean> listBeans = new ArrayList<>();
    public SpeedShoppingListAdapter speedShoppingListAdapter;
    private StoreListAdapter storeAdapter;
    public Utils utils = new Utils();
    public ApiInterface apiService;
    public UserBean userBean;
    public String listId, storeId;
    public List<String> item_Ids = new ArrayList<>();
    public EditText edtUserName;
    public List<String> strings = new ArrayList<>();
    public List<ListItemResponse.DataBean> list;
    public List<String> aisleLocationList;
    public ArrayList<String> aisleList = new ArrayList<>();
    public ArrayList<String> aislePositionList = new ArrayList<>();
    public ImageView icBackButton, toggleButton, icMic, icAddCategory, cross, congratImg;
    public TextView txtTitle, btnDeleteChecked, btnDeleteAll, btnReverseSort, emptyView, storeListTitle, storeAddress, storeNameTitle, sstxEarned, totalPrice;
    public ImageView importItem, btnShare;
    public ArrayAdapter<String> adapter;
    Boolean orderFlag = false;
    Dialog dialog;
    ViewPager upcoming_pager;
    LinearLayout viewPagerCountDots;
    public int dotsCount;
    public ImageView[] dots;
    SlidingImageAdapter slidingImage_adapter;
    List<AdvertisementListByMerchantIdResponse.DataBean> advertisementList = new ArrayList<>();
    Dialog storeDialog;
    EditText cet_search;
    RecyclerView recyclerView;
    ImageView txtSync, store_dialog_cross;
    ProgressHUD progressHUD;
    private InterstitialAd mInterstitialAd;
    MySharedPreference mySharedPreference = new MySharedPreference(this);
    int unPurchasedCount = 0;
    public String shareToken;
    public boolean isUpgraded = false;
    public float total = 0.0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_shopping_list_layout);
        context = this;
        init();
    }

    public void init() {
        isUpgraded = MySharedPreference.getPurchased(context, "membership");
//        isUpgraded = true;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        lvList = findViewById(R.id.lvList);
        emptyView = findViewById(R.id.empty_view);
        storeListTitle = findViewById(R.id.storeListTitle);
        storeNameTitle = findViewById(R.id.storeNameTitle);
//        storeAddress = findViewById(R.id.addText);
//        storeLogo = findViewById(R.id.icStoreLogo);
        icAddCategory = findViewById(R.id.icAddCategory);
        edtUserName = findViewById(R.id.edtUserName);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteChecked = findViewById(R.id.btnDeleteChecked);
        btnReverseSort = findViewById(R.id.btnReverseSort);
        icBackButton = findViewById(R.id.backButton);
        toggleButton = findViewById(R.id.toggleButton);
        sstxEarned = findViewById(R.id.sstxEarendTextView);
        importItem = findViewById(R.id.importItem);
        icMic = findViewById(R.id.icMic);
        txtTitle = findViewById(R.id.txtTitle);
        txtSync = findViewById(R.id.txtSync);
        totalPrice = findViewById(R.id.total_price);
        btnShare = findViewById(R.id.share);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(this);
        userBean = mySharedPreference.getLoginDetails();

        txtTitle.setText(R.string.shopping_list_text);
        icBackButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);
        txtSync.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        listId = intent.getStringExtra("listId");

        storeListTitle.setText(intent.getStringExtra("listName"));
        storeNameTitle.setText(intent.getStringExtra("storeName"));
//        storeAddress.setText(intent.getStringExtra("storeAddress"));
        storeId = intent.getStringExtra("storeId");
        if (intent.hasExtra("storeId")) {
            storeId = intent.getStringExtra("storeId");
        }

//        Glide.with(context).load(intent.getStringExtra("storeLogo")).into(storeLogo);

        speedShoppingListAdapter = new SpeedShoppingListAdapter(this, listBeans);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        lvList.setLayoutManager(mLayoutManager);
        lvList.setItemAnimator(new DefaultItemAnimator());
        lvList.setAdapter(speedShoppingListAdapter);

        if (utils.isNetworkConnected(this)) {
            callAdvertisementListByStoreIdAPI();
            getStoreItemsList();
            getAllStores();
            getSSTXEarnedVal();
            getStoreItems();
        }

        icAddCategory.setOnClickListener(v -> {
            if (!edtUserName.getText().toString().equals("")) {
                if (utils.isNetworkConnected(context)) {
                    addItem(edtUserName.getText().toString());
                } else {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "You are not connected to internet!");
                }
                utils.hideKeyboard(SpeedShoppingActivity.this);
            } else {
                addCategoryItem();
            }
        });

        importItem.setOnClickListener(v -> {
            showStoreList();
        });

        btnShare.setOnClickListener(v -> {
            shareToken = getTokenString();
            saveShareToken();
        });

        btnDeleteAll.setOnClickListener(v -> AskOption("all"));
        btnReverseSort.setOnClickListener(v -> ReverseSort());
        btnDeleteChecked.setOnClickListener(v -> AskOption("checked"));
        icMic.setOnClickListener(v -> promptSpeechInput());
        icBackButton.setOnClickListener(v -> finish());

        txtSync.setOnClickListener(v -> {
            if (utils.isNetworkConnected(context)) {
                getStoreItems();
                getStoreItemsList();
            } else {
                utils.showSnackBar(getWindow().getDecorView().getRootView(), "You are not connected to internet!");
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));
        mInterstitialAd
                .loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("Google Admob", "Ads Loaded");
            }

            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        AdView adView = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        if (isUpgraded) {
            adView.setVisibility(View.GONE);
        }

        OneSignal.addTrigger("speedshop", "loaded");

    }

    private void saveShareToken() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(SpeedShoppingActivity.this);
            Call<GetResponse> call = apiService.saveShareToken(userBean.getUserToken(), shareToken, listId);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    try {
                        GetResponse tokenResponse = response.body();
                        assert tokenResponse != null;
                        if (tokenResponse.getStatus() == 200) {
                            shareShoppingList(shareToken);
                            utils.hideDialog();
                        } else {
                            utils.hideDialog();
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

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

    private void shareShoppingList(String token) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String sharebody = "I want to share my shopping list with you. \n\n " +
                "This way we'll never forget to buy anything we need, and our lists will always be in sync. \n\n " +
                "Accept my share request with Speed Shopper. If you're new, you will be sent to download the app first. Don't worry, it's free! \n\n " +
                "https://speedshopperapp.com/app/share/token/" + token + "\n\n" +
                "Try Speed Shopper, the world's best shopping list app, for iOS and Android.\n\n" +
                "https://apps.apple.com/us/app/speed-shopper/id1434065555 \n\n" +
                "https://play.google.com/store/apps/details?id=c.offerak.speedshopper";

        sendIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Shopping List");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "View My Shopping List With Speed Shopper");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "Share Shopping List");
        startActivity(shareIntent);
    }


    public String getTokenString() {
        String token = "";
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        token = formatter.format(date);
        token = listId + "_" + token;
        return token;
    }

    private void ReverseSort() {
        // Implement a reverse-order Comparator by lambda function
        Comparator<SpeedShoppingListBean> comp = (SpeedShoppingListBean a, SpeedShoppingListBean b) -> {
            int firstIndex = a.getIndex(), secondIndex = b.getIndex();
            if (a.getStatus().equals("1")) {
                return 1;
            }
            if (orderFlag) {
                return Integer.compare(secondIndex, firstIndex);
            } else {
                return Integer.compare(firstIndex, secondIndex);
            }
        };
        orderFlag = !orderFlag;
        Collections.sort(listBeans, comp);
        speedShoppingListAdapter.notifyDataSetChanged();


    }

    private void getAllStores() {
        if (utils.isNetworkConnected(context)) {
            Call<GetShoppingListResponse> call = apiService.getShoppingList(userBean.getUserToken());
            call.enqueue(new Callback<GetShoppingListResponse>() {
                @Override
                public void onResponse(Call<GetShoppingListResponse> call, retrofit2.Response<GetShoppingListResponse> response) {
                    try {
                        GetShoppingListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            List<GetShoppingListResponse.DataBean> data = tokenResponse.getData();
                            storeListBeans.clear();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    for (int i = 0; i < data.size(); i++) {
                                        if (!data.get(i).getId().equals(listId)) {
                                            ShoppingListBean listBean = new ShoppingListBean();
                                            listBean.setShopItem(data.get(i).getName());
                                            listBean.setStoreName(data.get(i).getStore_name());
                                            listBean.setShoppingListName(data.get(i).getName());
                                            listBean.setImageName(data.get(i).getImage());
                                            listBean.setAddress(data.get(i).getAddress());
                                            listBean.setId(data.get(i).getId());
                                            listBean.setStoreId(data.get(i).getStore_id());
                                            storeListBeans.add(listBean);
                                        }
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
                                } else {
                                    listBeans.clear();
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
                public void onFailure(Call<GetShoppingListResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + "server1111 error ");
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Server error!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void AskOption(String deleteStr) {
        String messageTitle = "Do you want to delete " + deleteStr + " items?";
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage(messageTitle)
                .setIcon(R.mipmap.delete_g)
                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    if (deleteStr.equals("all")) {
                        deleteAllItem();
                    } else {
                        deleteCheckedItem();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create();
        myQuittingDialogBox.show();
    }

    public void promptSpeechInput() {
        try {
            startActivityForResult(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    .putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt)), REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    public void addCategoryItem() {
        final CharSequence[] items = strings.toArray(new CharSequence[strings.size()]);
        try {
            if (strings.size() > 0) {
                if (!strings.get(0).equals("No item found")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Make your selection");
                    builder.setItems(items, (dialog, item) -> {
                        // Do something with the selection
                        addItem(strings.get(item));
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No Items available");
                    builder.setItems(items, (dialog, item) -> {
                        // Do something with the selection
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d("ArrayIndexOutOfBound", "addCategoryItem: ");
        }
    }

    public void getStoreItemsList() {
        if (utils.isNetworkConnected(context)) {
            Call<ListItemResponse> call = apiService.storeItems(userBean.getUserToken(), storeId);
            call.enqueue(new Callback<ListItemResponse>() {
                @Override
                public void onResponse(Call<ListItemResponse> call, retrofit2.Response<ListItemResponse> response) {
                    getItemAisleLocation();
                    ListItemResponse tokenResponse = response.body();
                    try {
                        if (tokenResponse != null) {
                            list = tokenResponse.getData();

                            if (tokenResponse.getMessage().equals("No item found")) {
                                strings.add(tokenResponse.getMessage());
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
                            } else {
                                if (list != null) {
                                    for (int i = 0; i < list.size(); i++) {
                                        strings.add(list.get(i).getName());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ListItemResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + "server1111 error ");
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Server error!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void getSSTXEarnedVal() {
        if (utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.getSSTXEarned(userBean.getUserToken(), storeId);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    try {
                        GetResponse myResponse = response.body();
                        sstxEarned.setText(myResponse.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetResponse> call, Throwable t) {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to reset password!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void getItemAisleLocation() {
        if (utils.isNetworkConnected(context)) {
            Call<AisleLocationResponse> call = apiService.getAisleLocations(userBean.getUserToken());
            call.enqueue(new Callback<AisleLocationResponse>() {
                @Override
                public void onResponse(Call<AisleLocationResponse> call, retrofit2.Response<AisleLocationResponse> response) {
                    try {
                        AisleLocationResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            aisleLocationList = tokenResponse.getData();

                            for (int i = 0; i < aisleLocationList.size(); i++) {
                                String[] s = aisleLocationList.get(i).split("__");
                                aisleList.add(s[0]);
                                aislePositionList.add(s[0]);

                            }
                        } else if (tokenResponse.getMessage().equals("Session expired")) {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                            }, 2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AisleLocationResponse> call, Throwable t) {
                    utils.hideDialog();
                    Log.e(TAG, "onFailure: " + "server1112 error ");

                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Server error!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void deleteAllItem() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(SpeedShoppingActivity.this);
            Call<GetResponse> call = apiService.removeAllItem(userBean.getUserToken(), listId);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    GetResponse tokenResponse = response.body();
                    try {
                        if (tokenResponse != null) {
                            listBeans.clear();
                            speedShoppingListAdapter.notifyDataSetChanged();

                        } else if (tokenResponse.getMessage().equals("Session expired")) {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                startActivity(new Intent(context, LoginActivity.class));
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

    public void deleteCheckedItem() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(SpeedShoppingActivity.this);
            Call<GetResponse> call = apiService.removeCheckedItem(userBean.getUserToken(), listId);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    GetResponse tokenResponse = response.body();
                    try {
                        if (tokenResponse != null) {
                            List<SpeedShoppingListBean> tempBeans = new ArrayList<>();

                            for (int i = 0; i < unPurchasedCount; i++) {
                                tempBeans.add(listBeans.get(i));
                            }
                            listBeans.clear();
                            listBeans.addAll(tempBeans);
                            speedShoppingListAdapter.notifyDataSetChanged();

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

    /**
     * Similar to compareTo method But compareTo doesn't return correct result for string+integer strings something like `A11` and `A9`
     */

    private int newCompareTo(String comp1, String comp2) {
        // If any value has 0 length it means other value is bigger
        if (comp1.length() == 0) {
            if (comp2.length() == 0) {
                return 0;
            }
            return -1;
        } else if (comp2.length() == 0) {
            return 1;
        }
        // Check if first string is digit
        if (TextUtils.isDigitsOnly(comp1)) {
            int val1 = Integer.parseInt(comp1);
            // Check if second string is digit
            if (TextUtils.isDigitsOnly(comp2)) { // If both strings are digits then we only need to use Integer compare method
                int val2 = Integer.parseInt(comp2);
                return Integer.compare(val1, val2);
            } else { // If only first string is digit we only need to use String compareTo method
                return comp1.compareTo(comp2);
            }

        } else { // If both strings are not digits

            int minVal = Math.min(comp1.length(), comp2.length()), sameCount = 0;

            // Loop through two strings and check how many strings are same
            for (int i = 0; i < minVal; i++) {
                char leftVal = comp1.charAt(i), rightVal = comp2.charAt(i);
                if (leftVal == rightVal) {
                    sameCount++;
                } else {
                    break;
                }
            }
            if (sameCount == 0) {
                // If there's no same letter, then use String compareTo method
                return comp1.compareTo(comp2);
            } else {
                // slice same string from both strings
                String newStr1 = comp1.substring(sameCount), newStr2 = comp2.substring(sameCount);
                if (newStr1.equals("")) {
                    if (newStr2.equals("")) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (newStr2.equals("")) {
                    return 1;
                }
                if (TextUtils.isDigitsOnly(newStr1) && TextUtils.isDigitsOnly(newStr2)) { // If both sliced strings are digits then use Integer compare method
                    return Integer.compare(Integer.parseInt(newStr1), Integer.parseInt(newStr2));
                } else { // If not, use String compareTo method
                    return comp1.compareTo(comp2);
                }
            }
        }
    }

    private void addItemToListBean(SpeedShoppingListBean bean, int purchaseStatus) {

        if (purchaseStatus == 1) {
            unPurchasedCount--;
        } else {
            unPurchasedCount++;
        }
        if (bean.getLocation() == null) {
            bean.setLocation("");
        }
        // If added item doesn't have aisle location then add it to top of list
        if (bean.getLocation().equals("")) {
            int index = purchaseStatus == 1 ? unPurchasedCount : 0;
            bean.setIndex(index);
            listBeans.add(index, bean);
        } else {  //If added item has aisle location
            boolean flag = false;
            if (purchaseStatus == 1) {
                // Find aisle location index and set index as it is
                int index = unPurchasedCount;
                // Avoid crash for Android 10 user
                try {
                    for (int i = unPurchasedCount; i < listBeans.size(); i++) {
                        SpeedShoppingListBean itemBean = listBeans.get(i);
                        if (newCompareTo(itemBean.getLocation(), bean.getLocation()) >= 0) {
                            bean.setIndex(i);
                            index = i;
                            flag = true;
                            break;
                        }
                    }
                } catch (Exception E) {
                    Log.e("Andorid 10 issue", E.toString());
                }
                // If doesn't find same aisle location then append it to the last
                if (flag) {
                    listBeans.add(index, bean);
                } else {
                    bean.setIndex(listBeans.size());
                    listBeans.add(listBeans.size(), bean);
                }
            } else {
                // Find aisle location index and set index as it is
                int index = unPurchasedCount;
                for (int i = 0; i < unPurchasedCount - 1; i++) {
                    SpeedShoppingListBean itemBean = listBeans.get(i);
                    if (newCompareTo(itemBean.getLocation(), bean.getLocation()) >= 0) {
                        bean.setIndex(i);
                        index = i;
                        flag = true;
                        break;
                    }
                }
                // If doesn't find same aisle location then append it to the last
                if (flag) {
                    listBeans.add(index, bean);
                } else {
                    bean.setIndex(unPurchasedCount - 1);
                    listBeans.add(unPurchasedCount - 1, bean);
                }
            }
        }
    }

    private void addItemAndReloadRecyclerView(ShoppingListAddItemResponse.DataBean dataBean) {
        SpeedShoppingListBean bean = new SpeedShoppingListBean();
        bean.setId(dataBean.getId());
        bean.setName(dataBean.getName());
        bean.setImage(dataBean.getImage());
        bean.setLocation(dataBean.getLocation());
        bean.setQuantity(dataBean.getQuantity());
        bean.setStatus(dataBean.getStatus());
        bean.setItem_id(dataBean.getItem_id());
        bean.setAdvertStatus("0");

        if (item_Ids.size() != 0) {
            for (int j = 0; j < item_Ids.size(); j++) {
                if (bean.getItem_id().equals(item_Ids.get(j))) {
                    bean.setAdvertStatus("1");
                }
            }
        }

        addItemToListBean(bean, 0);

        speedShoppingListAdapter.notifyDataSetChanged();

        btnReverseSort.setVisibility(View.VISIBLE);
        btnDeleteAll.setVisibility(View.VISIBLE);
        btnDeleteChecked.setVisibility(View.VISIBLE);
        lvList.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
    }

    public void addItem(String listName) {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(SpeedShoppingActivity.this);
            Call<ShoppingListAddItemResponse> call = apiService.addItemToShoppingList(userBean.getUserToken(), listId, storeId, listName);
            call.enqueue(new Callback<ShoppingListAddItemResponse>() {
                @Override
                public void onResponse(Call<ShoppingListAddItemResponse> call, retrofit2.Response<ShoppingListAddItemResponse> response) {

                    ShoppingListAddItemResponse tokenResponse = response.body();
                    utils.hideDialog();
                    try {
                        if (tokenResponse != null) {
                            getSSTXEarnedVal();
                            if (tokenResponse.getMessage().equals("Item added to your list")) {
                                edtUserName.setText("");

                                ShoppingListAddItemResponse.DataBean dataBean = tokenResponse.getData();
                                addItemAndReloadRecyclerView(dataBean);

                            } else if (tokenResponse.getMessage().equals("Session expired")) {
                                utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                final Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    startActivity(new Intent(context, LoginActivity.class));
                                    finish();
                                }, 2000);
                            } else {
                                utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ShoppingListAddItemResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    void reloadRecyclerViewWithData(SpeedAvailableItemResponse.DataBean dataBean) {
        total = 0.0f;
        listBeans.clear();

        List<SpeedAvailableItemResponse.DataBean.PurchasedBean> purchasedBeanList = dataBean.getPurchased();
        List<SpeedAvailableItemResponse.DataBean.UnpurchasedBean> unPurchasedBeanList = dataBean.getUnpurchased();
        unPurchasedCount = 0;

        if (unPurchasedBeanList != null) {
            unPurchasedCount = unPurchasedBeanList.size();
            for (int i = 0; i < unPurchasedBeanList.size(); i++) {
                SpeedShoppingListBean bean = new SpeedShoppingListBean();
                bean.setId(unPurchasedBeanList.get(i).getId());
                bean.setIndex(i);
                bean.setLocation(unPurchasedBeanList.get(i).getLocation());
                bean.setName(unPurchasedBeanList.get(i).getName());
                bean.setImage(unPurchasedBeanList.get(i).getImage());
                bean.setQuantity(unPurchasedBeanList.get(i).getQuantity());
                bean.setUnitPrice(unPurchasedBeanList.get(i).getUnit_price());
                bean.setStatus(unPurchasedBeanList.get(i).getStatus());
                bean.setItem_id(unPurchasedBeanList.get(i).getItem_id());
                bean.setAdvertStatus("0");
                float unPurchasedTotal = unPurchasedBeanList.get(i).getQuantity() * unPurchasedBeanList.get(i).getUnit_price();
                total += unPurchasedTotal;
                listBeans.add(bean);
            }
        }

        if (purchasedBeanList != null) {
            for (int i = 0; i < purchasedBeanList.size(); i++) {
                SpeedShoppingListBean bean = new SpeedShoppingListBean();
                bean.setId(purchasedBeanList.get(i).getId());
                bean.setIndex(i + unPurchasedCount);
                bean.setLocation(purchasedBeanList.get(i).getLocation());
                bean.setName(purchasedBeanList.get(i).getName());
                bean.setImage(purchasedBeanList.get(i).getImage());
                bean.setQuantity(purchasedBeanList.get(i).getQuantity());
                bean.setUnitPrice(purchasedBeanList.get(i).getUnit_price());
                bean.setStatus(purchasedBeanList.get(i).getStatus());
                bean.setItem_id(purchasedBeanList.get(i).getItem_id());
                bean.setAdvertStatus("0");
                float purchasedTotal = purchasedBeanList.get(i).getQuantity() * purchasedBeanList.get(i).getUnit_price();
                total += purchasedTotal;
                listBeans.add(bean);
            }
        }

        totalPrice.setText("Total Price : $ " + Float.toString(total));

        if (item_Ids.size() != 0 && listBeans.size() != 0) {
            for (int i = 0; i < listBeans.size(); i++) {
                for (int j = 0; j < item_Ids.size(); j++) {
                    if (listBeans.get(i).getItem_id().equals(item_Ids.get(j))) {
                        listBeans.get(i).setAdvertStatus("1");
                    }
                }
            }
        }

        speedShoppingListAdapter.notifyDataSetChanged();

        if (listBeans.size() == 0) {
            lvList.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            btnReverseSort.setVisibility(View.GONE);
            btnDeleteAll.setVisibility(View.GONE);
            btnDeleteChecked.setVisibility(View.GONE);
        } else {
            btnReverseSort.setVisibility(View.VISIBLE);
            btnDeleteAll.setVisibility(View.VISIBLE);
            btnDeleteChecked.setVisibility(View.VISIBLE);
            lvList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    public void getStoreItems() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(SpeedShoppingActivity.this);
            Call<SpeedAvailableItemResponse> call = apiService.shoppingListItem(userBean.getUserToken(), listId);
            call.enqueue(new Callback<SpeedAvailableItemResponse>() {
                @Override
                public void onResponse(Call<SpeedAvailableItemResponse> call, retrofit2.Response<SpeedAvailableItemResponse> response) {
                    utils.hideDialog();
                    try {
                        SpeedAvailableItemResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            SpeedAvailableItemResponse.DataBean dataBean = tokenResponse.getData();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    reloadRecyclerViewWithData(dataBean);
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        startActivity(new Intent(context, LoginActivity.class));
                                        finish();
                                    }, 2000);
                                } else if (tokenResponse.getStatus() == 400) {
                                    if (tokenResponse.getMessage().equals("No item in shopping list")) {
                                        listBeans.clear();
                                        speedShoppingListAdapter.notifyDataSetChanged();
                                    }
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
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
                public void onFailure(Call<SpeedAvailableItemResponse> call, Throwable t) {
                    utils.hideDialog();
                    Log.e(TAG, "onFailure: " + "server1113 error ");

                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Server error!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edtUserName.setText(result.get(0));
                }
                break;
            }
            case ITEM_EDIT_INTEGER_VALUE:
                if (resultCode == RESULT_OK && null != data) {
//                    int pos = data.getIntExtra("m_pos", 0);
//                    int itemQuantity = data.getIntExtra("ITEM_QUANTITY", 1);
//                    String aisleLocation = data.getStringExtra("ITEM_LOCATION");
//                    float itemPrice = data.getFloatExtra("ITEM_PRICE", 0.0f);
//
//                    SpeedShoppingListBean editBean = listBeans.get(pos);
//                    editBean.setLocation(aisleLocation);
//                    editBean.setQuantity(itemQuantity);
//                    editBean.setUnitPrice(itemPrice);
//                    listBeans.remove(pos);
//                    listBeans.add(pos, editBean);
//                    speedShoppingListAdapter.notifyDataSetChanged();
                    getStoreItems();
                }
                break;
        }
    }

    public void importItemFromAnotherStore(String itemId) {
        if (utils.isNetworkConnected(context)) {
            progressHUD = ProgressHUD.show(context, "Importing...", true, false, null);
            Call<SpeedAvailableItemResponse> call = apiService.importItem(userBean.getUserToken(), itemId, listId);
            call.enqueue(new Callback<SpeedAvailableItemResponse>() {
                @Override
                public void onResponse(Call<SpeedAvailableItemResponse> call, retrofit2.Response<SpeedAvailableItemResponse> response) {
                    progressHUD.dismiss();
                    if (storeDialog != null) {
                        storeDialog.dismiss();
                    }
                    getStoreItems();
                    getStoreItemsList();
                }

                @Override
                public void onFailure(Call<SpeedAvailableItemResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }


    public void purchase(String itemId, String purchaseStatus, int position) {
        if (utils.isNetworkConnected(context)) {
            Call<SpeedAvailableItemResponse> call = apiService.purchaseItem(userBean.getUserToken(), itemId, purchaseStatus, listId);
            call.enqueue(new Callback<SpeedAvailableItemResponse>() {
                @Override
                public void onResponse(Call<SpeedAvailableItemResponse> call, retrofit2.Response<SpeedAvailableItemResponse> response) {
                    try {
                        SpeedAvailableItemResponse tokenResponse = response.body();
                        try {
                            if (tokenResponse != null) {

                                if (tokenResponse.getMessage().equals("Session expired")) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        startActivity(new Intent(context, LoginActivity.class));
                                        finish();
                                    }, 2000);
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
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });

            // Move selected item to unselected list, unselected item to selected list
            new Handler().postDelayed(() -> {
                if (listBeans.size() > position) {
                    SpeedShoppingListBean changedBean = listBeans.get(position);
                    listBeans.remove(position);
                    if (purchaseStatus.equals("1")) {
                        changedBean.setStatus("1");
                        addItemToListBean(changedBean, 1);
                        if (unPurchasedCount == 0) {
                            showDoneDialog();
                        }
                    } else if (purchaseStatus.equals("0")) {
                        changedBean.setStatus("0");
                        addItemToListBean(changedBean, 0);
                    } else if (purchaseStatus.equals("9") && position < unPurchasedCount) {
                        unPurchasedCount--;
                    }
                }
                speedShoppingListAdapter.notifyDataSetChanged();
            }, 600);

        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void showStoreList() {
        if (storeListBeans.size() > 0) {
            storeDialog = new Dialog(context);
            storeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            storeDialog.setCancelable(false);
            storeDialog.setContentView(R.layout.dialog_store);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            Window window = storeDialog.getWindow();

            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            storeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
            cet_search = storeDialog.findViewById(R.id.cet_search);
            recyclerView = storeDialog.findViewById(R.id.recyclerView);
            store_dialog_cross = storeDialog.findViewById(R.id.crossStore);
            Log.e(TAG, "showAisleList: " + aisleList.toString());
            storeAdapter = new StoreListAdapter(context, storeListBeans);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(storeAdapter);

            cet_search.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (cet_search.getText().toString().isEmpty()) {
                        storeAdapter = new StoreListAdapter(context, storeListBeans);
                        recyclerView.setAdapter(storeAdapter);
                    } else {
                        storeAdapter.getFilter().filter(cet_search.getText().toString());
                    }
                    return true;
                }
                return false;
            });

            cet_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.e(TAG, "onTextChanged1: ");
                    if (s.toString().isEmpty()) {
                        storeAdapter = new StoreListAdapter(context, storeListBeans);
                        recyclerView.setAdapter(storeAdapter);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.e(TAG, "onTextChanged2: " + s);
                    if (s.toString().isEmpty()) {
                        storeAdapter = new StoreListAdapter(context, storeListBeans);
                        recyclerView.setAdapter(storeAdapter);
                    }
                    storeAdapter.getFilter().filter(cet_search.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.e(TAG, "onTextChanged3: ");
                    if (s.toString().isEmpty()) {
                        storeAdapter = new StoreListAdapter(context, storeListBeans);
                        recyclerView.setAdapter(storeAdapter);
                    }
                }
            });
            storeDialog.show();
            store_dialog_cross.setOnClickListener(v -> storeDialog.dismiss());

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Items available");
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void showEditItem(int pos) {
        SpeedShoppingListBean bean = listBeans.get(pos);

        Intent myIntent = new Intent(SpeedShoppingActivity.this, ItemEditActivity.class);
        myIntent.putExtra("m_id", bean.getId());
        myIntent.putExtra("item_id", bean.getItem_id());
        myIntent.putExtra("listId", listId);
        myIntent.putExtra("storeId", storeId);
        myIntent.putExtra("m_pos", pos);
        myIntent.putExtra("aisle_list", aisleList);
        myIntent.putExtra("item_name", bean.getName());
        myIntent.putExtra("item_image", bean.getImage());
        myIntent.putExtra("aisle_number", bean.getLocation());
        myIntent.putExtra("item_quantity", String.valueOf(bean.getQuantity()));
        myIntent.putExtra("item_price", String.valueOf(bean.getUnitPrice()));
        startActivityForResult(myIntent, ITEM_EDIT_INTEGER_VALUE);
    }

    public void showItemImageDialog(Drawable imgData) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_item_image_view);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);


        ImageView itemImage = dialog.findViewById(R.id.item_image);

        itemImage.setImageDrawable(imgData);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        ImageView imageCross = dialog.findViewById(R.id.cross_image);

        imageCross.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
    }

    //----------------- Advertisement List By Merchant Id ---------------------------
    public void callAdvertisementListByStoreIdAPI() {
//        utils.showDialog(context);
        if (utils.isNetworkConnected(context)) {
            Log.e(TAG, "TOKEN: " + userBean.getUserToken());
            Call<AdvertisementListByMerchantIdResponse> call = apiService.getAdvertisementListByStoreIdAPI(userBean.getUserToken(), storeId);
            call.enqueue(new Callback<AdvertisementListByMerchantIdResponse>() {
                @Override
                public void onResponse(Call<AdvertisementListByMerchantIdResponse> call, Response<AdvertisementListByMerchantIdResponse> response) {
                    try {
                        AdvertisementListByMerchantIdResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {

                                    advertisementList.clear();
                                    if (response.body() == null) {
                                        return;
                                    }

                                    advertisementList = response.body().getData();
                                    for (int i = 0; i < advertisementList.size(); i++) {
                                        if (advertisementList.get(i).getItem_id() != null) {
                                            Log.e(TAG, "item_Id: " + advertisementList.get(i).getItem_id());
                                            item_Ids.add(advertisementList.get(i).getItem_id());
                                        }
                                    }

                                    if (listBeans.size() != 0 && item_Ids.size() != 0) {
                                        for (int i = 0; i < listBeans.size(); i++) {
                                            for (int j = 0; j < item_Ids.size(); j++) {
                                                if (listBeans.get(i).getItem_id().equals(item_Ids.get(j))) {
                                                    listBeans.get(i).setAdvertStatus("1");
                                                }
                                            }
                                        }
                                    }

                                    if (speedShoppingListAdapter != null) {
                                        speedShoppingListAdapter.notifyDataSetChanged();
                                    }


                                } else if (response.body().getStatus() == 401) {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        }
                                    }, 2000);
                                } else {
                                    utils.showSnackBar(getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
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
                public void onFailure(Call<AdvertisementListByMerchantIdResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    //----------------------- Advertisement Dialog -------------------------------
    public void showDialogAdvertisement() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_upcoming);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        upcoming_pager = dialog.findViewById(R.id.upcoming_pager);
        viewPagerCountDots = dialog.findViewById(R.id.viewPagerCountDots);
        cross = dialog.findViewById(R.id.crossUpcoming);

        upcoming_pager.setOnPageChangeListener(this);
        cross.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //----------------------- Advertisement Dialog -------------------------------
    public void showDoneDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_all_item_select);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        cross = dialog.findViewById(R.id.cross);
        congratImg = dialog.findViewById(R.id.congrat_image);
        Glide.with(this).load(R.drawable.checked).into(congratImg);

        cross.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

        if (mInterstitialAd.isLoaded() && !isUpgraded) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e(TAG, "onPageSelected: " + position);
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.z_nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.z_selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setUiPageViewController() {
        dotsCount = slidingImage_adapter.getCount();
        Log.e(TAG, "dotsCount: " + String.valueOf(dotsCount));
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.z_nonselecteditem_dot));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 0, 4, 0);
            viewPagerCountDots.addView(dots[i], layoutParams);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.z_selecteditem_dot));
    }

    class SpeedShoppingListAdapter extends RecyclerView.Adapter<SpeedShoppingListAdapter.MyViewHolder> implements View.OnClickListener {

        public Context context;
        public List<SpeedShoppingListBean> shoppingListBeans;

        SpeedShoppingListAdapter(Context context, List<SpeedShoppingListBean> shoppingListBeans) {
            this.context = context;
            this.shoppingListBeans = shoppingListBeans;

            if (shoppingListBeans.size() == 0) {
                lvList.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                btnDeleteAll.setVisibility(View.GONE);
                btnDeleteChecked.setVisibility(View.GONE);
            } else {
                btnDeleteAll.setVisibility(View.VISIBLE);
                btnDeleteChecked.setVisibility(View.VISIBLE);
                lvList.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView shopItem, lineView, aisleLocationText, quantityText;
            ImageView icDelete, icEdit, icItemImage;
            RippleCheckBox rippleCheckBox;

            MyViewHolder(View view) {
                super(view);
                shopItem = view.findViewById(R.id.listName);
                lineView = view.findViewById(R.id.lineView);
                aisleLocationText = view.findViewById(R.id.aisleLocationText);
                quantityText = view.findViewById(R.id.quantityText);
                icDelete = view.findViewById(R.id.icDelete);
                icEdit = view.findViewById(R.id.icEdit);
                icItemImage = view.findViewById(R.id.itemImage);
                rippleCheckBox = view.findViewById(R.id.ic_checkbox);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.speed_shopping_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SpeedShoppingListAdapter.MyViewHolder holder, int position) {

            if (shoppingListBeans.size() == 0) {
                lvList.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                btnDeleteAll.setVisibility(View.GONE);
                btnDeleteChecked.setVisibility(View.GONE);
            } else {
                btnDeleteAll.setVisibility(View.VISIBLE);
                btnDeleteChecked.setVisibility(View.VISIBLE);
                lvList.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }

            SpeedShoppingListBean bean = shoppingListBeans.get(position);
            if (bean.getName() != null)
                holder.shopItem.setText(bean.getName());

            Log.e(TAG, "onBindViewHolder: " + bean.getAdvertStatus());
            if (bean.getAdvertStatus().equals("1")) {
                holder.shopItem.setTextColor(getResources().getColor(R.color.colorAppBlue));
                holder.shopItem.setPaintFlags(holder.shopItem.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                holder.shopItem.setTextColor(getResources().getColor(android.R.color.black));
                holder.shopItem.setPaintFlags(0);
            }


            if (bean.getLocation() != null && !bean.getLocation().equals("")) {
                holder.aisleLocationText.setText(bean.getLocation().toUpperCase());
                holder.aisleLocationText.setSingleLine(false);
                holder.aisleLocationText.setMaxLines(10);
            } else {
                holder.aisleLocationText.setText("Add aisle");
            }

            if (bean.getStatus() != null && bean.getStatus().equals("0")) {
                holder.rippleCheckBox.setChecked(false);
            } else {
                holder.rippleCheckBox.setChecked(true);
            }

            if (isUpgraded && !bean.getImage().equals("")) {
                Glide.with(context)
                        .load(Constants.ITEM_IMAGE_URL + bean.getImage())
                        .into(holder.icItemImage);
                holder.icItemImage.setVisibility(View.VISIBLE);
            } else {
                holder.icItemImage.setVisibility(View.GONE);
            }

            holder.quantityText.setText(String.valueOf(bean.getQuantity()));
            holder.icEdit.setOnClickListener(new onclick(position, "editItem"));
            holder.rippleCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
                String purchaseStatus = isChecked ? "1" : "0";
                String id = listBeans.get(position).getId();
                int counter = mySharedPreference.increaseAndGetShowAdsCounter();
                if (counter == 0) {
                    if (mInterstitialAd.isLoaded() && !isUpgraded) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }
//                remove(position);
                purchase(id, purchaseStatus, position);
            });

            holder.icDelete.setOnClickListener(new onclick(position, "del"));
            holder.icItemImage.setOnClickListener(v -> {
                showItemImageDialog(holder.icItemImage.getDrawable());

            });
            holder.shopItem.setOnClickListener(new onclick(position, "shopItem"));
        }

        @Override
        public int getItemCount() {
            return shoppingListBeans.size();
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class onclick implements View.OnClickListener {
        int pos;
        String from;

        public onclick(int position, String from) {
            this.pos = position;
            this.from = from;
        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick: " + listBeans.get(pos).getId());

            //   View view1 = lvList.getChildAt(pos);

            String id = listBeans.get(pos).getId();
            String itemId = listBeans.get(pos).getItem_id();

            switch (from) {
                case "del":
                    purchase(id, "9", pos);
                    break;
                case "editItem":
                    showEditItem(pos);
                    break;
                case "shopItem":
                    Log.e(TAG, "onClick: " + "shopItem");
                    List<AdvertisementListByMerchantIdResponse.DataBean> adds = new ArrayList<>();
                    Log.e(TAG, "onClick: " + adds.size());

                    if (listBeans.size() != 0 && advertisementList.size() != 0) {

                        for (int j = 0; j < advertisementList.size(); j++) {
                            if (listBeans.get(pos).getItem_id().equals(advertisementList.get(j).getItem_id())) {
                                AdvertisementListByMerchantIdResponse.DataBean bean = new AdvertisementListByMerchantIdResponse.DataBean();
                                bean.setImage(advertisementList.get(j).getImage());
                                bean.setAdv_link(advertisementList.get(j).getAdv_link());
                                bean.setAdv_id(advertisementList.get(j).getAdv_id());
                                bean.setName(advertisementList.get(j).getName());
                                adds.add(bean);
                            }
                        }
                    }

                    Log.e(TAG, "onClick: " + adds.size());

                    if (adds.size() != 0) {
                        Log.e(TAG, "onClick: " + "shopItem123");

                        showDialogAdvertisement();
                        slidingImage_adapter = new SlidingImageAdapter(context, apiService, userBean.getUserToken(), adds);
                        upcoming_pager.setAdapter(slidingImage_adapter);
                        setUiPageViewController();
                    }
                    //  setUiPageViewController(id);

                    break;

            }
        }
    }

    public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.MyViewHolder> implements Filterable {

        Context context;
        List<ShoppingListBean> storeList = new ArrayList<>();
        List<ShoppingListBean> storeFilterList = new ArrayList<>();
        String pos;

        StoreListAdapter(Context context, List<ShoppingListBean> storeList) {
            this.context = context;
            this.storeList = storeList;
            this.pos = pos;
            storeFilterList = storeList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView ctv_title_item, storeNameTextView;
            LinearLayout wholeView;
            String importId;

            MyViewHolder(View itemView) {
                super(itemView);
                ctv_title_item = itemView.findViewById(R.id.title_item);
                wholeView = itemView.findViewById(R.id.whole_view);
                storeNameTextView = itemView.findViewById(R.id.store_name);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_location, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder.ctv_title_item.setText(storeFilterList.get(position).getShoppingListName());
            holder.storeNameTextView.setText(storeFilterList.get(position).getStoreName());
            holder.importId = storeFilterList.get(position).getId();
            holder.wholeView.setOnClickListener(v -> ((SpeedShoppingActivity) context).importItemFromAnotherStore(holder.importId));
        }

        @Override
        public int getItemCount() {
            return storeFilterList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        storeFilterList = storeList;
                    } else {
                        ArrayList<ShoppingListBean> filteredList = new ArrayList<>();
                        for (ShoppingListBean row : storeList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getStoreName().toLowerCase().contains(charString.toLowerCase()) || row.getStoreName().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        }

                        storeFilterList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = storeFilterList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    storeFilterList = (ArrayList<ShoppingListBean>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
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
        OneSignal.addTrigger("speedshop", "loaded");
    }


}
