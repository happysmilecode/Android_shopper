package c.offerak.speedshopper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LandingScreen;
import c.offerak.speedshopper.modal.PurchaseModel;

public class PremadeFragment extends Fragment {

    Context mContext;
    View rootView;
    public PremadeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_premade, container, false);
        mContext = getActivity();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();
        initializeBilling();
        return rootView;
    }

    public void init() {

        LandingScreen.txtTitle.setText(R.string.pre_made);
        LandingScreen.history.setVisibility(View.GONE);
        LandingScreen.txtSync.setVisibility(View.VISIBLE);
    }

    private void initializeBilling() {

    }
}