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
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class ShoppingListsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        context = getActivity();
        init();
        return rootView;
    }

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
        right_hand.setVisibility(View.VISIBLE);

        AdView adView = rootView.findViewById(R.id.ads_view1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(1000); //You can manage the time of the blink with this parameter
//        anim.setStartOffset(200);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        ((TextView) rootView.findViewById(R.id.ctv_header)).startAnimation(anim);

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
        /*FTV = rootView.findViewById(R.id.fadingTextView);
        FTV.setTexts(texts); //You can use an array resource or a string array as the parameter

//For text change once every hour
        FTV.setTimeout(60, MINUTES);

//For text change once every half a minute
        FTV.setTimeout(0.5, MINUTES);

//For text change every 10 seconds
        FTV.setTimeout(10, SECONDS);

//For text change every 500 milliseconds (0.5 seconds)
        FTV.setTimeout(500, MILLISECONDS);*/

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

//        homeList.setOnItemClickListener((parent, view1, position, id) -> {
//            String storeId = listBeans.get(position).getStoreId();
//            String listName = listBeans.get(position).getShopItem();
//            String storeName = listBeans.get(position).getStoreName();
//            String listID = listBeans.get(position).getId();
//            String storeAddress = listBeans.get(position).getAddress();
//
//            startActivity(new Intent(getActivity(), SpeedShoppingActivity.class)
//                    .putExtra("listName", listName)
//                    .putExtra("storeName", storeName)
//                    .putExtra("storeAddress", storeAddress)
//                    .putExtra("storeId", storeId)
//                    .putExtra("listId", listID));
//        });

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
                                    right_hand.setVisibility(View.VISIBLE);
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

    private void openDialog(String id, int pos) {

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
    }

    private class ShoppingListAdapter extends BaseAdapter {

        TextView shopItem, lineView, storeName, storeAddress, counterTextView;
        RelativeLayout counterLayout;
        CardView cardView;
        ImageView icDelete, icEdit, arrow;
        private Context context;
        private List<ShoppingListBean> shoppingListBeans;
        private MySharedPreference mySharedPreference;

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
            shopItem = convertView.findViewById(R.id.listName);
            storeName = convertView.findViewById(R.id.storeName);
            storeAddress = convertView.findViewById(R.id.storeAddress);
            counterLayout = convertView.findViewById(R.id.counter_layout);
            lineView = convertView.findViewById(R.id.lineView);
            counterTextView = convertView.findViewById(R.id.counter_text_view);
            cardView = convertView.findViewById(R.id.cardView);
            icDelete = convertView.findViewById(R.id.icDelete);
            icEdit = convertView.findViewById(R.id.icEdit);
            arrow = convertView.findViewById(R.id.arrow);
            ShoppingListBean bean = shoppingListBeans.get(position);
            shopItem.setText(bean.getShopItem().substring(0, 1).toUpperCase() + bean.getShopItem().substring(1));
            storeName.setText(bean.getStoreName());

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
            if (position % 2 != 0) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAppBlue));
                icDelete.setImageResource(R.mipmap.delete);
                icEdit.setImageResource(R.mipmap.edit);
                lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                shopItem.setTextColor(ContextCompat.getColor(context, R.color.white));
                storeName.setTextColor(ContextCompat.getColor(context, R.color.white));
                storeAddress.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                icDelete.setImageResource(R.mipmap.delete_g);
                icEdit.setImageResource(R.mipmap.edit_g);
                lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                shopItem.setTextColor(ContextCompat.getColor(context, R.color.black));
                storeName.setTextColor(ContextCompat.getColor(context, R.color.black));
                storeAddress.setTextColor(ContextCompat.getColor(context, R.color.black));
            }

            icDelete.setOnClickListener(new onclick(position));
            icEdit.setOnClickListener(new onclick(position));
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            LandingScreen.txtSync.setOnClickListener(v -> {
                if (utils.isNetworkConnected(getActivity())) {
                    listSetupShoppingListsFragment();
                } else {
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
                }
            });
            return convertView;
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
}