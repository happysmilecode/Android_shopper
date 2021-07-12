package c.offerak.speedshopper.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.onesignal.OneSignal;
import com.soundcloud.android.crop.Crop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import c.offerak.speedshopper.BuildConfig;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.adapter.LocationAdapter;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.SpeedAvailableItemResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.ImagePicker;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.ProgressHUD;
import c.offerak.speedshopper.utils.Utility;
import c.offerak.speedshopper.utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static c.offerak.speedshopper.activity.LandingScreen.lat;
import static c.offerak.speedshopper.activity.LandingScreen.lng;

public class ItemEditActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    @BindView(R.id.item_name) public TextView itemNameTextView;
    @BindView(R.id.minus_button) public Button minus_button;
    @BindView(R.id.plus_button) public Button plus_button;
    @BindView(R.id.quantityText) public TextView quantityTextView;
    @BindView(R.id.edtUserName) public EditText itemPrice;
    @BindView(R.id.select_aisle) public TextView selectAisleTextView;
    @BindView(R.id.done_button) public ImageButton doneButton;
    @BindView(R.id.photoTakeView) public RelativeLayout photo_takeView;
    @BindView(R.id.itemImageView) public RelativeLayout item_imageView;
    @BindView(R.id.itemImage) public ImageView itemImage;
    @BindView(R.id.retake_photo) public ImageButton retake_photo;
    @BindView(R.id.take_photo) public TextView take_photo;
    @BindView(R.id.remove_photo) public ImageButton remove_photo;

    EditText cet_search;
    RecyclerView recyclerView;
    ImageView locationDialogCross;

    LocationAdapter locationAdapter;
    int itemQuantity, m_pos;
    float unit_price;
    ProgressHUD progressHUD;
    public ImageView icBackButton, toggleButton;
    public Utils utils = new Utils();
    public ApiInterface apiService;
    public UserBean userBean;
    MySharedPreference mySharedPreference;
    public String listId, storeId, item_id, m_id, aisleLocation, imageName, itemName;
    public ArrayList<String> aisleList=new ArrayList<>();
    public ArrayList<String> aislePositionList=new ArrayList<>();
    public Boolean isLocated = false;
    Dialog locationDialog;
    final int PERMISSION_REQUEST_CODE = 101;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    public boolean isUpgraded = false;
    File file;

//    public RelativeLayout item_imageView, photo_takeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        context = this;
        ButterKnife.bind(this, this);
        init();
    }

    public void init() {
        isUpgraded = MySharedPreference.getPurchased(context, "membership");
//        isUpgraded = true;
        getDataFromIntent();
        initLayout();
        initVariables();
        initClickEvents();
        OneSignal.addTrigger("itemEdit", "loaded");
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
        imageName = intent.getStringExtra("item_image");
        Log.e("ITEM_IMAGE--->", imageName);
        itemName = intent.getStringExtra("item_name");
        aisleLocation = intent.getStringExtra("aisle_number");
        itemQuantity = Integer.parseInt(intent.getStringExtra("item_quantity"));
        unit_price = Float.parseFloat(intent.getStringExtra("item_price"));
        m_pos = intent.getIntExtra("m_pos", 0);
        aisleList = intent.getStringArrayListExtra("aisle_list");
        aislePositionList = aisleList;

        isLocated = !aisleLocation.equals("");
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

    public void locationAdd(String address) {
        if(utils.isNetworkConnected(context)) {
            progressHUD = ProgressHUD.show(context, "Loading", true, false, null);
            Call<SpeedAvailableItemResponse> call;
            if (isUpgraded) {
                if (isLocated) {
                    call = apiService.updateItemLocationPro(userBean.getUserToken(), item_id, lat, storeId, address, lng, listId);
                } else {
                    call = apiService.addItemLocationPro(userBean.getUserToken(), item_id, lat, storeId, address, lng, listId);
                }

            } else {
                if (isLocated) {
                    call = apiService.updateItemLocation(userBean.getUserToken(), item_id, lat, storeId, address, lng, listId);
                } else {
                    call = apiService.addItemLocation(userBean.getUserToken(), item_id, lat, storeId, address, lng, listId);
                }
            }

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

    public void removeItemImage() {
        utils = new Utils();
        String token = userBean.getUserToken();

        if(utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.removeItemImage(token, m_id, imageName);
            utils.showDialog(ItemEditActivity.this);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(@NotNull Call<GetResponse> call, @NotNull retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    try {
                        GetResponse myResponse = response.body();
                        if (myResponse != null && myResponse.getStatus() == 200) {
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), myResponse.getMessage());
                            itemImage.setImageResource(R.drawable.menu_bg2);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) doneButton.getLayoutParams();
                            photo_takeView.setVisibility(View.VISIBLE);
                            item_imageView.setVisibility(View.GONE);
                            lp.addRule(RelativeLayout.BELOW, photo_takeView.getId());
                            doneButton.setLayoutParams(lp);
                        } else {
                            assert myResponse != null;
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), myResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<GetResponse> call, @NotNull Throwable t) {
                    progressHUD.dismiss();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to remove the Image of Item!");
                }
            });
        } else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }


    public void updateQuantityAndImage() {
        utils = new Utils();
//        utils.showDialog(this);

        MultipartBody.Part item_img = null;

        if (file != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            item_img = MultipartBody.Part.createFormData("item_pic", file.getName(), reqFile);
        }

        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), quantityTextView.getText().toString());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), itemPrice.getText().toString());
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), userBean.getUserToken());
        RequestBody listitem_id = RequestBody.create(MediaType.parse("text/plain"), m_id);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), item_id);
        String userToken = userBean.getUserToken();


        if(utils.isNetworkConnected(context)) {
            Call<GetResponse> call = apiService.updateItemQuantity(userToken, listitem_id, quantity, price, id, item_img);
//            progressHUD = ProgressHUD.show(context, "Loading", true, false, null);
            utils.showDialog(ItemEditActivity.this);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    try {
                        GetResponse myResponse = response.body();
                        if (myResponse != null && myResponse.getStatus() == 200) {
                            itemQuantity = Integer.parseInt(quantityTextView.getText().toString());
                            utils.showSnackBar(getWindow().getDecorView().getRootView(), myResponse.getMessage());
                            Intent retData = new Intent();
                            retData.putExtra("ITEM_QUANTITY", itemQuantity);
                            retData.putExtra("ITEM_PRICE", Float.parseFloat(itemPrice.getText().toString()));
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
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Failed to update the item!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }
    void initLayout() {
        itemNameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));
//        assert itemPrice != null;
        itemPrice.setText(String.valueOf(unit_price));
        selectAisleTextView.setText(aisleLocation);
        // Hide and show menu items
        icBackButton = findViewById(R.id.backButton);
        toggleButton = findViewById(R.id.toggleButton);
        icBackButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);
        setEnabledForMinusBtn(itemQuantity);
