package c.offerak.speedshopper.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.bumptech.glide.Glide;
import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.onesignal.OneSignal;
import com.soundcloud.android.crop.Crop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

import c.offerak.speedshopper.BuildConfig;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.MenuActivity;
import c.offerak.speedshopper.activity.StoreImageActivity;
import c.offerak.speedshopper.modal.PurchaseModel;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.reside.ResideMenu;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.response.ProfileResponse;
import c.offerak.speedshopper.response.UpdateProfileResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.ImagePicker;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utility;
import c.offerak.speedshopper.utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;
import static c.offerak.speedshopper.reside.ResideMenu.imgUserImage;
import static c.offerak.speedshopper.reside.ResideMenu.textUserName;

public class ProfileFragment extends Fragment implements BillingProcessor.IBillingHandler {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    private static int STORAGE_PERMISSION_CODE = 200;
    EditText txtUserName;
    final int PERMISSION_REQUEST_CODE = 101;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    TextView mailId, editProfile, updateProfile, upgradeProfile, restore, deleteProfile;
    ImageView imgCam, profileImage, imageView2;
    UserBean userBean;
    ConstraintLayout constraintLayout;
    Bitmap bitmap;
    MySharedPreference mySharedPreference;
    ApiInterface apiService;
    private Utils utils = new Utils();
    Context mContext;
    View rootView;
    File file;
    BillingProcessor bp;

    private PurchaseModel mViewModel;

    Dialog dialog;
    ImageView btn_close, earnedImg;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_activty, container, false);
        mContext = getActivity();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(PurchaseModel.class);
        init();
        initializeBilling();
        return rootView;
    }

    private void initializeBilling(){
        Log.d("KEY--->", mViewModel.getGooglePlayConsolSubscriptionId());
//        bp = new BillingProcessor(mContext, mViewModel.getGooglePlayConsolSubscriptionId(), this);
        bp = BillingProcessor.newBillingProcessor(mContext, mViewModel.getGooglePlayConsolLicenseKey(), this);
        bp.initialize();
    }

    public void init() {
        //footer.setVisibility(View.GONE);
        txtTitle.setText("Profile");
        txtSync.setVisibility(GONE);
        history.setVisibility(View.GONE);

//        ButterKnife.bind(getActivity());

        apiService = ApiClient.getClient().create(ApiInterface.class);
        mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        txtUserName = rootView.findViewById(R.id.txtUserName);
        mailId = rootView.findViewById(R.id.mailId);
        editProfile = rootView.findViewById(R.id.editProfile);
        imgCam = rootView.findViewById(R.id.imgCam);
        profileImage = rootView.findViewById(R.id.profile_image);
        imageView2 = rootView.findViewById(R.id.imageView2);
        updateProfile = rootView.findViewById(R.id.updateProfile);
        deleteProfile = rootView.findViewById(R.id.deleteProfile);
        constraintLayout = rootView.findViewById(R.id.myConstraint);
        upgradeProfile = rootView.findViewById(R.id.upgrade);
//        restore = rootView.findViewById(R.id.restore);

        imgCam.setVisibility(View.INVISIBLE);
        txtUserName.setEnabled(false);
        mailId.setText(MySharedPreference.getSharedPreferences(mContext, Constants.EMAIL));
        txtUserName.setAllCaps(true);
        updateProfile.setVisibility(GONE);

        editProfile.setOnClickListener(v -> {
            imgCam.setVisibility(View.VISIBLE);
            txtUserName.setEnabled(true);
            txtUserName.requestFocus();
            editProfile.setVisibility(GONE);
            upgradeProfile.setVisibility(GONE);
//            restore.setVisibility(GONE);
            updateProfile.setVisibility(View.VISIBLE);
        });

        updateProfile.setOnClickListener(v -> {
            txtUserName.setEnabled(false);
            imgCam.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.VISIBLE);
//            restore.setVisibility(View.VISIBLE);
            upgradeProfile.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.GONE);

            if (utils.isNetworkConnected(getActivity())) {
                updateProfile();
            } else {
                utils.showSnackBar(constraintLayout, "You are not connected to internet!");
            }
        });

        upgradeProfile.setOnClickListener(v -> {
            openDialog();
        });

        deleteProfile.setOnClickListener(v -> {
            deleteProfile();
        });

