package c.offerak.speedshopper.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.onesignal.OneSignal;

import java.util.EnumMap;
import java.util.Map;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.activity.TransactionHistoryActivity;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.ProfileResponse;
import c.offerak.speedshopper.response.WalletResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.rest.Constants;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.CLIPBOARD_SERVICE;
import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

public class WalletFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = WalletFragment.class.getSimpleName();
    Context context;
    View rootView;
    Button btn_deposit, btn_sstx, btn_eth;
    ImageView qr_address, copy, copy_priate, cross;
    Dialog dialog, dialog1;
    private UserBean userBean;
    private MySharedPreference mySharedPreference;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private String balance = "", ethtoken = "", address = "", private_address = "";
    private TextView txtBalance, ctv_address, ctv_pri_address, ctv_btn_yes, textView2, textView;
    private Bitmap qrImage;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        txtBalance = rootView.findViewById(R.id.txtBalance);
        textView = rootView.findViewById(R.id.textView);
        textView2 = rootView.findViewById(R.id.textView2);
        btn_deposit = rootView.findViewById(R.id.btn_deposit);
        btn_sstx = rootView.findViewById(R.id.btn_sstx);
        btn_eth = rootView.findViewById(R.id.btn_eth);
        profileImage = rootView.findViewById(R.id.profile_image);
        Glide.with(this).load(R.drawable.money).into(profileImage);

        txtTitle.setText(R.string.wallet);
        txtSync.setVisibility(View.GONE);
        history.setVisibility(View.VISIBLE);
        btn_sstx.setSelected(true);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        history.setOnClickListener(this);
        btn_sstx.setOnClickListener(this);
        btn_eth.setOnClickListener(this);
        btn_deposit.setOnClickListener(this);

