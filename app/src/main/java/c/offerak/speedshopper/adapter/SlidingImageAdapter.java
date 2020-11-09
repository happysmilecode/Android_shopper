package c.offerak.speedshopper.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;
import c.offerak.speedshopper.activity.LoginActivity;
import c.offerak.speedshopper.response.AdvertisementListByMerchantIdResponse;
import c.offerak.speedshopper.response.GetResponse;
import c.offerak.speedshopper.rest.ApiInterface;
import c.offerak.speedshopper.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlidingImageAdapter extends PagerAdapter {

    private static final String TAG = SlidingImageAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private List<AdvertisementListByMerchantIdResponse.DataBean> IMAGES = new ArrayList<>();
    private Utils utils = new Utils();
    private String token;
    private ApiInterface apiService;

    public SlidingImageAdapter(Context context, ApiInterface apiService, String token, List<AdvertisementListByMerchantIdResponse.DataBean> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        this.token = token;
        this.apiService = apiService;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);
        final TextView title = imageLayout.findViewById(R.id.title);

        AdvertisementListByMerchantIdResponse.DataBean dataBean = IMAGES.get(position);

        title.setText(dataBean.getName());
        Glide.with(context).load(dataBean.getImage()).into(imageView);

        imageView.setOnClickListener(v -> {
            String advLink = dataBean.getAdv_link();
            if (!advLink.equals("")) {
                if (!advLink.startsWith("http://") && !advLink.startsWith("https://")) {
                    advLink = "http://" + advLink;
                }


                Call<GetResponse> call = apiService.advertisementIncreaseClickCountAPI(token, dataBean.getAdv_id());
                call.enqueue(new Callback<GetResponse>() {
                    @Override
                    public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                    }

                    @Override
                    public void onFailure(Call<GetResponse> call, Throwable t) {
                    }
                });

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(advLink));
                context.startActivity(browserIntent);
            }
        });
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}