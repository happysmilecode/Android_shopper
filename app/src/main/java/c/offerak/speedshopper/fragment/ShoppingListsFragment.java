package c.offerak.speedshopper.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.HomeScreen;
import c.offerak.speedshopper.activity.LandingScreen;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.SpeedShoppingActivity;
import c.offerak.speedshopper.activity.StoreImageActivity;
import c.offerak.speedshopper.modal.PurchaseModel;
import c.offerak.speedshopper.modal.ShoppingListBean;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.GetShoppingListResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import pl.droidsonroids.gif.GifTextView;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static android.view.LayoutInflater.from;

public class ShoppingListsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,  BillingProcessor.IBillingHandler {

    private static final String TAG = ShoppingListsFragment.class.getSimpleName();
    List<ShoppingListBean> listBeans = new ArrayList<>();
    int PLACE_PICKER_REQUEST = 1;
    private ShoppingListAdapter adapter;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    private RelativeLayout tapHeader;
    private LinearLayout lay_ctv_note;
    private ListView homeList;
    private String storeName, storeAddress, storeLogo, storeId, listName;
    ImageView icAdd, icBackButton, toggleButton;
    View rootView;
    Context context;
    TextView fadingTextView;
    GifTextView right_hand;
    int i = 0;

    BillingProcessor bp;
    IOSDialog iosDialog;
    private PurchaseModel mViewModel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        context = getActivity();
        mViewModel = ViewModelProviders.of((FragmentActivity) context).get(PurchaseModel.class);
        init();
        initializeBilling();
//        initializePurchase();
        return rootView;
    }

    public void initializeBilling(){
        // intialize the billing process
//        iosDialog = new IOSDialog.Builder(context)
//                .setCancelable(false)
//                .setSpinnerClockwise(false)
//                .setMessageContentGravity(Gravity.END)
//                .build();
//        iosDialog.show();

        bp = new BillingProcessor(context, mViewModel.getGooglePlayConsolLicenseKey(), this);
        bp.initialize();
    }

    @SuppressLint("MissingPermission")
    public void init() {

        Log.e(TAG, "Firebase Token: " + MySharedPreference.getSharedPreferences(context, Constants.DEVICE_ID));
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        icAdd = rootView.findViewById(R.id.icAdd);
        homeList = rootView.findViewById(R.id.shoppingListRecycler);
        icBackButton = rootView.findViewById(R.id.backButton);
        toggleButton = rootView.findViewById(R.id.toggleButton);
        tapHeader = rootView.findViewById(R.id.tapHeader);
        fadingTextView = rootView.findViewById(R.id.fadingTextView);
        right_hand = rootView.findViewById(R.id.right_hand);
        tapHeader.setVisibility(View.GONE);
        fadingTextView.setVisibility(View.VISIBLE);
        right_hand.setVisibility(View.GONE);

        AdView adView = rootView.findViewById(R.id.ads_view1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        LandingScreen.txtTitle.setText(R.string.my_list);
        LandingScreen.history.setVisibility(View.GONE);
        LandingScreen.txtSync.setVisibility(View.VISIBLE);

        LandingScreen.txtSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isNetworkConnected(getActivity())) {
                    listSetupShoppingListsFragment();
                } else {
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
                }
            }
        });

        String[] texts = {getString(R.string.tap_the_plus_button_to_create_your_list),
                getString(R.string.tap_the_plus_button_to_create_your_list),
                getString(R.string.tap_the_plus_button_to_create_your_list)};

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        icAdd.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), HomeScreen.class), 3000);
        });

        if (utils.isNetworkConnected(getActivity())) {
            listSetupShoppingListsFragment();
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //  For stop loading loader again
        if (i != 0) {
            listSetupShoppingListsFragment();
        } else {
            i = 1;
        }
    }

    private void listSetupShoppingListsFragment() {
        if (utils.isNetworkConnected(context)) {
            utils.showDialog(getActivity());
            Call<GetShoppingListResponse> call = apiService.getShoppingList(userBean.getUserToken());
            call.enqueue(new Callback<GetShoppingListResponse>() {
                @Override
                public void onResponse(Call<GetShoppingListResponse> call, retrofit2.Response<GetShoppingListResponse> response) {
                    utils.hideDialog();
                    try {
                        GetShoppingListResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            List<GetShoppingListResponse.DataBean> data = tokenResponse.getData();
                            listBeans.clear();
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
                                        listBeans.add(listBean);
                                    }
                                    adapter = new ShoppingListAdapter(getActivity(), listBeans);
                                    homeList.setAdapter(adapter);
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
                                    listBeans.clear();
//                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
                                    //adapter.notifyDataSetChanged();
                                }

                                if (listBeans.size() > 0) {
                                    tapHeader.setVisibility(View.VISIBLE);
                                    fadingTextView.setVisibility(View.GONE);
                                    right_hand.setVisibility(View.GONE);
                                } else {
                                    tapHeader.setVisibility(View.GONE);
                                    fadingTextView.setVisibility(View.VISIBLE);
                                    right_hand.setVisibility(View.GONE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        utils.hideDialog();
    }

    private void deleteListItem(String listid) {
        if (utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.removeShoppingList(userBean.getUserToken(), listid);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {

                    GetResponse tokenResponse = response.body();
                    String message = tokenResponse.getMessage();
                    try {
                        if (tokenResponse != null) {
                            listSetupShoppingListsFragment();
                        } else if (message.equals("Session expired")) {
                            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), message);

                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }, 2000);
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

    private void AskOption(String position) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.mipmap.delete_g)
                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    deleteListItem(position);
                    dialog.dismiss();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create();

        myQuittingDialogBox.show();
    }

    private void openPurchaseDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.purchase_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_yes.setOnClickListener(v -> {
            boolean isAvailable = BillingProcessor.isIabServiceAvailable(context);
            if(isAvailable)
            /**
             * IMPORTANT: when you provide a payload, internally the library prepends a string to your payload.
             * For subscriptions, it prepends "subs:\<productId\>:", and for products, it prepends "inapp:\<productId\>:\<UUID\>:".
             * This is important to know if you do any validation on the payload returned from Google Play after a successful purchase.
             * */
                bp.purchase(getActivity(), mViewModel.getPRODUCT_SKU() );
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openDialog(String id, int pos) {

        Log.e("Shopping ListID:", id);

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.list_add_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        EditText inputE = dialog.findViewById(R.id.edtUserName);
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView btnDont = dialog.findViewById(R.id.btnDone);
        ImageView imgCross = dialog.findViewById(R.id.iconCross);

        dialogTitle.setText(R.string.update_list_name);
        inputE.setText(listBeans.get(pos).getShopItem());
        btnDont.setText(R.string.update);

        btnDont.setOnClickListener(v -> {
            String listName = inputE.getText().toString();
            if (!listName.equals("")) {
                dialog.dismiss();
                utils.hideKeyboard(getActivity());
                updateList(id, listName);
            } else {
                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "Please enter the address!");
            }
        });
        imgCross.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateList(String id, String listName) {
        if (utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.updateShoppingListName(userBean.getUserToken(), id, listName);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {

                    try {
                        GetResponse tokenResponse = response.body();
                        String message = tokenResponse.getMessage();
                        if (tokenResponse != null) {
                            listSetupShoppingListsFragment();
                        } else if (message.equals("Session expired")) {
                            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), message);

                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }, 2000);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 3000 && resultCode == RESULT_OK) {
            storeId = data.getStringExtra("storeId");
            storeLogo = data.getStringExtra("storeLogo");
            storeAddress = data.getStringExtra("storeAddress");
            storeName = data.getStringExtra("storeName");
            listName = data.getStringExtra("listName");
        } else if (requestCode == 3000 && resultCode == 2000) {

        }

        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class ShoppingListAdapter extends BaseAdapter {

        TextView shopItem, lineView, storeName, storeAddress, counterTextView;
        RelativeLayout counterLayout;
        RelativeLayout cardviewContainer;
        LinearLayout store_info_view;
        CardView cardView;
        ImageView icDelete, icEdit, arrow;
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

            LandingScreen.txtSync.setVisibility(View.VISIBLE);

            convertView = from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
            cardviewContainer = convertView.findViewById(R.id.parentCardView);
            store_info_view = convertView.findViewById(R.id.store_info);
            shopItem = convertView.findViewById(R.id.listName);
            storeName = convertView.findViewById(R.id.storeName);
            storeAddress = convertView.findViewById(R.id.storeAddress);
            counterLayout = convertView.findViewById(R.id.counter_layout);
            lineView = convertView.findViewById(R.id.lineView);
            counterTextView = convertView.findViewById(R.id.counter_text_view);
//            cardView = convertView.findViewById(R.id.cardView);
            icDelete = convertView.findViewById(R.id.icDelete);
            icEdit = convertView.findViewById(R.id.icEdit);
            arrow = convertView.findViewById(R.id.arrow);
            ShoppingListBean bean = shoppingListBeans.get(position);
            shopItem.setText(bean.getShopItem().substring(0, 1).toUpperCase() + bean.getShopItem().substring(1));
            storeName.setText(bean.getStoreName());
            String imageName = bean.getImageName();

            if (imageName == null) {
                imageName = "logo_0";
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

            icDelete.setOnClickListener(new onclick(position));
            icEdit.setOnClickListener(new onclick(position));
            store_info_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String storeId = bean.getStoreId();
                    String listName = bean.getShopItem();
                    String storeName = bean.getStoreName();
                    String listID = bean.getId();
                    String storeAddress = bean.getAddress();

                    startActivity(new Intent(getActivity(), SpeedShoppingActivity.class)
                            .putExtra("listName", listName)
                            .putExtra("storeName", storeName)
                            .putExtra("storeAddress", storeAddress)
                            .putExtra("storeId", storeId)
                            .putExtra("listId", listID));
                }
            });
            counterLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String storeId = bean.getStoreId();
                    String listName = bean.getShopItem();
                    String storeName = bean.getStoreName();
                    String listID = bean.getId();
                    String storeAddress = bean.getAddress();

                    startActivity(new Intent(getActivity(), SpeedShoppingActivity.class)
                            .putExtra("listName", listName)
                            .putExtra("storeName", storeName)
                            .putExtra("storeAddress", storeAddress)
                            .putExtra("storeId", storeId)
                            .putExtra("listId", listID));
                }
            });

            arrow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    String storeId = bean.getStoreId();
//                    String listName = bean.getShopItem();
//                    String storeName = bean.getStoreName();
//                    String listID = bean.getId();
//                    String storeAddress = bean.getAddress();
//                    String storeImage = bean.getImageName();
//
//                    Log.e("Shopping List ID:", listID);
//
//                    startActivity(new Intent(getActivity(), StoreImageActivity.class)
//                            .putExtra("listName", listName)
//                            .putExtra("storeName", storeName)
//                            .putExtra("storeAddress", storeAddress)
//                            .putExtra("storeId", storeId)
//                            .putExtra("listId", listID)
//                            .putExtra("storeImage", storeImage));
//                    getActivity().finish();
                    if (mySharedPreference.getPurchased(context, "STORE_LOGO")) {
                        String storeId = bean.getStoreId();
                        String listName = bean.getShopItem();
                        String storeName = bean.getStoreName();
                        String listID = bean.getId();
                        String storeAddress = bean.getAddress();
                        String storeImage = bean.getImageName();

                        Log.e("Shopping List ID:", listID);

                        startActivity(new Intent(getActivity(), StoreImageActivity.class)
                                .putExtra("listName", listName)
                                .putExtra("storeName", storeName)
                                .putExtra("storeAddress", storeAddress)
                                .putExtra("storeId", storeId)
                                .putExtra("listId", listID)
                                .putExtra("storeImage", storeImage));
                        getActivity().finish();
                    } else {
                        openPurchaseDialog();
                    }
                }
            });

            LandingScreen.txtSync.setOnClickListener(v -> {
                if (utils.isNetworkConnected(getActivity())) {
                    listSetupShoppingListsFragment();
                } else {
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
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

    private class onclick implements View.OnClickListener {
        int pos;

        public onclick(int position) {
            this.pos = position;
        }

        @Override
        public void onClick(View view) {
            String id = listBeans.get(pos).getId();
            switch (view.getId()) {
                case R.id.icDelete:
                    AskOption(id);
                    break;

                case R.id.icEdit:
                    openDialog(id, pos);
                    break;

            }
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /**
         * Called when requested PRODUCT ID was successfully purchased
         */
        MySharedPreference.setPurchased(context, "STORE_LOGO",true);
        //always consume made purchase and allow to buy same product multiple times
        bp.consumePurchase(mViewModel.getPRODUCT_SKU());

        showSuccessPurchase();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

        if(bp.loadOwnedPurchasesFromGoogle()){
            // check user is already subscribe or not
            if(bp.isPurchased(mViewModel.getPRODUCT_SKU())){
                /** if already subscribe then we will change the static variable
                 * and call billingrpocessor release() method.
                 * */
                MySharedPreference.setPurchased(context, "STORE_LOGO",true);
                bp.release();
//                iosDialog.cancel();
            }
            else {
                MySharedPreference.setPurchased(context, "STORE_LOGO",false);
//                iosDialog.cancel();
            }
        }

    }

    private void showSuccessPurchase(){
//        new KAlertDialog(context, KAlertDialog.SUCCESS_TYPE)
//                .setTitleText("Congratulations!")
//                .setContentText("Purchase Successfully made! You can add the logos in your shopping list!")
//                .show();
        Toast.makeText(context, " You have already purchased the Logos", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getActivity(), StoreImageActivity.class));

    }

}