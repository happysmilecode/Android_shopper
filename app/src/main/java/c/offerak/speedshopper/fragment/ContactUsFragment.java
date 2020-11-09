package c.offerak.speedshopper.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.modal.UserBean;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiClient;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.MySharedPreference;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;

import static c.offerak.speedshopper.activity.LandingScreen.history;
import static c.offerak.speedshopper.activity.LandingScreen.txtSync;
import static c.offerak.speedshopper.activity.LandingScreen.txtTitle;

public class ContactUsFragment extends Fragment {

    public static final String TAG = ContactUsFragment.class.getSimpleName();
    Context context;
    private TextView submitButton;
    private ApiInterface apiService;
    private Utils utils = new Utils();
    private UserBean userBean;
    private EditText inputType;
    private TextView inpText;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact_us_layout, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        submitButton = rootView.findViewById(R.id.btnSubmit);
        inputType = rootView.findViewById(R.id.inputType);
        inpText = rootView.findViewById(R.id.inpText);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans_Semibold.ttf");
        inpText.setTypeface(custom_font);

        txtTitle.setText(R.string.contact_us_small);
        txtSync.setVisibility(View.GONE);
        history.setVisibility(View.GONE);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        MySharedPreference mySharedPreference = new MySharedPreference(getActivity());
        userBean = mySharedPreference.getLoginDetails();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utils.isNetworkConnected(context)) {
                    utils.hideKeyboard(getActivity());
                    String msg = inputType.getText().toString().trim();

                    if (!msg.equals("")) {
                        utils.showDialog(getActivity());
                        Call<GetResponse> call = apiService.contact(userBean.getUserToken(), msg);
                        call.enqueue(new Callback<GetResponse>() {
                            @Override
                            public void onResponse(Call<GetResponse> call,
                                                   retrofit2.Response<GetResponse> response) {
                                try {
                                    GetResponse tokenResponse = response.body();
                                    if (tokenResponse != null) {
                                        try {
                                            if (tokenResponse.getStatus() == 200) {
                                                inputType.setText("");
                                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                                        tokenResponse.getMessage());
                                            } else if (tokenResponse.getStatus() == 401) {
                                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                                        tokenResponse.getMessage());

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                                        getActivity().finish();
                                                    }
                                                }, 2000);
                                            } else {
                                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                                        tokenResponse.getMessage());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    utils.hideDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<GetResponse> call, Throwable t) {
                                utils.hideDialog();
                                utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(),
                                        "Please check your internet connection!");
                            }
                        });
                    }
                } else {
                    utils.showSnackBar(getActivity().getWindow().getDecorView().getRootView(), getString(R.string.not_connected_to_internet));
                }
            }
        });
    }
}