//        restore.setOnClickListener(v -> {
//
//        });



        imgCam.setOnClickListener(v -> {
            Log.e(TAG, "init: " + "imgCam");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(mContext, WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(mContext, READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {
                    requestCameraPermission();
                }
            } else {
                selectImage();
            }

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE);
                    return;
                }
            }

            try {
                Log.e(TAG, "init: "+"Hello" );
                ImagePicker.pickImage(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Exception>>>>>>>>>>>>", "<<<<<<<<<Handled");
            }*/
        });

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        utils = new Utils();
        if (utils.isNetworkConnected(mContext)) {
            getProfile(userBean);
        } else {
            Toast.makeText(mContext, "You are not connected with internet!", Toast.LENGTH_SHORT).show();
            //utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not connected to internet!");
        }
//            }
//        }, 500);
        OneSignal.addTrigger("profile", "loaded");
    }

    private void openDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.subscription_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        TextView monthly = dialog.findViewById(R.id.sub_monthly);
        TextView thr_months = dialog.findViewById(R.id.three_months);
        TextView six_months = dialog.findViewById(R.id.six_months);
        TextView yearly = dialog.findViewById(R.id.sub_year);
        TextView cancel = dialog.findViewById(R.id.cancel);

        monthly.setOnClickListener(v -> {
            upgradeMembership(v.getId());
            dialog.dismiss();
        });
        thr_months.setOnClickListener(v -> {
            upgradeMembership(v.getId());
            dialog.dismiss();
        });
        six_months.setOnClickListener(v -> {
            upgradeMembership(v.getId());
            dialog.dismiss();
        });
        yearly.setOnClickListener(v -> {
            upgradeMembership(v.getId());
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    private void upgradeMembership(int viewId) {
//        boolean isAvailable = BillingProcessor.isIabServiceAvailable(mContext);
        boolean isAvailable = bp.isSubscriptionUpdateSupported();
        if (isAvailable) {
            switch (viewId)
            {
                case R.id.sub_monthly:
                    bp.subscribe(getActivity(), mViewModel.getPRODUCT_MONTHLY_SKU());
                    break;
                case R.id.three_months:
                    bp.subscribe(getActivity(), mViewModel.getPRODUCT_THREEMONTH_SKU());
                    break;
                case R.id.six_months:
                    bp.subscribe(getActivity(), mViewModel.getPRODUCT_SIXMONTH_SKU());
                    break;
                case R.id.sub_year:
                    bp.subscribe(getActivity(), mViewModel.getPRODUCT_YEARLY_SKU());
                    break;

                default:
                    break;
            }
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "You are not able to subscribe the membership plan!");
        }

    }

    private void updateProfile() {
        utils = new Utils();
        utils.showDialog(getActivity());

        MultipartBody.Part profile_img = null;

        if (file != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            profile_img = MultipartBody.Part.createFormData("profile_pic", file.getName(), reqFile);
        }

        RequestBody nameR = RequestBody.create(MediaType.parse("text/plain"), txtUserName.getText().toString());
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), userBean.getUserToken());

        if (utils.isNetworkConnected(mContext)) {
            Call<UpdateProfileResponse> call = apiService.update(token, nameR, profile_img);
            call.enqueue(new Callback<UpdateProfileResponse>() {
                @Override
                public void onResponse(Call<UpdateProfileResponse> call, retrofit2.Response<UpdateProfileResponse> response) {

                    utils.hideDialog();
                    try {
                        UpdateProfileResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            UpdateProfileResponse.DataBean dataBean = tokenResponse.getData();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    utils.showSnackBar(constraintLayout, tokenResponse.getMessage());
                                    textUserName.setText(dataBean.getName());

                                    String proPic = dataBean.getPath() + "" + dataBean.getProfile_pic();
                                    Glide.with(mContext).load(proPic).into(ResideMenu.imgUserImage);

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
                public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                    utils.hideDialog();
                    utils.showSnackBar(constraintLayout, "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    private void deleteProfile() {
        utils = new Utils();
        utils.showDialog(getActivity());

        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), userBean.getUserToken());

        if (utils.isNetworkConnected(mContext)) {
            Call<GetResponse> call = apiService.update(token);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, retrofit2.Response<GetResponse> response) {
                    utils.hideDialog();
                    try {
                        GetResponse tokenResponse = response.body();
                        if (tokenResponse != null) {
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    utils.showSnackBar(constraintLayout, tokenResponse.getMessage());
                                    if (tokenResponse.getMessage().equals("Account deleted successfully.")) {
                                        logout();
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
                    utils.showSnackBar(constraintLayout, "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void logout() {
        MySharedPreference mySharedPreference = new MySharedPreference(mContext);
        mySharedPreference.clearPreference(mContext);
        mySharedPreference.setLoginDetails("", "", "", "", "", "", "", "");
        startActivity(new Intent(mContext, LoginActivity.class));
        getActivity().finish();
    }

    void getProfile(UserBean bean) {
        if (utils.isNetworkConnected(mContext)) {
            String token = bean.getUserToken();
            Call<ProfileResponse> call = apiService.getProfile(token);
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, retrofit2.Response<ProfileResponse> response) {

                    try {
                        ProfileResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            ProfileResponse.DataBean dataBean = tokenResponse.getData();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    Glide.with(mContext)
                                            .load(dataBean.getPath() + dataBean.getProfile_pic())
                                            .into(profileImage);

                                    Glide.with(mContext)
                                            .load(dataBean.getPath() + dataBean.getProfile_pic())
                                            .into(imageView2);

                                    mySharedPreference.setLoginDetails(userBean.getLogin_num(), userBean.getBalance(), userBean.getUserMail(), dataBean.getName(), dataBean.getPath() + "" + dataBean.getProfile_pic(), userBean.getUserToken(), userBean.getUserId(), "loggedin");
                                    txtUserName.setText(dataBean.getName().toUpperCase());
                                    MySharedPreference.setSharedPreference(mContext, Constants.EMAIL, dataBean.getEmail());
                                    mailId.setText(dataBean.getEmail());
                                    //utils.showSnackBar(constraintLayout, message);
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    getActivity().finish();
//                                    final Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            startActivity(new Intent(getActivity(), LoginActivity.class));
//                                            getActivity().finish();
//                                        }
//                                    }, 2000);
                                } else {
                                    utils.showSnackBar(constraintLayout, tokenResponse.getMessage());
                                }
                                //utils.hideDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    utils.hideDialog();
                    //utils.showSnackBar(constraintLayout, "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_PICK) {
            ImagePicker.beginCrop(getActivity(), resultCode, data);
        } else if (requestCode == ImagePicker.REQUEST_CROP) {
            bitmap = ImagePicker.getImageCropped(getActivity(), resultCode, data,
                    ImagePicker.ResizeType.FIXED_SIZE, 300);

            imgUserImage.setImageBitmap(bitmap);
            profileImage.setImageBitmap(bitmap);
            imageView2.setImageBitmap(bitmap);
            Log.d("", "bitmap picked: " + bitmap);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 200: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        int CAMERA_PERSMISSION = 300;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERSMISSION);
                        return;
                    }
                } else {
                    utils.showSnackBar(constraintLayout, "you just denied the permission");
                }
                break;
            }

            case 300: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(getActivity());
                } else {
                    utils.showSnackBar(constraintLayout, "you just denied the permission");
                }
                break;
            }
        }
    }

    //----------------------select image-------------------------------------------------
    private void selectImage() {

        final CharSequence[] options = {"From Camera", "From Gallery", "Close"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add your photo...");
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

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), CAMERA) | ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(getActivity(), requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            File getImage = getActivity().getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } else {
            /*File getImage = getActivity().getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",
                        new File(getImage.getPath(), "pickImageResult.jpeg"));
            }*/

            File getImage = getActivity().getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",
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
        File pro = new File(Utility.MakeDir(Constants.SDCARD_FOLDER_PATH, mContext), System.currentTimeMillis() + ".jpg");
        Uri destination1 = Uri.fromFile(pro);
        Crop.of(source, destination1).asSquare().withAspect(200, 200).start(getActivity());
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            String imagepath = Crop.getOutput(result).getPath();
            file = new File(imagepath);

//            Glide.with(mContext).load(imagepath).into(imgUserImage);
            Glide.with(mContext).load(imagepath).into(profileImage);
            Glide.with(mContext).load(imagepath).into(imageView2);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(mContext, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {
        MySharedPreference.setPurchased(mContext, productId,true);
        MySharedPreference.setPurchased(mContext, "membership",true);
        //always consume made purchase and allow to buy same product multiple times
        bp.consumePurchaseAsync(productId, new BillingProcessor.IPurchasesResponseListener() {
            @Override
            public void onPurchasesSuccess() {
                if (productId.equals(mViewModel.getPRODUCT_MONTHLY_SKU())) {
                    callAPIRewardMethod("monthly");
                } else if (productId.equals(mViewModel.getPRODUCT_THREEMONTH_SKU())) {
                    callAPIRewardMethod("3months");
                } else if (productId.equals(mViewModel.getPRODUCT_SIXMONTH_SKU())) {
                    callAPIRewardMethod("6months");
                } else if (productId.equals(mViewModel.getPRODUCT_YEARLY_SKU())) {
                    callAPIRewardMethod("yearly");
                }
                utils.showSnackBar(rootView, "You have upgraded premium membership successfully");
            }

            @Override
            public void onPurchasesError() {
                utils.showSnackBar(rootView, "You did not upgrade premium membership");
            }
        });
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        utils.showSnackBar(rootView, "The Billing process failed");
    }

    @Override
    public void onBillingInitialized() {
        bp.loadOwnedPurchasesFromGoogleAsync(new BillingProcessor.IPurchasesResponseListener() {
            @Override
            public void onPurchasesSuccess() {
                if(bp.isSubscribed(mViewModel.getPRODUCT_MONTHLY_SKU())){
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_MONTHLY_SKU(),true);
                    MySharedPreference.setPurchased(mContext, "membership",true);
                    bp.release();
//                    callAPIRewardMethod("monthly");
                    utils.showSnackBar(rootView, "You are already Monthly premium member.");
                }
                else {
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_MONTHLY_SKU(),false);
                }

                if(bp.isSubscribed(mViewModel.getPRODUCT_THREEMONTH_SKU())){
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_THREEMONTH_SKU(),true);
                    MySharedPreference.setPurchased(mContext, "membership",true);
                    bp.release();
//                    callAPIRewardMethod("3months");
                    utils.showSnackBar(rootView, "You are already Three Months premium member.");
                }
                else {
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_THREEMONTH_SKU(),false);
                }

                if(bp.isSubscribed(mViewModel.getPRODUCT_SIXMONTH_SKU())){
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_SIXMONTH_SKU(),true);
                    MySharedPreference.setPurchased(mContext, "membership",true);
                    bp.release();
//                    callAPIRewardMethod("6months");
                    utils.showSnackBar(rootView, "You are already Six Months premium member.");
                }
                else {
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_SIXMONTH_SKU(),false);
                }

                if(bp.isSubscribed(mViewModel.getPRODUCT_YEARLY_SKU())){
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_YEARLY_SKU(),true);
                    MySharedPreference.setPurchased(mContext, "membership",true);
                    bp.release();
//                    callAPIRewardMethod("yearly");
                    utils.showSnackBar(rootView, "You are already Yearly premium member.");
                }
                else {
                    MySharedPreference.setPurchased(mContext, mViewModel.getPRODUCT_YEARLY_SKU(),false);
                }
            }

            @Override
            public void onPurchasesError() {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("profile", "loaded");
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public void callAPIRewardMethod(String key) {
        utils.showDialog(mContext);
        Log.e("~~~~", userBean.getUserToken());
        if(utils.isNetworkConnected(mContext)) {
            Call<GetResponse> call = apiService.getReward(userBean.getUserToken(), key);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(@NonNull Call<GetResponse> call, @NonNull Response<GetResponse> response) {

                    GetResponse resp = response.body();
                    try {
                        if (resp != null) {

                            if (resp.getStatus() == 200) {
                                utils.hideDialog();
                                showDialogReward();
                            } else {
                                utils.hideDialog();
                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), "There are no any reward for Store Image purchase");

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetResponse> call, Throwable t) {
                    utils.hideDialog();
                    Log.d("", "onResponse: ");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    public void showDialogReward() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_earned);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.transparent)));
        earnedImg = dialog.findViewById(R.id.earned_image);
        btn_close = dialog.findViewById(R.id.close_btn);
        Glide.with(this).load(R.drawable.earned).into(earnedImg);
        btn_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}