//        getWalletDetials();
        getWallet();
        OneSignal.addTrigger("wallet", "loaded");
    }

    public void getWallet() {
        utils.showDialog(context);
        if (utils.isNetworkConnected(context)) {
            String token = userBean.getUserToken();
            Call<ProfileResponse> call = apiService.getProfile(token);
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, retrofit2.Response<ProfileResponse> response) {

                    try {
                        ProfileResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            ProfileResponse.DataBean dataBean = tokenResponse.getData();
                            utils.hideDialog();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    txtBalance.setText(dataBean.getBalance());

                                    mySharedPreference.setLoginDetails(dataBean.getLogin_num(), dataBean.getBalance(), dataBean.getEmail(), dataBean.getName(), dataBean.getPath() + "" + dataBean.getProfile_pic(), dataBean.getToken(), dataBean.getId(), "loggedin");

                                    MySharedPreference.setSharedPreference(context, Constants.EMAIL, dataBean.getEmail());

                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());

                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    getActivity().finish();
                                } else {
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
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

    public void getWalletDetials() {
        if (utils.isNetworkConnected(context)) {
            String token = userBean.getUserToken();
            Log.e(TAG, "Token: " + token);
            Call<WalletResponse> call = apiService.getWallet(token);
            call.enqueue(new Callback<WalletResponse>() {
                @Override
                public void onResponse(Call<WalletResponse> call, retrofit2.Response<WalletResponse> response) {

                    try {
                        WalletResponse tokenResponse = response.body();
                        if (tokenResponse != null) {

                            WalletResponse.DataBean dataBean = tokenResponse.getData();
                            try {
                                if (tokenResponse.getStatus() == 200) {
                                    balance = dataBean.getEthToken();
                                    ethtoken = dataBean.getEthBalance();
                                    address = dataBean.getAddress();
                                    private_address = dataBean.getPrivate_address();
                                    txtBalance.setText(dataBean.getEthToken());
                                } else if (tokenResponse.getMessage().equals("Session expired")) {
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            getActivity().finish();
                                        }
                                    }, 2000);
                                } else {
                                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), tokenResponse.getMessage());
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
                public void onFailure(Call<WalletResponse> call, Throwable t) {
//                utils.hideDialog();
                    Log.e(TAG, "onFailure: " + t.toString());
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                            "Please check your internet connection!");
                }
            });
        } else {
            utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history:
                startActivity(new Intent(context, TransactionHistoryActivity.class));
                break;

            case R.id.btn_sstx:
                textView.setText(R.string.sstx);
                textView2.setText(R.string.money);
                txtBalance.setText(balance);
                btn_sstx.setSelected(true);
                btn_eth.setSelected(false);
                break;

            case R.id.btn_eth:
                textView.setText(R.string.eth);
                textView2.setText(R.string.token);
                txtBalance.setText(ethtoken);
                btn_eth.setSelected(true);
                btn_sstx.setSelected(false);
                break;

            case R.id.btn_deposit:
                if (address.isEmpty() || private_address.isEmpty()) {
                    Toast.makeText(context, "Address Not found", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogQRCodeAddress();
                }
                break;

            case R.id.cross:
                dialog.dismiss();
                break;

            case R.id.copy:
                if (ctv_address.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Address Not found", Toast.LENGTH_SHORT).show();
                } else {
                    myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                    myClip = ClipData.newPlainText("Address", ctv_address.getText().toString());
                    myClipboard.setPrimaryClip(myClip);
                    showDialogBuy();
                }
                break;
            case R.id.private_copy:
                if (ctv_pri_address.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Address Not found", Toast.LENGTH_SHORT).show();
                } else {
                    myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                    myClip = ClipData.newPlainText("Address", ctv_pri_address.getText().toString());
                    myClipboard.setPrimaryClip(myClip);
                    showDialogBuy();
                }
                break;

            case R.id.btn_yes:
                dialog1.dismiss();
                break;
        }
    }

    //----------------------- Buy Dialog -------------------------------
    public void showDialogBuy() {
        dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.dialog_qr_code_success);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog1.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        ctv_btn_yes = dialog1.findViewById(R.id.btn_yes);
        ctv_btn_yes.setOnClickListener(this);
        dialog1.show();
    }

    //---------------- QR Code Dialog --------------------
    public void showDialogQRCodeAddress() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_qr_address);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        qr_address = dialog.findViewById(R.id.qr_address);
        ctv_address = dialog.findViewById(R.id.address);
        ctv_address.setText(address);
        RelativeLayout ral_private = dialog.findViewById(R.id.ral_private);
        ctv_pri_address = dialog.findViewById(R.id.private_address);
        ctv_pri_address.setText(private_address);
        copy = dialog.findViewById(R.id.copy);
        copy_priate = dialog.findViewById(R.id.private_copy);
        cross = dialog.findViewById(R.id.cross);
        if (private_address.isEmpty())
            ral_private.setVisibility(View.GONE);
        generateImage();
        copy.setOnClickListener(this);
        cross.setOnClickListener(this);
        copy_priate.setOnClickListener(this);
        dialog.show();
    }

    private void generateImage() {

        if (address.trim().isEmpty()) {
            alert("code");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
//                int size = qr_address.getMeasuredWidth();
                int size = 500;
                if (size > 1) {
                    Log.e(TAG, "size is set manually");
                    size = 500;
                }

                Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
                hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                hintMap.put(EncodeHintType.MARGIN, 1);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try {
                    BitMatrix byteMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, size,
                            size, hintMap);
                    int height = byteMatrix.getHeight();
                    int width = byteMatrix.getWidth();
                    qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            qrImage.setPixel(x, y, byteMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImage(qrImage);
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                    alert(e.getMessage());
                }
            }
        }).start();
    }

    private void showImage(Bitmap bitmap) {
        if (bitmap == null) {
            qr_address.setImageResource(android.R.color.transparent);
            qrImage = null;
            //txtSave_Hint.setVisibility(View.GONE);
        } else {
            qr_address.setImageBitmap(bitmap);
            //txtSave_Hint.setVisibility(View.VISIBLE);
        }
    }

    private void alert(String message) {
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle("QRCode Generator")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        OneSignal.addTrigger("wallet", "loaded");
    }
}
