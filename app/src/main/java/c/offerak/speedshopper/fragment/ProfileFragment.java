package c.offerak.speedshopper.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import c.offerak.speedshopper.BuildConfig;
import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.reside.ResideMenu;
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

public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    private static int STORAGE_PERMISSION_CODE = 200;
    EditText txtUserName;
    final int PERMISSION_REQUEST_CODE = 101;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    TextView mailId, editProfile, updateProfile;
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

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_activty, container, false);
        mContext = getActivity();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init();
        return rootView;
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
        constraintLayout = rootView.findViewById(R.id.myConstraint);

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
            updateProfile.setVisibility(View.VISIBLE);
        });

        updateProfile.setOnClickListener(v -> {
            txtUserName.setEnabled(false);
            imgCam.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.GONE);

            if (utils.isNetworkConnected(getActivity())) {
                updateProfile();
            } else {
                utils.showSnackBar(constraintLayout, "You are not connected to internet!");
            }
        });

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
    }

    private void updateProfile() {
        utils = new Utils();
        utils.showDialog(getActivity());
        /*MultipartBody.Part body;

        File imageFile;
        if (bitmap != null) {
            File filesDir = getActivity().getFilesDir();
            imageFile = new File(filesDir, "speedShopper" + ".jpg");
            OutputStream os;
            try {
                os = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            body = MultipartBody.Part.createFormData("profile_pic", imageFile.getName(), requestFile);
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            body = MultipartBody.Part.createFormData("profile_pic", "", requestFile);
        }*/

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

                                    mySharedPreference.setLoginDetails(userBean.getUserMail(), dataBean.getName(), dataBean.getPath() + "" + dataBean.getProfile_pic(), userBean.getUserToken(), userBean.getUserId(), "loggedin");
                                    txtUserName.setText(dataBean.getName().toUpperCase());
                                    MySharedPreference.setSharedPreference(mContext, Constants.EMAIL, dataBean.getEmail());
                                    mailId.setText(dataBean.getEmail());
                                    //utils.showSnackBar(constraintLayout, message);
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    //utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), message);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            getActivity().finish();
                                        }
                                    }, 2000);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
}