//        itemPrice.setFocusable(false);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) doneButton.getLayoutParams();
        if (isUpgraded) {
            if (!imageName.equals("")) {
                Glide.with(context)
                        .load(Constants.ITEM_IMAGE_URL + imageName)
                        .into(itemImage);
                photo_takeView.setVisibility(View.GONE);
                item_imageView.setVisibility(View.VISIBLE);
                lp.addRule(RelativeLayout.BELOW, item_imageView.getId());
                doneButton.setLayoutParams(lp);
            } else {
                photo_takeView.setVisibility(View.VISIBLE);
                item_imageView.setVisibility(View.GONE);
                lp.addRule(RelativeLayout.BELOW, photo_takeView.getId());
                doneButton.setLayoutParams(lp);
            }
        } else {
            photo_takeView.setVisibility(View.GONE);
            item_imageView.setVisibility(View.GONE);
        }

    }

    void initClickEvents() {
        icBackButton.setOnClickListener(v -> {
            Intent retData = new Intent();
            retData.putExtra("ITEM_QUANTITY", itemQuantity);
            retData.putExtra("ITEM_LOCATION", aisleLocation);
            retData.putExtra("ITEM_PRICE", Float.parseFloat(itemPrice.getText().toString()));
            retData.putExtra("m_pos", m_pos);
            setResult(Activity.RESULT_OK, retData);
            finish();
        });
        minus_button.setOnClickListener(this);
        plus_button.setOnClickListener(this);
        selectAisleTextView.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        take_photo.setOnClickListener(this);
        itemPrice.setOnClickListener(this);
        retake_photo.setOnClickListener(this);
        remove_photo.setOnClickListener(this);
    }

    void updateQuantityTextfield(Boolean isAdd) {
        int quantity = Integer.parseInt(quantityTextView.getText().toString());
        quantity = isAdd ? quantity + 1 : quantity - 1;
        setEnabledForMinusBtn(quantity);
        quantityTextView.setText(String.valueOf(quantity));
        itemQuantity = quantity;
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
                if (utils.isNetworkConnected(this)) {
                    updateQuantityAndImage();
                } else {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "You are not connected to internet!");
                }
                break;
            case R.id.retake_photo:
            case R.id.take_photo:
                taekPhoto();
                break;
            case R.id.edtUserName:
                itemPrice.setFocusable(true);
                itemPrice.setText("");
                break;
            case R.id.remove_photo:
                removeItemImage();
                break;

        }
    }

    public void taekPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                requestCameraPermission();
            }
        } else {
            selectImage();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA) | ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions((Activity) context, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case 200: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (context.checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        int CAMERA_PERSMISSION = 300;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERSMISSION);
                        return;
                    }
                } else {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "you just denied the permission");
                }
                break;
            }

            case 300: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(this);
                } else {
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "you just denied the permission");
                }
                break;
            }
        }
    }

    //----------------------select image-------------------------------------------------
    private void selectImage() {

        final CharSequence[] options = {"From Camera", "From Gallery", "Close"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Add a photo for Item...");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("From Camera")) {
                    Uri outputFileUri = getCaptureImageOutputUri();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Close")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            File getImage = this.getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } else {
            /*File getImage = getActivity().getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",
                        new File(getImage.getPath(), "pickImageResult.jpeg"));
            }*/

            File getImage = this.getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                        new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private void beginCrop(Uri source) {
        File pro = new File(Utility.MakeDir(Constants.SDCARD_FOLDER_PATH, context), System.currentTimeMillis() + ".jpg");
        Uri destination1 = Uri.fromFile(pro);
        Crop.of(source, destination1).asSquare().withAspect(200, 200).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            String imagepath = Crop.getOutput(result).getPath();
            file = new File(imagepath);

            photo_takeView.setVisibility(View.GONE);
            item_imageView.setVisibility(View.VISIBLE);

            Glide.with(context).load(imagepath).into(itemImage);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) doneButton.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, item_imageView.getId());
            doneButton.setLayoutParams(lp);



        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri imageUri = getPickImageResultUri(data);
                beginCrop(imageUri);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
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
        OneSignal.addTrigger("itemEdit", "loaded");
    }
}
