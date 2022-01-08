package c.offerak.speedshopper.fragment;

import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LandingScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.SpeedShoppingActivity;
import c.offerak.speedshopper.activity.StoreImageActivity;
import c.offerak.speedshopper.activity.StoreProduct;
import c.offerak.speedshopper.adapter.EZListsAdapter;
import c.offerak.speedshopper.modal.ShoppingListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.EZListsResponse;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.GetShoppingListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.RecyclerItemClickListener;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class EZListsFragment extends Fragment {

    public static final String TAG = GiftCardsFragment.class.getSimpleName();
    Context context;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private RelativeLayout layout;
    private EditText searchEdit;
    private UserBean userBean;
    private ArrayList<EZListsResponse.DataBean> listBeans = new ArrayList<>();
    List<ShoppingListBean> storeListBeans = new ArrayList<>();
    private EZListsAdapter adapter;
    private RecyclerView homeList;
    private TextView noDataFound;
    private LinearLayoutManager mLayoutManager;
    View rootView;
    Dialog storeDialog;
    ShoppingListAdapter storeAdapter;
    RelativeLayout constraintLayout;
    ListView recyclerView;
    private  EZListsResponse.DataBean ezListBean;
    private ShoppingListBean myListBean;


    public EZListsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ez_lists, container, false);
        context = getActivity();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();
        initializeBilling();

        return rootView;
    }

    public void init() {

        LandingScreen.txtTitle.setText(R.string.ez_lists);
        LandingScreen.history.setVisibility(View.GONE);
        LandingScreen.txtSync.setVisibility(View.VISIBLE);

        LandingScreen.txtSync.setOnClickListener(v -> {
            if (utils.isNetworkConnected(getActivity())) {
                listSetup("0");;
            } else {
                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
            }
        });

        homeList = rootView.findViewById(R.id.lvList);
        noDataFound = rootView.findViewById(R.id.noDataFound);
        searchEdit = rootView.findViewById(R.id.searchEdit);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();
        homeList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), homeList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String storeId = listBeans.get(position).getId();
                        String storeName = listBeans.get(position).getTitle();
                        String storeAddress = listBeans.get(position).getDescription();
                        Log.e(TAG, "onItemClick: " + storeName);

                        ezListBean = listBeans.get(position);

                        if (storeListBeans.isEmpty()) {
                            if (utils.isNetworkConnected(getActivity())) {
                            listSetupShoppingListsFragment();
                        } else {
                            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
                        }
                        } else {
                            showStoreList();
                        }
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

        OneSignal.addTrigger("searchMarket", "loaded");
    }

    private void listSetup(String page) {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Call<EZListsResponse> call = apiService.getEZLists(userBean.getUserToken(), page, searchEdit.getText().toString());
            call.enqueue(new Callback<EZListsResponse>() {
                @Override
                public void onResponse(Call<EZListsResponse> call, retrofit2.Response<EZListsResponse> response) {
                    try {
                        EZListsResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    listBeans.clear();
                                    listBeans = (ArrayList<EZListsResponse.DataBean>) tokenResponse.getData();

                                    if(listBeans.isEmpty()){
                                        homeList.setVisibility(View.GONE);
                                        noDataFound.setVisibility(View.VISIBLE);
                                    }else {
                                        homeList.setVisibility(View.VISIBLE);
                                        noDataFound.setVisibility(View.GONE);
                                    }
                                    adapter = new EZListsAdapter(getActivity(), listBeans);
                                    mLayoutManager = new LinearLayoutManager(getActivity());
                                    homeList.setAdapter(adapter);
                                    homeList.setLayoutManager(mLayoutManager);

                                    Log.e("!!!!!!!!!!", "@@@@@@@@@@");
                                    listSetupShoppingListsFragment();

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
//                    utils.hideDialog();
                }

                @Override
                public void onFailure(Call<EZListsResponse> call, Throwable t) {
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

    private void listSetupShoppingListsFragment() {
        if (utils.isNetworkConnected(context)) {
//            utils.showDialog(getActivity());
            Call<GetShoppingListResponse> call = apiService.getShoppingList(userBean.getUserToken());
            call.enqueue(new Callback<GetShoppingListResponse>() {
                @Override
                public void onResponse(Call<GetShoppingListResponse> call, retrofit2.Response<GetShoppingListResponse> response) {
                    utils.hideDialog();
                    try {
                        GetShoppingListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            List<GetShoppingListResponse.DataBean> data = tokenResponse.getData();
                            storeListBeans.clear();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    for (int i = 0; i < data.size(); i++) {
                                        ShoppingListBean listBean = new ShoppingListBean();
                                        listBean.setShopItem(data.get(i).getName());
                                        listBean.setStoreName(data.get(i).getStore_name());
                                        listBean.setAddress(data.get(i).getAddress());
                                        listBean.setItemCount(data.get(i).getItemCount());
                                        listBean.setId(data.get(i).getId());
                                        listBean.setStoreId(data.get(i).getStore_id());
                                        listBean.setImageName(data.get(i).getStoreImage());
                                        storeListBeans.add(listBean);
                                    }
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
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
                                    storeListBeans.clear();
//                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                    //adapter.notifyDataSetChanged();
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
                    utils.hideDialog();
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void addItemToMyLists() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Call<GetResponse> call = apiService.addItemToMyLists(userBean.getUserToken(), ezListBean.getId(), myListBean.getId());
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    try {
                        GetResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            try {
                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                if (tokenResponse.getStatus() == 200) {
                                    storeDialog.dismiss();
                                    String storeId = myListBean.getStoreId();
                                    String listName = myListBean.getShopItem();
                                    String storeName = myListBean.getStoreName();
                                    String listID = myListBean.getId();
                                    String storeAddress = myListBean.getAddress();

                                    startActivity(new Intent(getActivity(), SpeedShoppingActivity.class)
                                            .putExtra("listName", listName)
                                            .putExtra("storeName", storeName)
                                            .putExtra("storeAddress", storeAddress)
                                            .putExtra("storeId", storeId)
                                            .putExtra("listId", listID));

                                } else if (tokenResponse.getMessage().equals("Session expired")) {
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
                }

                @Override
                public void onFailure(Call<GetResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        } else {
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

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("searchMarket", "loaded");
    }

    private void initializeBilling() {

    }

    public void showStoreList() {
        if (storeListBeans.size() > 0) {
            storeDialog = new Dialog(context);
            storeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            storeDialog.setCancelable(true);
            storeDialog.setContentView(R.layout.dialog_select_list);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            Window window = storeDialog.getWindow();

            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            storeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));

            recyclerView = storeDialog.findViewById(R.id.shoppingListRecycler);
            storeAdapter = new ShoppingListAdapter(context, storeListBeans);
            recyclerView.setAdapter(storeAdapter);

            constraintLayout = storeDialog.findViewById(R.id.constraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeDialog.dismiss();
                }
            });

            storeDialog.show();


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("No Items available");
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void AskOption() {
        String messageTitle = "Do you want to add these items to your list?";
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                .setMessage(messageTitle)
                .setPositiveButton("Yes", (dialog, whichButton) -> {
                    addItemToMyLists();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();
        myQuittingDialogBox.show();
    }

    private class ShoppingListAdapter extends BaseAdapter {

        TextView shopItem, lineView, storeName, storeAddress, counterTextView;
        RelativeLayout counterLayout;
        RelativeLayout cardviewContainer;
        LinearLayout store_info_view;
        CardView cardView;
        ImageView arrow;
        private Context context;
        private List<ShoppingListBean> shoppingListBeans;
        private MySharedPreference mySharedPreference;

//        Boolean purchased = false;

        ShoppingListAdapter(Context context, List<ShoppingListBean> shoppingListBeans) {
            this.context = context;
            this.shoppingListBeans = shoppingListBeans;
        }

        @Override
        public int getCount() {
            return shoppingListBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return shoppingListBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            apiService = ApiClient.getClient().create(ApiInterface.class);
            mySharedPreference = new MySharedPreference(context);
            userBean = mySharedPreference.getLoginDetails();

            convertView = from(parent.getContext()).inflate(R.layout.select_list_item, parent, false);
            cardviewContainer = convertView.findViewById(R.id.parentCardView);
            store_info_view = convertView.findViewById(R.id.store_info);
            shopItem = convertView.findViewById(R.id.listName);
            storeName = convertView.findViewById(R.id.storeName);
            storeAddress = convertView.findViewById(R.id.storeAddress);
            counterLayout = convertView.findViewById(R.id.counter_layout);
            lineView = convertView.findViewById(R.id.lineView);
            counterTextView = convertView.findViewById(R.id.counter_text_view);
//            cardView = convertView.findViewById(R.id.cardView);
            arrow = convertView.findViewById(R.id.arrow);
            ShoppingListBean bean = shoppingListBeans.get(position);
            shopItem.setText(bean.getShopItem().substring(0, 1).toUpperCase() + bean.getShopItem().substring(1));
            storeName.setText(bean.getStoreName());
            String imageName = bean.getImageName();

            if (imageName == null || imageName.equals("logo_0")) {
                imageName = "logo_0.png";
            }
            Glide.with(context)
                    .load(Constants.IMAGE_URL + imageName)
                    .into(arrow);

//            String imageResource = "@drawable/" + imageName;
//            int imageId = getResources().getIdentifier(imageResource, "drawable", getActivity().getPackageName());
//            arrow.setImageResource(imageId);

            String itemCountStr = bean.getItemCount();
            Log.e(TAG, "getView: " + bean.getAddress());
            storeAddress.setText("Address: " + bean.getAddress());



            if (itemCountStr.equals("0") || itemCountStr.equals("")) {
                counterLayout.setVisibility(View.GONE);
            } else {
                int itemCount = Integer.parseInt(itemCountStr);
                if (itemCount >= 999) {
                    itemCountStr = "99+";
                }
                counterTextView.setText(itemCountStr);
            }
            if (position < 3) {
                if (position == 0) {
                    viewUpdate(1);
                } else if(position == 1) {
                    viewUpdate(2);
                } else {
                    viewUpdate(3);
                }
            } else {
                if (position % 3 == 0) {
                    cardviewContainer.setBackgroundResource(R.drawable.glossybox1);
                } else if (position % 3 == 1){
                    cardviewContainer.setBackgroundResource(R.drawable.glossybox2blue);
                } else {
                    cardviewContainer.setBackgroundResource(R.drawable.glossybox3yellow);
                }
            }

            store_info_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String storeId = bean.getStoreId();
                    String listName = bean.getShopItem();
                    String storeName = bean.getStoreName();
                    String listID = bean.getId();
                    String storeAddress = bean.getAddress();

                    myListBean = bean;
                    AskOption();

//                    startActivity(new Intent(getActivity(), SpeedShoppingActivity.class)
//                            .putExtra("listName", listName)
//                            .putExtra("storeName", storeName)
//                            .putExtra("storeAddress", storeAddress)
//                            .putExtra("storeId", storeId)
//                            .putExtra("listId", listID));
                }
            });

            return convertView;
        }

        public void viewUpdate (int i) {
            if (i == 1) {
                cardviewContainer.setBackgroundResource(R.drawable.glossybox1);
            } else if (i == 2) {
                cardviewContainer.setBackgroundResource(R.drawable.glossybox2blue);
            } else {
                cardviewContainer.setBackgroundResource(R.drawable.glossybox3yellow);
            }
        }
    }
}