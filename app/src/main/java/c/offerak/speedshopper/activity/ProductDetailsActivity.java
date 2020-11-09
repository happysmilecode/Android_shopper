package c.offerak.speedshopper.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ProductDetailsActivity.class.getSimpleName();
    Context context;
    private ImageView imgProduct;
    private TextView txtTitle, txtProductName, txtProductPrice, txtProductDiscount, txtDescription, btn_yes, btn_cancel, txtWebsite;
    private ImageView toggleButton, backButton;
    private ImageView btn_buy;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    String store_id, product_id;
    Dialog dialog;
    private AlertView mAlertView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        context = this;
        init();
    }

    public void init() {
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtProductDiscount = findViewById(R.id.txtProductDiscount);
        txtWebsite = findViewById(R.id.txtWebsite);
        Linkify.addLinks(txtWebsite, Linkify.ALL);
        txtDescription = findViewById(R.id.txtDescriptionD);
        txtTitle = findViewById(R.id.txtTitle);
        toggleButton = findViewById(R.id.toggleButton);
        backButton = findViewById(R.id.backButton);
        btn_buy = findViewById(R.id.btn_buy);

        backButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);
        txtDescription.setMovementMethod(new ScrollingMovementMethod());
        txtTitle.setText("Product Detail");

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(context);
        userBean = mySharedPreference.getLoginDetails();

        Intent intent = getIntent();
        if (intent.hasExtra("productName")) {
            txtProductName.setText(intent.getStringExtra("productName"));
        }

        if (intent.hasExtra("productPrice")) {
            txtProductPrice.setText("SSTX - " + intent.getStringExtra("productPrice"));
        }

        if (intent.hasExtra("productDiscount")) {
            txtProductDiscount.setText(intent.getStringExtra("productDiscount") + "% Off");
        }

        if (intent.hasExtra("productWebsite")) {
            txtWebsite.setText(intent.getStringExtra("productWebsite"));
        }

        if (intent.hasExtra("productDescription")) {
            txtDescription.setText(intent.getStringExtra("productDescription"));
        }

        if (intent.hasExtra("productImage")) {
            Glide.with(context).load(intent.getStringExtra("productImage")).into(imgProduct);
        }

        if (intent.hasExtra("store_id")) {
            store_id = intent.getStringExtra("store_id");
        }

        if (intent.hasExtra("product_id")) {
            product_id = intent.getStringExtra("product_id");
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_buy:
                showDialogBuy();
                break;

            case R.id.btn_yes:
                callBuyAPI();
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
        }
    }

    //------------------------ Buy ----------------------------
    public void callBuyAPI() {
        if(utils.isNetworkConnected(context)) {
            utils.showDialog(context);
            Log.e(TAG, "TOKEN: " + userBean.getUserToken());
            Call<GetResponse> call = apiService.getBuyAPI(userBean.getUserToken(),
                    userBean.getUserId(),
                    store_id,
                    product_id);
            call.enqueue(new Callback<GetResponse>() {
                @Override
                public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                    Log.e(TAG, "onResponse: " + response);
                    try {
                        GetResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            utils.hideDialog();
                            try {
                                if (loginResponse.getStatus() == 200) {
                                    dialog.dismiss();
                                    mAlertView = new AlertView("Speed Shopper", loginResponse.getMessage(), "OK", null, null, context, AlertView.Style.Alert,
                                            new OnItemClickListener(){
                                                public void onItemClick(Object o,int position){
                                                }
                                            }
                                    );
                                    mAlertView.show();
                                } else {
                                    dialog.dismiss();

                                    mAlertView = new AlertView("Speed Shopper", loginResponse.getMessage(), "OK", null, null, context, AlertView.Style.Alert,
                                        new OnItemClickListener(){
                                            public void onItemClick(Object o,int position){
                                            }
                                        }
                                    );
                                    mAlertView.show();
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
                    Log.e(TAG, "onFailure: " + t.toString());
                    dialog.dismiss();
                    utils.hideDialog();
                    utils.showSnackBar(getWindow().getDecorView().getRootView(), "Please check your internet connection!");
                }
            });
        }else {
            utils.showSnackBar(getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
        }
    }

    //----------------------- Buy Dialog -------------------------------
    public void showDialogBuy() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_buy);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        btn_yes = dialog.findViewById(R.id.btn_yes);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_yes.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        dialog.show();
    }
}
